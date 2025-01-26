/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.launch;

import modelengine.fitframework.build.support.AbstractPackageMojo;
import modelengine.fitframework.build.util.ArtifactDownloader;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * 表示构建启动程序的任务。
 *
 * @author 季聿阶
 * @since 2023-07-30
 */
@Mojo(name = "package-launcher", defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
public class PackageLauncherMojo extends AbstractPackageMojo {
    @Override
    public void execute() throws MojoExecutionException {
        ArtifactDownloader downloader = this.createDownloader();
        LauncherRepackager repackager = new LauncherRepackager(this.project(), this.getLog(), downloader);
        repackager.repackage();
    }
}
