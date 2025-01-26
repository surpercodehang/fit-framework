/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.plugin.dynamic.directory;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.conf.runtime.ApplicationConfig;
import modelengine.fitframework.filesystem.DirectoryMonitor;
import modelengine.fitframework.filesystem.FileChangedObserver;
import modelengine.fitframework.filesystem.FileCreatedObserver;
import modelengine.fitframework.filesystem.FileDeletedObserver;
import modelengine.fitframework.filesystem.FileObservers;
import modelengine.fitframework.filesystem.FileTreeVisitedObserver;
import modelengine.fitframework.filesystem.FileTreeVisitingObserver;
import modelengine.fitframework.filesystem.FileVisitedFailedObserver;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginComparators;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.FitRuntimeStartedObserver;
import modelengine.fitframework.schedule.ExecutePolicy;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示动态插件的扫描器。
 * <p>该扫描器会定期扫描指定目录，将目录中新添加的插件动态地加载起来，同时，当一个插件被移除该目录时，也可以动态地卸载该插件。</p>
 *
 * @author 季聿阶
 * @since 2023-08-07
 */
@Component
public class DynamicPluginScanner
        implements FitRuntimeStartedObserver, FileCreatedObserver, FileChangedObserver, FileDeletedObserver,
        FileTreeVisitingObserver, FileTreeVisitedObserver, FileVisitedFailedObserver {
    private static final Logger log = Logger.get(DynamicPluginScanner.class);

    private final FitRuntime runtime;
    private final DirectoryMonitor monitor;
    private final Path tempDirectory;

    private final Map<File, File> toStartPluginFiles = new HashMap<>();
    private final List<Plugin> toStartPlugins = new ArrayList<>();
    private final List<Plugin> toStopPlugins = new ArrayList<>();

    /**
     * 创建动态插件扫描器。
     * <p>当 {@code directory} 为空白字符串时，动态插件功能自动关闭。</p>
     *
     * @param runtime 表示运行时环境的 {@link FitRuntime}。
     * @param directory 表示扫描的目录的 {@link String}。
     * @param applicationConfig 表示应用配置的 {@link ApplicationConfig}。
     */
    public DynamicPluginScanner(FitRuntime runtime, @Value("${directory}") String directory,
            ApplicationConfig applicationConfig) {
        this.runtime = notNull(runtime, "The runtime cannot be null.");
        String actualDirectory = this.getDynamicDirectory(runtime, directory);
        if (StringUtils.isBlank(actualDirectory)) {
            log.warn("The config of dynamic directory is blank, the dynamic plugin is disabled. "
                    + "[config='plugin.fit.dynamic.plugin.directory']");
            this.monitor = null;
            this.tempDirectory = null;
            return;
        }
        File root = new File(actualDirectory);
        isTrue(root.isDirectory(), "The directory to monitor must be a directory. [directory={0}]", directory);
        this.monitor = DirectoryMonitor.create(root,
                Collections.singleton(Jar.FILE_EXTENSION),
                FileObservers.builder()
                        .created(this)
                        .changed(this)
                        .deleted(this)
                        .visitedFailed(this)
                        .treeVisiting(this)
                        .treeVisited(this)
                        .build(),
                ExecutePolicy.fixedDelay(1000),
                (thread, cause) -> log.error("Failed to scan directory.", cause));
        notNull(applicationConfig, "The application config cannot be null.");
        try {
            this.tempDirectory = Files.createTempDirectory(applicationConfig.name() + "-");
            this.tempDirectory.toFile().deleteOnExit();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create temp directory for dynamic plugin.", e);
        }
    }

    private String getDynamicDirectory(FitRuntime runtime, String directory) {
        String config = runtime.config().get("plugin.fit.dynamic.plugin.directory", String.class);
        if (StringUtils.isNotBlank(config)) {
            return config;
        }
        return directory;
    }

    @Override
    public void onRuntimeStarted(FitRuntime runtime) {
        if (this.monitor != null) {
            this.monitor.start();
        }
    }

    @Override
    public void onFileChanged(File file) throws IOException {
        log.info("Dynamic plugin changed. [plugin={}]", FileUtils.path(file));
        this.clearCopiedFiles(file);
        File copied = this.copyFile(file);
        this.toStartPluginFiles.put(file, copied);
        URL url = FileUtils.urlOf(copied);
        Plugin toStopPlugin = this.runtime.root().unloadPlugin(url);
        this.toStopPlugins.add(toStopPlugin);
        log.info("Dynamic plugin unloaded. [plugin={}]", FileUtils.path(file));
        Plugin toStartPlugin = this.runtime.root().loadPlugin(url);
        this.toStartPlugins.add(toStartPlugin);
        log.info("Dynamic plugin loaded. [name={}]", toStartPlugin.metadata().name());
    }

    @Override
    public void onFileCreated(File file) throws IOException {
        log.info("Dynamic plugin detected. [plugin={}]", FileUtils.path(file));
        this.clearCopiedFiles(file);
        File copied = this.copyFile(file);
        this.toStartPluginFiles.put(file, copied);
        Plugin plugin = this.runtime.root().loadPlugin(FileUtils.urlOf(copied));
        this.toStartPlugins.add(plugin);
        log.info("Dynamic plugin loaded. [name={}]", plugin.metadata().name());
    }

    private void clearCopiedFiles(File file) {
        Optional.ofNullable(this.toStartPluginFiles.get(file)).ifPresent(this::tryRemove);
        this.toStartPluginFiles.remove(file);
    }

    private void tryRemove(File copied) {
        try {
            FileUtils.delete(copied);
        } catch (IllegalStateException e) {
            log.warn("Failed to remove copied file, ignored. [file={}, reason={}]", copied.getName(), e.getMessage());
        }
    }

    private File copyFile(File file) throws IOException {
        Path tempPath = Files.createTempFile(this.tempDirectory,
                FileUtils.ignoreExtension(file.getName()) + "-",
                FileUtils.extension(file.getName()));
        File tempFile = tempPath.toFile();
        tempFile.deleteOnExit();
        Files.copy(file.toPath(), tempPath, StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }

    private static void startPlugin(Plugin plugin) {
        try {
            plugin.start();
            log.info("Dynamic plugin started. [name={}]", plugin.metadata().name());
        } catch (Throwable cause) {
            log.warn("Failed to start plugin, prepare to stop it. [name={}]", plugin.metadata().name(), cause);
            boolean isStopped = stopPlugin(plugin);
            if (isStopped) {
                log.warn("Plugin stopped. [name={}]", plugin.metadata().name());
            }
        }
    }

    private static boolean stopPlugin(Plugin plugin) {
        if (plugin == null) {
            return true;
        }
        try {
            plugin.stop();
            log.info("Dynamic plugin stopped. [name={}]", plugin.metadata().name());
            return true;
        } catch (Throwable cause) {
            log.warn("Failed to stop plugin, ignored. [name={}]", plugin.metadata().name(), cause);
            return false;
        }
    }

    @Override
    public void onFileDeleted(File file) {
        log.info("Dynamic plugin missed. [plugin={}]", FileUtils.path(file));
        Plugin plugin = this.runtime.root().unloadPlugin(FileUtils.urlOf(file));
        this.toStopPlugins.add(plugin);
        log.info("Dynamic plugin unloaded. [plugin={}]", FileUtils.path(file));
        this.clearCopiedFiles(file);
    }

    @Override
    public void onFileVisitedFailed(File file, IOException exception) {
        log.warn("Failed to load plugin. [plugin={}, cause={}]", FileUtils.path(file), exception.getMessage());
    }

    @Override
    public void onFileTreeVisiting(File root) {
        this.toStopPlugins.clear();
        this.toStartPlugins.clear();
    }

    @Override
    public void onFileTreeVisited(File root) {
        this.toStopPlugins.sort(PluginComparators.STARTUP);
        Collections.reverse(this.toStopPlugins);
        this.toStopPlugins.forEach(DynamicPluginScanner::stopPlugin);
        this.toStartPlugins.sort(PluginComparators.STARTUP);
        this.toStartPlugins.forEach(DynamicPluginScanner::startPlugin);
    }
}
