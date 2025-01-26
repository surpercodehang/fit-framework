/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.util;

import modelengine.fitframework.maven.MavenCoordinate;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

/**
 * The DefaultArtifactDownloader
 *
 * @author 梁济时
 * @since 2023-02-02
 */
public final class ArtifactDownloader {
    private static final String ARTIFACT_TYPE = "jar";

    private final ArtifactResolver artifactResolver;
    private final ArtifactHandlerManager artifactHandlerManager;
    private final ArtifactRepository localRepository;
    private final List<ArtifactRepository> remoteRepositories;

    private ArtifactDownloader(ArtifactResolver artifactResolver, ArtifactHandlerManager artifactHandlerManager,
            ArtifactRepository localRepository, List<ArtifactRepository> remoteRepositories) {
        this.artifactResolver = artifactResolver;
        this.artifactHandlerManager = artifactHandlerManager;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
    }

    private Artifact artifact(MavenCoordinate coordinate) {
        ArtifactHandler artifactHandler = this.artifactHandlerManager.getArtifactHandler(ARTIFACT_TYPE);
        return new DefaultArtifact(coordinate.groupId(),
                coordinate.artifactId(),
                coordinate.version(),
                Artifact.SCOPE_COMPILE,
                ARTIFACT_TYPE,
                null,
                artifactHandler);
    }

    /**
     * 下载指定包。
     *
     * @param coordinate 表示包的 Maven 坐标的 {@link MavenCoordinate}。
     * @return 表示下载到的归档件的 {@link List}{@code <}{@link Artifact}{@code >}。其中包含目标包及其依赖包。
     */
    public List<Artifact> download(MavenCoordinate coordinate) {
        Artifact artifact = this.artifact(coordinate);
        ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setArtifact(artifact);
        request.setLocalRepository(localRepository);
        request.setRemoteRepositories(remoteRepositories);
        request.setResolveTransitively(true);
        ArtifactResolutionResult result = artifactResolver.resolve(request);
        return new ArrayList<>(result.getArtifacts());
    }

    /**
     * 为 {@link ArtifactDownloader.Builder} 提供默认实现。
     *
     * @author 陈镕希
     * @since 2020-12-26
     */
    public static final class Builder {
        private ArtifactResolver artifactResolver;
        private ArtifactHandlerManager artifactHandlerManager;
        private ArtifactRepository localRepository;
        private List<ArtifactRepository> remoteRepositories;

        /**
         * 设置归档件的解析程序。
         * <pre>
         * &#64;Component
         * private ArtifactResolver artifactResolver;
         * </pre>
         * <p>需要依赖：org.apache.maven:maven-compat</p>
         *
         * @param artifactResolver 表示归档件解析程序的 {@link ArtifactResolver}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        public ArtifactDownloader.Builder artifactResolver(ArtifactResolver artifactResolver) {
            this.artifactResolver = artifactResolver;
            return this;
        }

        /**
         * 设置归档件处理程序的管理程序。
         * <pre>
         * &#64;Component
         * private ArtifactHandlerManager artifactHandlerManager;
         * </pre>
         * <p>需要依赖：org.apache.maven:maven-core</p>
         *
         * @param artifactHandlerManager 表示归档件处理程序的管理程序的 {@link ArtifactHandlerManager}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        public ArtifactDownloader.Builder artifactHandlerManager(ArtifactHandlerManager artifactHandlerManager) {
            this.artifactHandlerManager = artifactHandlerManager;
            return this;
        }

        /**
         * 设置本地仓。
         * <pre>
         * &#64;Parameter(defaultValue = "${localRepository}", readonly = true)
         * private ArtifactRepository localRepository;
         * </pre>
         * <p>需要依赖：org.apache.maven:maven-artifact</p>
         *
         * @param localRepository 表示本地仓的 {@link ArtifactRepository}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        public ArtifactDownloader.Builder localRepository(ArtifactRepository localRepository) {
            this.localRepository = localRepository;
            return this;
        }

        /**
         * 设置远程仓。
         * <p>通过 {@link MavenProject#getRemoteArtifactRepositories()} 获取。</p>
         * <pre>
         * &#64;Parameter(defaultValue = "${project}", readonly = true, required = true)
         * private MavenProject project;
         * </pre>
         *
         * @param remoteRepositories 表示远程仓列表的 {@link List}{@code <}{@link ArtifactRepository}{@code >}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        public ArtifactDownloader.Builder remoteRepositories(List<ArtifactRepository> remoteRepositories) {
            this.remoteRepositories = remoteRepositories;
            return this;
        }

        /**
         * 构建一个归档件下载程序。
         *
         * @return 表示归档件下载程序的 {@link ArtifactDownloader}。
         */
        public ArtifactDownloader build() {
            return new ArtifactDownloader(this.artifactResolver,
                    this.artifactHandlerManager,
                    this.localRepository,
                    this.remoteRepositories);
        }
    }

    /**
     * 返回一个构建程序，用以定制化归档件下载程序。
     *
     * @return 表示归档件下载程序的 {@link ArtifactDownloader}。
     */
    public static Builder custom() {
        return new Builder();
    }
}
