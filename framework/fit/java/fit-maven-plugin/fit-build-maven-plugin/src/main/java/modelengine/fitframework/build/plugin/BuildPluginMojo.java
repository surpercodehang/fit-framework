/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.plugin;

import modelengine.fitframework.plugin.maven.support.AbstractMojo;
import modelengine.fitframework.plugin.maven.support.SharedDependency;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.List;
import java.util.Objects;

/**
 * 表示编译插件的任务。
 *
 * @author 季聿阶
 * @since 2023-07-23
 */
@Mojo(name = "build-plugin", defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
public class BuildPluginMojo extends AbstractMojo {
    @Parameter(property = "hierarchicalNames")
    private String hierarchicalNames;
    @Parameter(property = "category")
    private String category;
    @Parameter(property = "level")
    private String level;
    @Parameter(property = "sharedDependencies")
    private List<SharedDependency> sharedDependencies;
    @Parameter(defaultValue = "${project.packaging}", readonly = true, required = true)
    private String packaging;

    @Override
    public void execute() throws MojoExecutionException {
        if (!Objects.equals(this.packaging, "jar")) {
            return;
        }
        PluginManifest manifest = PluginManifest.custom()
                .group(this.project().getGroupId())
                .name(this.project().getArtifactId())
                .hierarchicalNames(this.hierarchicalNames)
                .version(this.project().getVersion())
                .category(this.category)
                .level(this.level)
                .build();
        PluginCompiler compiler = new PluginCompiler(this.project(), this.getLog(), manifest, this.sharedDependencies);
        compiler.compile();
    }
}
