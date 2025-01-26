/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import modelengine.fitframework.plugin.maven.support.DefaultDependencyResolver;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.util.function.Predicate;

/**
 * 解析文件所依赖 Maven 项的接口类。
 *
 * @author 梁济时
 * @since 2020-10-10
 */
public interface DependencyResolver {
    /**
     * 解析依赖。
     *
     * @return 表示解析好的依赖。
     * @throws MojoExecutionException 当解析过程中发生异常时。
     */
    ResolvedDependency resolve() throws MojoExecutionException;

    /**
     * 表示依赖解析器的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置 Maven 项目。
         *
         * @param project 表示待设置的 Maven 项目的 {@link MavenProject}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder setMavenProject(MavenProject project);

        /**
         * 向当前构建器中设置源代码的目录。
         *
         * @param sourceCodeDirectory 表示待设置的源代码目录的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder setSourceCodeDirectory(String sourceCodeDirectory);

        /**
         * 向当前构建器中设置依赖的过滤器。
         *
         * @param dependencyPredicate 表示待设置的依赖的过滤器的 {@link Predicate}{@code <}{@link Dependency}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder filterDependency(Predicate<Dependency> dependencyPredicate);

        /**
         * 构建解析器并解析。
         *
         * @return 表示解析好的依赖。
         * @throws MojoExecutionException 当解析过程中发生异常时。
         */
        ResolvedDependency resolve() throws MojoExecutionException;
    }

    /**
     * 创建一个自定义的依赖解析器的构建器。
     *
     * @return 表示一个自定义的依赖解析器的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultDependencyResolver.Builder();
    }
}
