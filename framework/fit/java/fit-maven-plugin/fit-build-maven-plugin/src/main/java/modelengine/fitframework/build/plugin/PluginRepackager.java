/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.plugin;

import static java.nio.charset.StandardCharsets.UTF_8;

import modelengine.fitframework.build.support.AbstractRepackager;
import modelengine.fitframework.build.util.JarPackager;
import modelengine.fitframework.plugin.maven.support.SharedDependency;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 为插件提供打包程序。
 *
 * @author 梁济时
 * @since 2023-02-28
 */
final class PluginRepackager extends AbstractRepackager {
    private static final String PLUGIN_METADATA_ENTRY_NAME = FIT_ROOT_DIRECTORY + PATH_SEPARATOR + "plugin.xml";
    private static final String SHARED_LIBRARY_DIRECTORY =
            FIT_ROOT_DIRECTORY + PATH_SEPARATOR + "shared" + PATH_SEPARATOR;
    private static final String THIRD_PARTY_LIBRARY_DIRECTORY =
            FIT_ROOT_DIRECTORY + PATH_SEPARATOR + "third-party" + PATH_SEPARATOR;

    private final PluginManifest manifest;

    PluginRepackager(MavenProject project, Log log, PluginManifest manifest,
            List<SharedDependency> sharedDependencies) {
        super(project, log, sharedDependencies);
        this.manifest = manifest;
    }

    void repackage() throws MojoExecutionException {
        File target = this.project().getArtifact().getFile();
        File backup = this.backupFile(target);
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(target.toPath()), UTF_8)) {
            JarPackager packager = new JarPackager(out);
            this.repackageOriginalJar(packager, backup);
            this.packageManifest(packager);
            this.packageDependencies(packager, this.project().getArtifacts());
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to repackage plugin JAR. [file={0}]",
                    FileUtils.path(target)), ex);
        }
    }

    private void repackageOriginalJar(JarPackager packager, File file) throws MojoExecutionException {
        Jar jar = loadJar(file);
        this.log().info(StringUtils.format("Prepare to repackage original jar. [from={0}]", file.getName()));
        int count = 0;
        for (Jar.Entry entry : jar.entries()) {
            if (entry.directory()) {
                continue;
            }
            if (this.isNotRepackage(entry)) {
                continue;
            }
            if (!isJar(entry) && isUtf8(entry)) {
                packager.addEntry(entry.name(), this.getEntryContent(entry).getBytes(UTF_8));
            } else {
                packager.packageJarEntry(entry);
            }
            count++;
            this.log().debug(StringUtils.format("Add entry to plugin. [name={0}]", entry.name()));
        }
        this.log()
                .info(StringUtils.format("Repackage original jar successfully. [from={0}, entries={1}]",
                        file.getName(),
                        count));
    }

    private boolean isNotRepackage(Jar.Entry entry) {
        return entry.name().startsWith(PLUGIN_METADATA_ENTRY_NAME) || entry.name().startsWith(SHARED_LIBRARY_DIRECTORY)
                || entry.name().startsWith(THIRD_PARTY_LIBRARY_DIRECTORY);
    }

    private void packageManifest(JarPackager packager) throws MojoExecutionException {
        ZipEntry entry = new ZipEntry(PLUGIN_METADATA_ENTRY_NAME);
        try {
            packager.out().putNextEntry(entry);
            this.manifest.write(packager.out());
            packager.out().closeEntry();
            this.log().info(StringUtils.format("Package manifest. [name={0}]", PLUGIN_METADATA_ENTRY_NAME));
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to write plugin manifest into JAR. [entry={0}]",
                    PLUGIN_METADATA_ENTRY_NAME), e);
        }
    }

    private void packageDependencies(JarPackager packager, Collection<Artifact> dependencies)
            throws MojoExecutionException {
        this.log().info(StringUtils.format("Prepare to package dependencies. [total={0}]", dependencies.size()));
        int added = 0;
        for (Artifact dependency : dependencies) {
            String directory = this.directoryOf(dependency);
            if (directory != null) {
                packager.store(dependency.getFile(), directory);
                added++;
                this.log()
                        .debug(StringUtils.format("Package dependency. [name={0}, to={1}]",
                                dependency.getFile().getName(),
                                directory));
            }
        }
        this.log().info(StringUtils.format("Package dependencies successfully. [actual={0}]", added));
    }

    private String directoryOf(Artifact dependency) throws MojoExecutionException {
        if (isFramework(dependency)) {
            return null;
        } else if (isService(dependency) || this.isShared(dependency)) {
            return SHARED_LIBRARY_DIRECTORY;
        } else {
            return THIRD_PARTY_LIBRARY_DIRECTORY;
        }
    }
}
