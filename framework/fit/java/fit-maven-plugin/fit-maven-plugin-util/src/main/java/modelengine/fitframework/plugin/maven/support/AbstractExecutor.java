/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 表示 Maven 插件的任务执行器的抽象父类。
 *
 * @author 季聿阶
 * @since 2023-07-23
 */
public abstract class AbstractExecutor {
    /** 表示 FIT 在输出件中的根目录。 */
    protected static final String FIT_ROOT_DIRECTORY = "FIT-INF";

    private final MavenProject project;
    private final Log log;
    private final List<SharedDependency> sharedDependencies;

    protected AbstractExecutor(MavenProject project, Log log, List<SharedDependency> sharedDependencies) {
        this.project = notNull(project, "The maven project cannot be null.");
        this.log = notNull(log, "The log cannot be null.");
        this.sharedDependencies = ObjectUtils.getIfNull(sharedDependencies, Collections::emptyList);
    }

    /**
     * 判断指定依赖是否为 FIT 框架中的包。
     *
     * @param artifact 表示指定依赖的 {@link Artifact}。
     * @return 如果指定依赖是 FIT 框架中的包，则返回 {@code true}，否则，返回 {@code false}。
     */
    protected static boolean isFramework(Artifact artifact) {
        return StringUtils.equalsIgnoreCase(artifact.getGroupId(), "modelengine.fitframework");
    }

    /**
     * 判断指定依赖是否为服务。
     *
     * @param artifact 表示指定依赖的 {@link Artifact}。
     * @return 如果指定服务为服务，则返回 {@code true}，否则，返回 {@code false}。
     * @throws MojoExecutionException 当解析依赖过程中发生异常时。
     */
    protected static boolean isService(Artifact artifact) throws MojoExecutionException {
        return AbstractExecutor.hasSpecifiedFile(artifact,
                FIT_ROOT_DIRECTORY + JarEntryLocation.ENTRY_PATH_SEPARATOR + "service.xml");
    }

    /**
     * 判断指定依赖是否为插件。
     *
     * @param artifact 表示指定依赖的 {@link Artifact}。
     * @return 如果指定服务为插件，则返回 {@code true}，否则，返回 {@code false}。
     * @throws MojoExecutionException 当解析依赖过程中发生异常时。
     */
    protected static boolean isPlugin(Artifact artifact) throws MojoExecutionException {
        return AbstractExecutor.hasSpecifiedFile(artifact,
                FIT_ROOT_DIRECTORY + JarEntryLocation.ENTRY_PATH_SEPARATOR + "plugin.xml");
    }

    /**
     * 判断指定依赖是否为共享的。
     *
     * @param artifact 表示指定依赖的 {@link Artifact}。
     * @return 如果指定服务为共享的，则返回 {@code true}，否则，返回 {@code false}。
     */
    protected boolean isShared(Artifact artifact) {
        for (SharedDependency sharedDependency : this.sharedDependencies) {
            if (StringUtils.equalsIgnoreCase(artifact.getGroupId(), sharedDependency.getGroupId())
                    && StringUtils.equalsIgnoreCase(artifact.getArtifactId(), sharedDependency.getArtifactId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在指定依赖中判断是否存在指定文件。
     *
     * @param artifact 表示指定依赖的 {@link Artifact}。
     * @param specified 表示指定的文件路径的 {@link String}。
     * @return 如果存在，返回 {@code true}，否则，返回 {@code false}。
     * @throws MojoExecutionException 当解析依赖过程中发生异常时。
     */
    protected static boolean hasSpecifiedFile(Artifact artifact, String specified) throws MojoExecutionException {
        try (ZipInputStream in = new ZipInputStream(Files.newInputStream(artifact.getFile().toPath()), UTF_8)) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                if (StringUtils.equalsIgnoreCase(entry.getName(), specified)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to read depended JAR. [file={0}]",
                    FileUtils.path(artifact.getFile())), e);
        }
    }

    /**
     * 确保目录被创建。
     *
     * @param outputDirectory 表示目标目录的 {@link String}。
     * @throws MojoExecutionException 当创建目录失败时。
     */
    protected static void ensureDirectory(String outputDirectory) throws MojoExecutionException {
        File file = new File(outputDirectory);
        try {
            FileUtils.ensureDirectory(file);
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to create directory. [file={0}]",
                    outputDirectory), e);
        }
    }

    /**
     * 获取当前项目的 Maven 工程。
     *
     * @return 表示 Maven 工程的 {@link MavenProject}。
     */
    protected final MavenProject project() {
        return this.project;
    }

    /**
     * 获取日志打印器。
     *
     * @return 表示日志打印器的 {@link Log}。
     */
    protected final Log log() {
        return this.log;
    }
}
