/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import modelengine.fitframework.plugin.maven.support.LogResolvedDependencyPrinter;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Locale;

/**
 * Analyses dependencies for maven projects.
 *
 * @author 梁济时
 * @since 2020-10-09
 */
@Mojo(name = "dependency", defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class DependencyMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        DependencyResolver.custom()
                .setMavenProject(this.project)
                .setSourceCodeDirectory(String.join(File.separator, "src", "main", "java"))
                .filterDependency(DependencyMojo::filterMainDependency)
                .resolve()
                .print(this.createPrinter("compile"));
    }

    private ResolvedDependencyPrinter createPrinter(String scope) {
        String name = String.format(Locale.ROOT, "%s (%s)", this.project.getArtifactId(), scope);
        return new LogResolvedDependencyPrinter(this.getLog(), name);
    }

    private static boolean filterMainDependency(Dependency dependency) {
        return !dependency.getScope().equalsIgnoreCase("test") && !dependency.getScope().equalsIgnoreCase("import");
    }
}
