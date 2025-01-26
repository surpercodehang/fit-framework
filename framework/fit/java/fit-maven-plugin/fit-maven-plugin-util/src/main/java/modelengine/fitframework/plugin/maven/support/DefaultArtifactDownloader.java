/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import modelengine.fitframework.plugin.maven.ArtifactDownloader;
import modelengine.fitframework.plugin.maven.MavenCoordinate;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * The DefaultArtifactDownloader
 *
 * @author 陈镕希
 * @since 2020/12/26
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultArtifactDownloader implements ArtifactDownloader {
    private static final String ARTIFACT_TYPE = "jar";

    private final ArtifactResolver artifactResolver;
    private final ArtifactHandlerManager artifactHandlerManager;
    private final ArtifactRepository localRepository;
    private final List<ArtifactRepository> remoteRepositories;
    private final MavenCoordinate artifactCoordinate;

    private Artifact artifact() {
        ArtifactHandler artifactHandler = this.artifactHandlerManager.getArtifactHandler(ARTIFACT_TYPE);
        return new DefaultArtifact(this.artifactCoordinate.getGroupId(), this.artifactCoordinate.getArtifactId(),
                this.artifactCoordinate.getVersion(),
                Artifact.SCOPE_COMPILE,
                ARTIFACT_TYPE,
                null,
                artifactHandler);
    }

    @Override
    public List<Artifact> download() {
        Artifact artifact = this.artifact();
        ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setArtifact(artifact);
        request.setLocalRepository(this.localRepository);
        request.setRemoteRepositories(this.remoteRepositories);
        request.setResolveTransitively(true);
        ArtifactResolutionResult result = this.artifactResolver.resolve(request);
        return new ArrayList<>(result.getArtifacts());
    }

    /**
     * 为 {@link ArtifactDownloader.Builder} 提供默认实现。
     *
     * @author 陈镕希
     * @since 2020-12-26
     */
    public static class Builder implements ArtifactDownloader.Builder {
        private ArtifactResolver artifactResolver;
        private ArtifactHandlerManager artifactHandlerManager;
        private ArtifactRepository localRepository;
        private List<ArtifactRepository> remoteRepositories;
        private MavenCoordinate artifactCoordinate;

        @Override
        public ArtifactDownloader.Builder setArtifactResolver(ArtifactResolver artifactResolver) {
            this.artifactResolver = artifactResolver;
            return this;
        }

        @Override
        public ArtifactDownloader.Builder setArtifactHandlerManager(ArtifactHandlerManager artifactHandlerManager) {
            this.artifactHandlerManager = artifactHandlerManager;
            return this;
        }

        @Override
        public ArtifactDownloader.Builder setLocalRepository(ArtifactRepository localRepository) {
            this.localRepository = localRepository;
            return this;
        }

        @Override
        public ArtifactDownloader.Builder setRemoteRepositories(List<ArtifactRepository> remoteRepositories) {
            this.remoteRepositories = remoteRepositories;
            return this;
        }

        @Override
        public ArtifactDownloader.Builder setArtifactCoordinate(MavenCoordinate artifactCoordinate) {
            this.artifactCoordinate = artifactCoordinate;
            return this;
        }

        @Override
        public ArtifactDownloader build() {
            return new DefaultArtifactDownloader(this.artifactResolver,
                    this.artifactHandlerManager,
                    this.localRepository,
                    this.remoteRepositories,
                    this.artifactCoordinate);
        }
    }
}
