/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import modelengine.fitframework.plugin.maven.MavenCoordinate;
import modelengine.fitframework.plugin.maven.ResolvedDependency;

import java.util.Collections;
import java.util.Set;

/**
 * 为 {@link ResolvedDependency} 提供空实现。
 *
 * @author 梁济时
 * @since 2020-10-10
 */
public class EmptyResolvedDependency implements ResolvedDependency {
    /** 表示 {@link EmptyResolvedDependency} 类的唯一实例。 */
    public static final ResolvedDependency INSTANCE = new EmptyResolvedDependency();

    @Override
    public Set<MavenCoordinate> getMissingDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getMissingDependedClassNames(MavenCoordinate missingDependency) {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getMissingDependencyUserClassNames(MavenCoordinate missingDependency, String dependedClassName) {
        return Collections.emptySet();
    }

    @Override
    public Set<MavenCoordinate> getRedundantDependencies() {
        return Collections.emptySet();
    }
}
