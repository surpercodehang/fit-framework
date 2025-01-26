/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

/**
 * 表示编译程序的抽象父类。
 *
 * @author 季聿阶
 * @since 2023-07-23
 */
public abstract class AbstractCompiler extends AbstractExecutor {
    public AbstractCompiler(MavenProject project, Log log, List<SharedDependency> sharedDependencies) {
        super(project, log, sharedDependencies);
    }

    /**
     * 对当前项目进行编译。
     *
     * @throws MojoExecutionException 当编译过程中出现异常时。
     */
    public void compile() throws MojoExecutionException {
        String outputDirectory = this.project().getBuild().getOutputDirectory();
        String root = outputDirectory + File.separator + FIT_ROOT_DIRECTORY;
        ensureDirectory(root);
        this.output(outputDirectory, root);
    }

    /**
     * 向目标文件夹下输出编译产物。
     *
     * @param outputDirectory 表示原始编译的输出目录名字的 {@link String}。
     * @param fitRootDirectory 表示 FIT 输出目标文件夹名字的 {@link String}。
     * @throws MojoExecutionException 当编译过程中出现异常时。
     */
    protected abstract void output(String outputDirectory, String fitRootDirectory) throws MojoExecutionException;
}
