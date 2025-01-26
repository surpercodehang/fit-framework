/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.app;

import static java.nio.charset.StandardCharsets.UTF_8;

import modelengine.fitframework.build.support.AbstractRepackager;
import modelengine.fitframework.build.util.ArtifactDownloader;
import modelengine.fitframework.build.util.JarPackager;
import modelengine.fitframework.jvm.classfile.AccessFlag;
import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.classfile.MethodInfo;
import modelengine.fitframework.jvm.classfile.constant.ClassInfo;
import modelengine.fitframework.jvm.classfile.constant.Utf8Info;
import modelengine.fitframework.jvm.classfile.descriptor.ClassDescriptor;
import modelengine.fitframework.jvm.classfile.descriptor.MethodDescriptor;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.launch.AggregatedFitLauncher;
import modelengine.fitframework.maven.MavenCoordinate;
import modelengine.fitframework.plugin.maven.support.SharedDependency;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.util.ClassUtils;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.XmlUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 为应用程序提供打包程序。
 *
 * @author 梁济时
 * @since 2023-02-02
 */
public final class AppRepackager extends AbstractRepackager {
    private static final String MAIN_METHOD_NAME = "main";
    private static final String MAIN_METHOD_DESCRIPTOR =
            MethodDescriptor.create(Collections.singletonList(ClassDescriptor.of(String[].class)),
                    ClassDescriptor.of(void.class)).toString();
    private static final int MAIN_METHOD_ACCESS_FLAGS = Stream.of(AccessFlag.ACC_PUBLIC, AccessFlag.ACC_STATIC)
            .map(AccessFlag::value)
            .mapToInt(U2::intValue)
            .reduce(0, (v1, v2) -> v1 | v2);

    private static final String MANIFEST_MAIN_CLASS_KEY = "Main-Class";
    private static final String MANIFEST_BUILT_BY_KEY = "Built-By";
    private static final String MANIFEST_BUILT_BY_VALUE = "FIT Application Packager (Huawei)";

    private static final String METADATA_FILE = FIT_ROOT_DIRECTORY + PATH_SEPARATOR + "metadata.xml";

    private final ArtifactDownloader downloader;
    private final DependencyNode rootDependency;

    /**
     * 重新打包应用。
     *
     * @param project 表示 Maven 项目的 {@link MavenProject}。
     * @param log 表示 Maven 日志的 {@link Log}。
     * @param downloader 表示下载器的 {@link ArtifactDownloader}。
     * @param rootDependency 表示根依赖的 {@link DependencyNode}。
     * @param sharedDependencies 表示共享依赖的 {@link List}{@code <}{@link SharedDependency}{@code >}。
     */
    public AppRepackager(MavenProject project, Log log, ArtifactDownloader downloader, DependencyNode rootDependency,
            List<SharedDependency> sharedDependencies) {
        super(project, log, sharedDependencies);
        this.downloader = downloader;
        this.rootDependency = rootDependency;
    }

    /**
     * 重新打包应用。
     *
     * @throws MojoExecutionException 当重新打包过程中发生异常时。
     */
    public void repackage() throws MojoExecutionException {
        File target = this.project().getArtifact().getFile();
        File backup = this.backupFile(target);
        Set<Artifact> dependencies = this.collectDependencies();
        List<Artifact> baseArtifacts = prepareBaseArtifacts(dependencies, this.downloader);
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(target.toPath()), UTF_8)) {
            JarPackager packager = JarPackager.of(out);
            this.packageOriginalJar(packager, backup);
            this.packageLauncher(packager);
            this.packageBaseArtifacts(packager, baseArtifacts);
            for (Artifact dependency : dependencies) {
                this.packageDependency(packager, dependency);
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to repackage application JAR. [file={0}]",
                    FileUtils.path(target)), ex);
        }
    }

    private Set<Artifact> collectDependencies() throws MojoExecutionException {
        Queue<DependencyNode> queue = new LinkedList<>(this.rootDependency.getChildren());
        Set<Artifact> dependencies = new HashSet<>();
        while (!queue.isEmpty()) {
            DependencyNode node = queue.poll();
            Artifact dependency = node.getArtifact();
            if (!StringUtils.equalsIgnoreCase(dependency.getType(), "jar")) {
                queue.addAll(node.getChildren());
                continue;
            }
            if (dependency.getFile() == null) {
                final Artifact current = dependency;
                Artifact found = this.project()
                        .getArtifacts()
                        .stream()
                        .filter(artifact -> Objects.equals(artifact.getGroupId(), current.getGroupId()))
                        .filter(artifact -> Objects.equals(artifact.getArtifactId(), current.getArtifactId()))
                        .findAny()
                        .orElse(null);
                if (found != null) {
                    dependency = found;
                }
            }
            if (dependency.getFile() == null) {
                dependency = this.download(dependency);
            }
            dependencies.add(dependency);
            if (!isPlugin(dependency)) {
                queue.addAll(node.getChildren());
            }
        }
        return dependencies;
    }

    private void packageOriginalJar(JarPackager packager, File file) throws MojoExecutionException {
        Jar jar = loadJar(file);
        this.log().info(StringUtils.format("Prepare to repackage original jar. [from={0}]", file.getName()));
        int count = 0;
        for (Jar.Entry entry : jar.entries()) {
            if (entry.directory()) {
                continue;
            }
            if (StringUtils.equalsIgnoreCase(entry.name(), AggregatedFitLauncher.MANIFEST_ENTRY_NAME)) {
                Manifest manifest;
                try (InputStream in = entry.read()) {
                    manifest = new Manifest(in);
                } catch (IOException ex) {
                    throw new MojoExecutionException(StringUtils.format(
                            "Failed to load manifest from entry in JAR. [file={0}, entry={1}]",
                            FileUtils.path(file),
                            entry.name()), ex);
                }
                manifest.getMainAttributes().putValue(MANIFEST_BUILT_BY_KEY, MANIFEST_BUILT_BY_VALUE);
                manifest.getMainAttributes()
                        .putValue(AggregatedFitLauncher.MANIFEST_ENTRY_CLASS_KEY, lookupEntryClass(jar));
                manifest.getMainAttributes().putValue(MANIFEST_MAIN_CLASS_KEY, AggregatedFitLauncher.class.getName());
                ZipEntry target = new ZipEntry(entry.name());
                packager.ensureDirectory(target.getName());
                try {
                    packager.out().putNextEntry(target);
                    manifest.write(packager.out());
                    packager.out().closeEntry();
                } catch (IOException ex) {
                    throw new MojoExecutionException(StringUtils.format(
                            "Failed to write manifest of JAR. [file={0}, entry={1}]",
                            FileUtils.path(file),
                            entry.name()), ex);
                }
            } else if (!isJar(entry) && isUtf8(entry)) {
                packager.addEntry(entry.name(), this.getEntryContent(entry).getBytes(UTF_8));
            } else {
                packager.packageJarEntry(entry, AggregatedFitLauncher.CLASS_DIRECTORY_ENTRY_NAME + entry.name());
            }
            count++;
            this.log().debug(StringUtils.format("Add entry to app. [name={0}]", entry.name()));
        }
        this.log()
                .info(StringUtils.format("Repackage original jar successfully. [from={0}, entries={1}]",
                        file.getName(),
                        count));
    }

    private void packageLauncher(JarPackager packager) throws MojoExecutionException {
        this.packageClass(packager, AggregatedFitLauncher.class.getName());
        this.packageClass(packager, AggregatedFitLauncher.UrlClassLoader.class.getName());
    }

    private void packageClass(JarPackager packager, String className) throws MojoExecutionException {
        String name =
                StringUtils.replace(className, ClassUtils.PACKAGE_SEPARATOR, JarEntryLocation.ENTRY_PATH_SEPARATOR);
        name += ClassFile.FILE_EXTENSION;
        try (InputStream in = IoUtils.resource(AppRepackager.class.getClassLoader(), name)) {
            ZipEntry entry = new ZipEntry(name);
            packager.out().putNextEntry(entry);
            IoUtils.copy(in, packager.out());
            packager.out().closeEntry();
            this.log().info(StringUtils.format("Package launch into jar successfully. [name={0}]", name));
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to write launcher class. [entry={0}]", name),
                    e);
        }
    }

    private void packageBaseArtifacts(JarPackager packager, List<Artifact> baseArtifacts)
            throws MojoExecutionException {
        this.log().info("Prepare to package base artifacts into jar.");
        int count = 0;
        for (Artifact baseArtifact : baseArtifacts) {
            Jar jar = loadJar(baseArtifact.getFile());
            for (Jar.Entry entry : jar.entries()) {
                if (entry.directory()) {
                    continue;
                }
                if (StringUtils.startsWithIgnoreCase(entry.name(), AggregatedFitLauncher.META_DIRECTORY_ENTRY_NAME)) {
                    continue;
                }
                packager.packageJarEntry(entry);
                count++;
                this.log().debug(StringUtils.format("Add entry to launcher. [name={0}]", entry.name()));
            }
        }
        this.log().info(StringUtils.format("Package base artifacts into jar successfully. [entries={0}]", count));
    }

    private void packageDependency(JarPackager packager, Artifact dependency) throws MojoExecutionException {
        String directory = this.directoryOf(dependency);
        if (directory != null) {
            packager.store(dependency.getFile(), directory);
        }
    }

    private String directoryOf(Artifact artifact) throws MojoExecutionException {
        if (isFramework(artifact)) {
            return frameworkDirectory(artifact);
        } else if (isPlugin(artifact)) {
            return AggregatedFitLauncher.FIT_ROOT_ENTRY_NAME + "plugins" + PATH_SEPARATOR;
        } else if (isService(artifact) || this.isShared(artifact)) {
            return AggregatedFitLauncher.SHARED_ENTRY_NAME;
        } else {
            return AggregatedFitLauncher.THIRD_PARTY_ENTRY_NAME;
        }
    }

    private static String frameworkDirectory(Artifact artifact) throws MojoExecutionException {
        boolean hasMetadata = hasSpecifiedFile(artifact, METADATA_FILE);
        if (!hasMetadata) {
            return AggregatedFitLauncher.LIB_ENTRY_NAME;
        }
        try (ZipFile file = new ZipFile(artifact.getFile())) {
            ZipEntry entry = file.getEntry(METADATA_FILE);
            try (InputStream inputStream = file.getInputStream(entry)) {
                Document xml = XmlUtils.load(inputStream);
                Element allInOne = XmlUtils.child(xml, "metadata/aggregated");
                boolean ignored = Boolean.parseBoolean(XmlUtils.content(allInOne, "ignored"));
                if (ignored) {
                    return null;
                }
                boolean shared = Boolean.parseBoolean(XmlUtils.content(allInOne, "shared"));
                if (shared) {
                    return AggregatedFitLauncher.SHARED_ENTRY_NAME;
                }
                return AggregatedFitLauncher.LIB_ENTRY_NAME;
            }
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to load XML. [artifact={0}, xml={1}]",
                    artifact,
                    METADATA_FILE), e);
        }
    }

    private static String lookupEntryClass(Jar jar) throws MojoExecutionException {
        List<String> mainClasses = new LinkedList<>();
        for (Jar.Entry entry : jar.entries()) {
            if (entry.directory() || !StringUtils.endsWithIgnoreCase(entry.name(), ClassFile.FILE_EXTENSION)) {
                continue;
            }
            ClassFile file;
            try (InputStream in = entry.read()) {
                file = new ClassFile(in);
            } catch (IOException e) {
                throw new MojoExecutionException(StringUtils.format(
                        "Failed to load class from entry in JAR. [entry={0}]",
                        entry.location()), e);
            }
            if (hasMainMethod(file)) {
                mainClasses.add(obtainClassName(file, file.thisClass()));
            }
        }
        if (mainClasses.isEmpty()) {
            throw new MojoExecutionException(StringUtils.format(
                    "No class with main method is defined in the JAR. [jar={0}]",
                    jar.location()));
        } else if (mainClasses.size() > 1) {
            throw new MojoExecutionException(StringUtils.format(
                    "Multiple classes with main method are defined in the JAR. [jar={0}, classes=[{1}]]",
                    jar.location(),
                    StringUtils.join(", ", mainClasses)));
        } else {
            return mainClasses.get(0);
        }
    }

    private static boolean hasMainMethod(ClassFile file) {
        for (MethodInfo method : file.methods()) {
            if (Objects.equals(obtainStringValue(file, method.nameIndex()), MAIN_METHOD_NAME) && Objects.equals(
                    obtainStringValue(file, method.descriptorIndex()),
                    MAIN_METHOD_DESCRIPTOR)
                    && (method.accessFlags().intValue() & MAIN_METHOD_ACCESS_FLAGS) == MAIN_METHOD_ACCESS_FLAGS) {
                return true;
            }
        }
        return false;
    }

    private static String obtainClassName(ClassFile file, U2 classIndex) {
        ClassInfo classInfo = file.constants().get(classIndex);
        String className = obtainStringValue(file, classInfo.nameIndex());
        return obtainClassName(className);
    }

    private static String obtainStringValue(ClassFile file, U2 utf8Index) {
        return file.constants().<Utf8Info>get(utf8Index).stringValue();
    }

    private static String obtainClassName(String constantValue) {
        return StringUtils.replace(constantValue, JarEntryLocation.ENTRY_PATH_SEPARATOR, ClassUtils.PACKAGE_SEPARATOR);
    }

    private Artifact download(Artifact artifact) throws MojoExecutionException {
        return this.download(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }

    private Artifact download(String groupId, String artifactId, String version) throws MojoExecutionException {
        MavenCoordinate coordinate = MavenCoordinate.create(groupId, artifactId, version);
        List<Artifact> artifacts = this.downloader.download(coordinate);
        Optional<Artifact> optional = artifacts.stream()
                .filter(artifact -> Objects.equals(artifact.getGroupId(), groupId))
                .filter(artifact -> Objects.equals(artifact.getArtifactId(), artifactId))
                .findAny();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new MojoExecutionException(StringUtils.format(
                    "Failed to download artifact. [groupId={0}, artifactId={1}, version={2}]",
                    groupId,
                    artifactId,
                    version));
        }
    }
}
