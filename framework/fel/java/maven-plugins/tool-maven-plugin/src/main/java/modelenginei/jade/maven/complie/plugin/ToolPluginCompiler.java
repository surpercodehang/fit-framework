/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelenginei.jade.maven.complie.plugin;

import modelengine.fel.tool.ToolSchema;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.maven.support.AbstractCompiler;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.util.FileUtils;
import modelenginei.jade.maven.complie.entity.ToolEntity;
import modelenginei.jade.maven.complie.parser.ByteBuddyToolParser;
import modelenginei.jade.maven.complie.parser.ToolParser;
import modelenginei.jade.maven.complie.util.JsonUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为工具插件提供编译程序。
 *
 * @author 杭潇
 * @author 易文渊
 * @since 2024-06-13
 */
public class ToolPluginCompiler extends AbstractCompiler {
    private static final Logger log = Logger.get(ToolPluginCompiler.class);
    private static final String TOOLS = "tools";

    ToolPluginCompiler(MavenProject project, Log log) {
        super(project, log, null);
    }

    @Override
    protected void output(String outputDirectory, String fitRootDirectory) throws MojoExecutionException {
        try (URLClassLoader classLoader = initURLClassLoader(outputDirectory)) {
            List<ToolEntity> toolEntities = scanClassFiles(classLoader, outputDirectory);
            this.outputToolManifest(outputDirectory, toolEntities);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to parse class files.", e);
        }
    }

    private URLClassLoader initURLClassLoader(String outputDirectory) throws IOException {
        List<URL> urls = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(outputDirectory, FIT_ROOT_DIRECTORY))) {
            List<File> files = paths.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
            for (File file : files) {
                if (!FileUtils.isJar(file)) {
                    continue;
                }
                urls.add(Jar.from(file).location().toUrl());
            }
            urls.add(Paths.get(outputDirectory).toUri().toURL());
        }
        return new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
    }

    private List<ToolEntity> scanClassFiles(ClassLoader classLoader, String outputDirectory) throws IOException {
        ToolParser toolParser = new ByteBuddyToolParser(classLoader, outputDirectory);
        try (Stream<Path> paths = Files.walk(Paths.get(outputDirectory))) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(FileUtils::isClass)
                    .flatMap(path -> toolParser.parseTool(path).stream())
                    .collect(Collectors.toList());
        }
    }

    private void outputToolManifest(String outputDirectory, List<ToolEntity> toolEntities) throws IOException {
        if (toolEntities.isEmpty()) {
            return;
        }
        File jsonFile = Paths.get(outputDirectory, ToolSchema.TOOL_MANIFEST).toFile();
        Map<String, Object> toolMap = new HashMap<>();
        toolMap.put(TOOLS, toolEntities.stream().map(ToolEntity::normalize).collect(Collectors.toList()));
        JsonUtils.OBJECT_MAPPER.writeValue(jsonFile, toolMap);
        log.info("Write tool json successfully. [file={}]", jsonFile.getName());
    }
}
