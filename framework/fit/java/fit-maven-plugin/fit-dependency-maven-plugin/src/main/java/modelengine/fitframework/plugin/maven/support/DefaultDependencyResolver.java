/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import modelengine.fitframework.plugin.maven.ClassDependency;
import modelengine.fitframework.plugin.maven.DependencyResolver;
import modelengine.fitframework.plugin.maven.MavenCoordinate;
import modelengine.fitframework.plugin.maven.ProjectDependency;
import modelengine.fitframework.plugin.maven.ResolvedDependency;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 表示 {@link DependencyResolver} 的默认实现。
 *
 * @author 梁济时
 * @since 2020-10-09
 */
public class DefaultDependencyResolver implements DependencyResolver {
    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final String JAR_FILE_EXTENSION = ".jar";
    private static final String POM_PACKAGING = "pom";

    private final MavenProject project;
    private final String sourceCodeDirectoryPath;
    private final Predicate<Dependency> dependencyPredicate;

    public DefaultDependencyResolver(MavenProject project, String sourceCodeDirectoryPath,
            Predicate<Dependency> dependencyPredicate) {
        this.project = project;
        this.sourceCodeDirectoryPath = sourceCodeDirectoryPath;
        this.dependencyPredicate = dependencyPredicate;
    }

    private boolean filter(Dependency dependency) {
        return this.dependencyPredicate.test(dependency);
    }

    /**
     * 用于解析maven解析得到的相关依赖
     *
     * @return 解析出的maven{@link ResolvedDependency}
     * @throws MojoExecutionException 压缩过程中发生的IO异常
     */
    @Override
    public ResolvedDependency resolve() throws MojoExecutionException {
        if (this.project.getPackaging().equalsIgnoreCase(POM_PACKAGING)) {
            return ResolvedDependency.empty();
        }

        File sourceCodeDirectory = new File(this.project.getBasedir(), this.sourceCodeDirectoryPath);
        ProjectDependency projectDependency = ProjectDependency.load(this.project, sourceCodeDirectory);
        Map<MavenCoordinate, Set<String>> jarClassNames = loadJarClassNames(this.project.getArtifacts());

        Set<MavenCoordinate> configuredDependencies = this.project.getDependencies()
                .stream()
                .filter(this::filter)
                .map(MavenCoordinate::create)
                .collect(Collectors.toSet());
        Set<MavenCoordinate> codeDependencies = projectDependency.getClassDependencies()
                .stream()
                .flatMap(classDependency -> getDependedArtifacts(classDependency, jarClassNames))
                .collect(Collectors.toSet());

        DefaultResolvedDependency result = new DefaultResolvedDependency();
        exclude(configuredDependencies, codeDependencies).forEach(result::addRedundantDependency);
        Set<MavenCoordinate> missingDependencies = exclude(codeDependencies, configuredDependencies);
        for (MavenCoordinate missingDependency : missingDependencies) {
            collectMissingDependency(result, missingDependency, projectDependency, jarClassNames);
        }
        return result;
    }

    private static Set<MavenCoordinate> exclude(Set<MavenCoordinate> source, Set<MavenCoordinate> target) {
        Set<MavenCoordinate> result = new HashSet<>(source);
        result.removeAll(target);
        return result;
    }

    private static void collectMissingDependency(DefaultResolvedDependency result, MavenCoordinate missingDependency,
            ProjectDependency projectDependency, Map<MavenCoordinate, Set<String>> jarClassNames) {
        for (ClassDependency classDependency : projectDependency.getClassDependencies()) {
            collectMissingDependency(result, missingDependency, classDependency, jarClassNames);
        }
    }

    private static void collectMissingDependency(DefaultResolvedDependency result, MavenCoordinate dependency,
            ClassDependency classDependency, Map<MavenCoordinate, Set<String>> jarClassNames) {
        String className = classDependency.getClassName();
        jarClassNames.get(dependency)
                .stream()
                .filter(classDependency.getDependencies()::contains)
                .forEach(dependedClassName -> result.addMissingDependency(dependency, dependedClassName, className));
    }

    private static Stream<MavenCoordinate> getDependedArtifacts(ClassDependency classDependency,
            Map<MavenCoordinate, Set<String>> jarClassNames) {
        return classDependency.getDependencies()
                .stream()
                .map(dependedClassName -> getDependedArtifact(dependedClassName, jarClassNames))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private static Optional<MavenCoordinate> getDependedArtifact(String className,
            Map<MavenCoordinate, Set<String>> jarClassNames) {
        return jarClassNames.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(className))
                .findAny()
                .map(Map.Entry::getKey);
    }

    private static Map<MavenCoordinate, Set<String>> loadJarClassNames(Set<Artifact> artifacts)
            throws MojoExecutionException {
        Map<MavenCoordinate, Set<String>> jarClassNames = new HashMap<>();
        for (Artifact artifact : artifacts) {
            jarClassNames.put(MavenCoordinate.create(artifact), new HashSet<>(loadClassNames(artifact.getFile())));
        }
        return jarClassNames;
    }

    private static Set<String> loadClassNames(File jarFile) throws MojoExecutionException {
        if (!jarFile.getName().endsWith(JAR_FILE_EXTENSION)) {
            return Collections.emptySet();
        }
        try (ZipFile file = new ZipFile(jarFile)) {
            Set<String> classNames = new HashSet<>();
            Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                getClassName(entry.getName()).ifPresent(classNames::add);
            }
            return classNames;
        } catch (IOException ex) {
            throw new MojoExecutionException("Fail to load jar file: " + jarFile.getName(), ex);
        }
    }

    private static Optional<String> getClassName(String entryName) {
        if (!entryName.endsWith(CLASS_FILE_EXTENSION) || entryName.contains("-")) {
            return Optional.empty();
        }
        String pathWithoutExtension = entryName.substring(0, entryName.length() - CLASS_FILE_EXTENSION.length());
        return Optional.of(pathWithoutExtension.replace('/', '.'));
    }

    /**
     * {@link DependencyResolver.Builder} 的默认实现。
     */
    public static class Builder implements DependencyResolver.Builder {
        private MavenProject project;
        private String sourceCodeDirectoryPath;
        private Predicate<Dependency> dependencyPredicate;

        @Override
        public DependencyResolver.Builder setMavenProject(MavenProject project) {
            this.project = project;
            return this;
        }

        @Override
        public DependencyResolver.Builder setSourceCodeDirectory(String sourceCodeDirectory) {
            this.sourceCodeDirectoryPath = sourceCodeDirectory;
            return this;
        }

        @Override
        public DependencyResolver.Builder filterDependency(Predicate<Dependency> dependencyPredicate) {
            this.dependencyPredicate = dependencyPredicate;
            return this;
        }

        @Override
        public ResolvedDependency resolve() throws MojoExecutionException {
            return new DefaultDependencyResolver(this.project,
                    this.sourceCodeDirectoryPath,
                    this.dependencyPredicate).resolve();
        }
    }
}
