/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.plugin;

import modelengine.fitframework.build.support.AbstractPackageMojo;
import modelengine.fitframework.plugin.maven.support.SharedDependency;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.List;
import java.util.Objects;

/**
 * 表示构建插件的任务。
 * <p><b>Jar in Jar 格式的插件需要支持随机访问，因此 Jar 包不能压缩。</b></p>
 *
 * @author 梁济时
 * @since 2023-02-28
 */
@Mojo(name = "package-plugin", defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
public class PackagePluginMojo extends AbstractPackageMojo {
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
        PluginRepackager repackager =
                new PluginRepackager(this.project(), this.getLog(), manifest, this.sharedDependencies);
        repackager.repackage();
    }
}
