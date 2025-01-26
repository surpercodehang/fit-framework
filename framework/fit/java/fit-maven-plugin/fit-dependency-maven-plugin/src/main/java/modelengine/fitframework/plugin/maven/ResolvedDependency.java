/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import modelengine.fitframework.plugin.maven.support.EmptyResolvedDependency;

import java.util.Set;

/**
 * 表示解析完成的依赖。
 *
 * @author 梁济时
 * @since 2020-10-09
 */
public interface ResolvedDependency {
    /**
     * 获取缺失的依赖集合。
     *
     * @return 表示缺失的依赖集合的 {@link Set}{@code <}{@link MavenCoordinate}{@code >}。
     */
    Set<MavenCoordinate> getMissingDependencies();

    /**
     * 获取缺失的依赖中被依赖的类的名字集合。
     *
     * @param missingDependency 表示缺失的依赖的 {@link MavenCoordinate}。
     * @return 表示缺失的依赖中被依赖的类的名字集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> getMissingDependedClassNames(MavenCoordinate missingDependency);

    /**
     * 获取缺失的依赖中指定被依赖的类的用户类名字的集合。
     *
     * @param missingDependency 表示缺失的依赖的 {@link MavenCoordinate}。
     * @param dependedClassName 表示指定的被依赖的类名的 {@link String}。
     * @return 表示缺失的依赖中指定被依赖的类的用户类名字的集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> getMissingDependencyUserClassNames(MavenCoordinate missingDependency, String dependedClassName);

    /**
     * 获取冗余的依赖集合。
     *
     * @return 表示冗余的依赖集合的 {@link Set}{@code <}{@link MavenCoordinate}{@code >}。
     */
    Set<MavenCoordinate> getRedundantDependencies();

    /**
     * 打印解析好的依赖信息。
     *
     * @param printer 表示打印器的 {@link ResolvedDependencyPrinter}。
     */
    default void print(ResolvedDependencyPrinter printer) {
        printer.print(this);
    }

    /**
     * 获取一个空的解析好的依赖。
     *
     * @return 表示一个空的解析好的依赖的 {@link ResolvedDependency}。
     */
    static ResolvedDependency empty() {
        return EmptyResolvedDependency.INSTANCE;
    }
}
