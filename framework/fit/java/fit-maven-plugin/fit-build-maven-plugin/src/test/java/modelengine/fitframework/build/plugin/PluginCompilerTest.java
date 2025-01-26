/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.util.support.Zip;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

/**
 * 表示 {@link PluginCompiler} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-12-04
 */
@DisplayName("测试 PluginCompile")
public class PluginCompilerTest {
    private MavenProject mavenProject;
    private PluginCompiler compiler;

    @BeforeEach
    void setup() throws MojoExecutionException {
        this.mavenProject = mock(MavenProject.class);
        Log log = mock(Log.class);
        PluginManifest manifest = PluginManifest.custom()
                .group("test.plugin")
                .name("test-plugin")
                .hierarchicalNames("test.plugin")
                .version("1.0.0-SNAPSHOT")
                .category("system")
                .level("4")
                .build();
        this.compiler = new PluginCompiler(this.mavenProject, log, manifest, Collections.emptyList());
    }

    @Test
    @DisplayName("当插件依赖了另一个插件时，编译报错")
    void shouldThrowExceptionWhenDependOnAnotherPlugin() throws IOException {
        File jarFile = new File("src/test/resources/plugin-compile-mojo-tmp.jar");
        jarFile.deleteOnExit();
        Zip zip = new Zip(jarFile, null).override(true).add(new File("src/test/resources/zip/has-plugin/FIT-INF"));
        zip.start();

        File directory = Files.createTempDirectory("PluginCompilerTest-").toFile();
        directory.deleteOnExit();
        Artifact artifact = mock(Artifact.class);
        when(artifact.getFile()).thenReturn(jarFile);
        when(artifact.getGroupId()).thenReturn("test.plugin");
        when(artifact.getArtifactId()).thenReturn("test-plugin");
        when(this.mavenProject.getArtifacts()).thenReturn(Collections.singleton(artifact));
        MojoExecutionException cause = catchThrowableOfType(() -> this.compiler.output(null, directory.getPath()),
                MojoExecutionException.class);
        assertThat(cause).hasMessage(
                "Plugin cannot depend on another plugin. [groupId=test.plugin, artifactId=test-plugin]");
    }
}
