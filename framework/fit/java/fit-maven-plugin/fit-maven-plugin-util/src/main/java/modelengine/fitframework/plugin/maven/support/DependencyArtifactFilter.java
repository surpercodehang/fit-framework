/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.util.StringUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

import java.util.Arrays;
import java.util.List;

/**
 * 为依赖提供归档件的过滤程序。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2023-02-03
 */
final class DependencyArtifactFilter implements ArtifactFilter {
    static final DependencyArtifactFilter INSTANCE = new DependencyArtifactFilter();

    private static final List<String> SCOPE_BLACK_LIST = Arrays.asList(Artifact.SCOPE_TEST, Artifact.SCOPE_PROVIDED);

    private DependencyArtifactFilter() {}

    @Override
    public boolean include(Artifact artifact) {
        return !SCOPE_BLACK_LIST.contains(artifact.getScope())
                && StringUtils.endsWithIgnoreCase(artifact.getFile().getName(), Jar.FILE_EXTENSION);
    }
}
