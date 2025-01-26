/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 为项目中存在的依赖提供定义。
 *
 * @author 梁济时
 * @since 2020-10-09
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDependency {
    private static final String SOURCE_CODE_DIRECTORY = "src/main/java";

    private final MavenCoordinate coordinate;
    private final File directory;
    private final Set<ClassDependency> classDependencies;

    /**
     * 加载项目的依赖信息
     *
     * @param project 表示 {@link MavenProject}。
     * @param sourceCodeDirectory 表示需要加载类依赖的目录。
     * @return 表示项目中存在的依赖 {@link ProjectDependency}。
     * @throws MojoExecutionException 加载过程中发生的IO异常。
     */
    public static ProjectDependency load(MavenProject project, File sourceCodeDirectory) throws MojoExecutionException {
        Set<ClassDependency> loadedClassDependencies = loadClassDependencies(sourceCodeDirectory);
        return new ProjectDependency(MavenCoordinate.create(project), project.getBasedir(), loadedClassDependencies);
    }

    private static Set<ClassDependency> loadClassDependencies(File sourceCodeDirectory) throws MojoExecutionException {
        Set<ClassDependency> cache = new HashSet<>();
        loadClassDependencies(cache, sourceCodeDirectory, sourceCodeDirectory);
        return cache;
    }

    private static void loadClassDependencies(Set<ClassDependency> cache, File sourceCodeDirectory, File file)
            throws MojoExecutionException {
        if (file.isFile()) {
            ClassDependency.load(sourceCodeDirectory, file).ifPresent(cache::add);
        }
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File sub : files) {
            loadClassDependencies(cache, sourceCodeDirectory, sub);
        }
    }
}
