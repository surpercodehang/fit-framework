/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.support;

import modelengine.fitframework.build.util.ArtifactDownloader;
import modelengine.fitframework.plugin.maven.support.AbstractMojo;

import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 为用以构建的 MOJO 提供基类。
 *
 * @author 梁济时
 * @since 2023-02-28
 */
public abstract class AbstractPackageMojo extends AbstractMojo {
    @Parameter(defaultValue = "${localRepository}", readonly = true)
    private ArtifactRepository localRepository;
    @Component
    private ArtifactResolver artifactResolver;
    @Component
    private ArtifactHandlerManager artifactHandlerManager;

    /**
     * 创建一个用于下载依赖项的工具。
     *
     * @return 表示依赖下载工具的 {@link ArtifactDownloader}。
     */
    protected ArtifactDownloader createDownloader() {
        return ArtifactDownloader.custom()
                .artifactResolver(this.artifactResolver)
                .artifactHandlerManager(this.artifactHandlerManager)
                .localRepository(this.localRepository)
                .remoteRepositories(this.project().getRemoteArtifactRepositories())
                .build();
    }
}
