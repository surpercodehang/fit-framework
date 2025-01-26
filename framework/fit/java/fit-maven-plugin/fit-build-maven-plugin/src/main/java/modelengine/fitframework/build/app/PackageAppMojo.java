/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.app;

import modelengine.fitframework.build.support.AbstractPackageMojo;
import modelengine.fitframework.build.util.ArtifactDownloader;
import modelengine.fitframework.plugin.maven.support.SharedDependency;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyCollectorBuilder;
import org.apache.maven.shared.dependency.graph.DependencyCollectorBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.util.List;

/**
 * 表示构建应用的任务。
 *
 * @author 梁济时
 * @since 2023-02-07
 */
@Mojo(name = "package-app", defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
public class PackageAppMojo extends AbstractPackageMojo {
    @Parameter(property = "sharedDependencies")
    private List<SharedDependency> sharedDependencies;
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;
    @Component(hint = "default")
    private DependencyCollectorBuilder dependencyCollectorBuilder;

    @Override
    public void execute() throws MojoExecutionException {
        ProjectBuildingRequest request = this.createRequest();
        ArtifactFilter filter = new ScopeArtifactFilter("runtime");
        DependencyNode root;
        try {
            root = this.dependencyCollectorBuilder.collectDependencyGraph(request, filter);
        } catch (DependencyCollectorBuilderException e) {
            throw new MojoExecutionException("Failed to collect dependency tree.", e);
        }

        ArtifactDownloader downloader = this.createDownloader();
        AppRepackager repackager =
                new AppRepackager(this.project(), this.getLog(), downloader, root, this.sharedDependencies);
        repackager.repackage();
    }

    private ProjectBuildingRequest createRequest() {
        ProjectBuildingRequest request = this.session.getProjectBuildingRequest();
        request = new DefaultProjectBuildingRequest(request);
        request.setProject(this.project());
        return request;
    }
}
