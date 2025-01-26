/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import modelengine.fitframework.plugin.maven.support.DefaultArtifactDownloader;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

/**
 * 为归档件提供下载程序。
 *
 * @author 梁济时
 * @since 2020-11-19
 */
public interface ArtifactDownloader {
    /**
     * 下载归档件。
     *
     * @return List<Artifact> 返回的归档件内容。
     */
    List<Artifact> download();

    /**
     * 为 {@link ArtifactDownloader} 提供构建器。
     *
     * @author 梁济时
     * @since 2020-11-19
     */
    interface Builder {
        /**
         * 设置归档件的解析程序。
         *
         * <p>可在Mojo中通过 {@link Component} 注解配置注入。</p>
         * <p>
         * Example:
         * <pre class="code">
         * {@code @Component}
         * private ArtifactResolver artifactResolver;
         * </pre>
         *
         * @param artifactResolver 表示归档件的解析程序的 {@link ArtifactResolver}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder setArtifactResolver(ArtifactResolver artifactResolver);

        /**
         * 设置归档件处理器的管理程序。
         *
         * <p>可在Mojo中通过 {@link Component} 注解配置注入。</p>
         * <p>
         * Example:
         * <pre class="code">
         * {@code @Component}
         * private ArtifactHandlerManager artifactHandlerManager;
         * </pre>
         *
         * @param artifactHandlerManager 表示归档件处理器管理程序的 {@link ArtifactHandlerManager}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder setArtifactHandlerManager(ArtifactHandlerManager artifactHandlerManager);

        /**
         * 设置本地仓库。
         *
         * <p>可在Mojo中通过 {@link Parameter} 注解配置注入。</p>
         * <p>
         * Example:
         * <pre class="code">
         * {@code @Parameter(defaultValue = "${localRepository}", readonly = true)}
         * private ArtifactRepository localRepository;
         * </pre>
         *
         * @param localRepository 表示本地仓库的 {@link ArtifactRepository}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder setLocalRepository(ArtifactRepository localRepository);

        /**
         * 设置可用的远端仓库列表。
         *
         * <p>可通过 {@link MavenProject#getRemoteArtifactRepositories()} 方法获取。{@link MavenProject} 可在Mojo中通过
         * {@link Parameter} 注解配置注入。</p>
         * <p>
         * Example:
         * <pre class="code">
         * {@code @Parameter(defaultValue = "${project}", readonly = true, required = true)}
         * private MavenProject project;
         * ...
         * List&lt;ArtifactRepository&gt;
         * remoteRepositories = </ArtifactRepository>this.project.getRemoteArtifactRepositories();
         * </pre>
         *
         * @param remoteRepositories 表示远端仓库列表的 {@link List}{@code <}{@link ArtifactRepository}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder setRemoteRepositories(List<ArtifactRepository> remoteRepositories);

        /**
         * 设置待下载的归档件的坐标。
         *
         * @param artifactCoordinate 表示归档件坐标的 {@link MavenCoordinate}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder setArtifactCoordinate(MavenCoordinate artifactCoordinate);

        /**
         * 构建一个 {@link ArtifactDownloader} 的默认实现的新实例。
         *
         * @return 表示归档件下载程序的新实例的 {@link ArtifactDownloader}。
         */
        ArtifactDownloader build();
    }

    /**
     * 返回一个构建器，用以构建归档件下载程序默认实现的新实例。
     *
     * @return 表示归档件下载程序构建器的 {@link Builder}。
     */
    static Builder builder() {
        return new DefaultArtifactDownloader.Builder();
    }
}
