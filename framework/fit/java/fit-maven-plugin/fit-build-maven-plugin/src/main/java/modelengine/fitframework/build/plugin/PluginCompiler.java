/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.plugin;

import modelengine.fitframework.plugin.maven.support.AbstractCompiler;
import modelengine.fitframework.plugin.maven.support.SharedDependency;
import modelengine.fitframework.util.StringUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;

/**
 * 为插件提供编译程序。
 *
 * @author 季聿阶
 * @since 2023-07-23
 */
final class PluginCompiler extends AbstractCompiler {
    private static final String PLUGIN_MANIFEST = "plugin.xml";

    private final PluginManifest manifest;

    PluginCompiler(MavenProject project, Log log, PluginManifest manifest, List<SharedDependency> sharedDependencies) {
        super(project, log, sharedDependencies);
        this.manifest = manifest;
    }

    @Override
    protected void output(String outputDirectory, String fitRootDirectory) throws MojoExecutionException {
        this.outputPluginManifest(fitRootDirectory);
        this.outputDependencies(fitRootDirectory);
    }

    private void outputPluginManifest(String outputDirectory) throws MojoExecutionException {
        String fileName = outputDirectory + File.separator + PLUGIN_MANIFEST;
        try (OutputStream out = Files.newOutputStream(new File(fileName).toPath())) {
            this.manifest.write(out);
            this.log()
                    .info(StringUtils.format("Write plugin manifest. [file={0}, category={1}, level={2}]",
                            fileName,
                            this.manifest.category(),
                            this.manifest.level()));
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to write plugin manifest. [file={0}]",
                    fileName), e);
        }
    }

    private void outputDependencies(String outputDirectory) throws MojoExecutionException {
        Set<Artifact> dependencies = this.project().getArtifacts();
        this.log().info(StringUtils.format("Prepare to package dependencies. [total={0}]", dependencies.size()));
        int added = 0;
        for (Artifact dependency : dependencies) {
            String directory = this.directoryOf(dependency);
            if (directory != null) {
                try {
                    Path path = Paths.get(outputDirectory, directory, dependency.getFile().getName());
                    ensureDirectory(path.getParent().toString());
                    Files.copy(dependency.getFile().toPath(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new MojoExecutionException(StringUtils.format("Failed to copy dependency. [name={0}]",
                            dependency), e);
                }
                added++;
                this.log()
                        .debug(StringUtils.format("Copy dependency successfully. [name={0}, to={1}]",
                                dependency,
                                directory));
            }
        }
        this.log().info(StringUtils.format("Package dependencies successfully. [actual={0}]", added));
    }

    private String directoryOf(Artifact dependency) throws MojoExecutionException {
        if (isFramework(dependency)) {
            return null;
        } else if (isService(dependency) || this.isShared(dependency)) {
            return "shared";
        } else if (isPlugin(dependency)) {
            throw new MojoExecutionException(StringUtils.format(
                    "Plugin cannot depend on another plugin. [groupId={0}, artifactId={1}]",
                    dependency.getGroupId(),
                    dependency.getArtifactId()));
        } else {
            return "third-party";
        }
    }
}
