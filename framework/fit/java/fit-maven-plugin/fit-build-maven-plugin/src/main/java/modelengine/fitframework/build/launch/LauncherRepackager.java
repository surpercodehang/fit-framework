/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.launch;

import static java.nio.charset.StandardCharsets.UTF_8;

import modelengine.fitframework.build.support.AbstractRepackager;
import modelengine.fitframework.build.util.ArtifactDownloader;
import modelengine.fitframework.build.util.JarPackager;
import modelengine.fitframework.launch.AggregatedFitLauncher;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipOutputStream;

/**
 * 为启动程序提供打包程序。
 *
 * @author 梁济时
 * @since 2023-02-02
 */
public final class LauncherRepackager extends AbstractRepackager {
    private final ArtifactDownloader downloader;

    public LauncherRepackager(MavenProject project, Log log, ArtifactDownloader downloader) {
        super(project, log, null);
        this.downloader = downloader;
    }

    /**
     * 重新打包启动程序。
     *
     * @throws MojoExecutionException 当重新打包过程中发生异常时。
     */
    public void repackage() throws MojoExecutionException {
        File target = this.project().getArtifact().getFile();
        File backup = this.backupFile(target);
        Set<Artifact> dependencies = this.project().getArtifacts();
        List<Artifact> baseArtifacts = prepareBaseArtifacts(dependencies, this.downloader);
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(target.toPath()), UTF_8)) {
            JarPackager packager = JarPackager.of(out);
            this.packageOriginalJar(packager, backup);
            this.packageBaseArtifacts(packager, baseArtifacts);
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to repackage application JAR. [file={0}]",
                    FileUtils.path(target)), ex);
        }
    }

    private void packageOriginalJar(JarPackager packager, File file) throws MojoExecutionException {
        Jar jar = loadJar(file);
        this.log().info(StringUtils.format("Prepare to repackage original jar. [from={0}]", file.getName()));
        int count = 0;
        for (Jar.Entry entry : jar.entries()) {
            if (entry.directory()) {
                continue;
            }
            if (!isJar(entry) && isUtf8(entry)) {
                packager.addEntry(entry.name(), this.getEntryContent(entry).getBytes(UTF_8));
            } else {
                packager.packageJarEntry(entry);
            }
            count++;
            this.log().debug(StringUtils.format("Add entry to launcher. [name={0}]", entry.name()));
        }
        this.log()
                .info(StringUtils.format("Repackage original jar successfully. [from={0}, entries={1}]",
                        file.getName(),
                        count));
    }

    private void packageBaseArtifacts(JarPackager packager, List<Artifact> baseArtifacts)
            throws MojoExecutionException {
        this.log().info("Prepare to package base artifacts into jar.");
        int count = 0;
        for (Artifact baseArtifact : baseArtifacts) {
            Jar jar = loadJar(baseArtifact.getFile());
            for (Jar.Entry entry : jar.entries()) {
                if (entry.directory()) {
                    continue;
                }
                if (StringUtils.startsWithIgnoreCase(entry.name(), AggregatedFitLauncher.META_DIRECTORY_ENTRY_NAME)) {
                    continue;
                }
                if (StringUtils.startsWithIgnoreCase(entry.name(), AggregatedFitLauncher.FIT_ROOT_ENTRY_NAME)) {
                    continue;
                }
                packager.packageJarEntry(entry);
                count++;
                this.log().debug(StringUtils.format("Add entry to launcher. [name={0}]", entry.name()));
            }
        }
        this.log().info(StringUtils.format("Package base artifacts into jar successfully. [entries={0}]", count));
    }
}
