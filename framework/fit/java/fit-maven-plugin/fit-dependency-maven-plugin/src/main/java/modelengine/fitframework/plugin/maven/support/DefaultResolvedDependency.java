/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import modelengine.fitframework.plugin.maven.MavenCoordinate;
import modelengine.fitframework.plugin.maven.ResolvedDependency;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 表示 {@link ResolvedDependency} 的默认实现。
 *
 * @author 梁济时
 * @since 2020-10-09
 */
public class DefaultResolvedDependency implements ResolvedDependency {
    private final Set<MavenCoordinate> redundantDependencies;
    private final Map<MavenCoordinate, Map<String, Set<String>>> missingDependencies;

    public DefaultResolvedDependency() {
        this.redundantDependencies = new HashSet<>();
        this.missingDependencies = new HashMap<>();
    }

    /**
     * 添加一个依赖项到冗余依赖列表中。
     *
     * @param dependency 表示Maven的坐标实体 {@link MavenCoordinate}。
     */
    public void addRedundantDependency(MavenCoordinate dependency) {
        this.redundantDependencies.add(dependency);
    }

    /**
     * 添加缺失的Maven依赖。
     *
     * @param dependency 表示缺失的依赖的Maven坐标实体 {@link MavenCoordinate}。
     * @param dependedClassName 表示被依赖的类名 {@link String}。
     * @param userClassName 表示依赖的类名 {@link String}。
     */
    public void addMissingDependency(MavenCoordinate dependency, String dependedClassName, String userClassName) {
        this.missingDependencies.computeIfAbsent(dependency, key -> new HashMap<>())
                .computeIfAbsent(dependedClassName, key -> new HashSet<>())
                .add(userClassName);
    }

    @Override
    public Set<MavenCoordinate> getMissingDependencies() {
        return this.missingDependencies.keySet();
    }

    @Override
    public Set<String> getMissingDependedClassNames(MavenCoordinate missingDependency) {
        Map<String, Set<String>> dependedClassNames = this.missingDependencies.get(missingDependency);
        return dependedClassNames == null ? Collections.emptySet() : dependedClassNames.keySet();
    }

    @Override
    public Set<String> getMissingDependencyUserClassNames(MavenCoordinate missingDependency, String dependedClassName) {
        Map<String, Set<String>> classDependencies = this.missingDependencies.get(missingDependency);
        if (classDependencies == null) {
            return Collections.emptySet();
        }
        Set<String> missingDependencyUserClassNames = classDependencies.get(dependedClassName);
        return missingDependencyUserClassNames == null ? Collections.emptySet() : missingDependencyUserClassNames;
    }

    @Override
    public Set<MavenCoordinate> getRedundantDependencies() {
        return this.redundantDependencies;
    }
}
