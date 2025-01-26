/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.discrete;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.maven.MavenCoordinate;
import modelengine.fitframework.model.Version;
import modelengine.fitframework.plugin.PluginCategory;
import modelengine.fitframework.plugin.PluginKey;
import modelengine.fitframework.plugin.PluginMetadata;
import modelengine.fitframework.plugin.support.DefaultPluginKey;
import modelengine.fitframework.plugin.support.DefaultPluginMetadata;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.FitRuntimeStartupException;
import modelengine.fitframework.runtime.support.AbstractRootPlugin;
import modelengine.fitframework.util.ArrayUtils;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 为根插件提供离散启动的实现。
 *
 * @author 季聿阶
 * @since 2023-07-29
 */
public class DiscreteRootPlugin extends AbstractRootPlugin {
    private final FitRuntime runtime;
    private final File frameworkDirectory;
    private final PluginMetadata metadata;

    DiscreteRootPlugin(FitRuntime runtime) {
        this.runtime = runtime;
        this.frameworkDirectory = notNull(FileUtils.file(runtime.location()),
                () -> new FitRuntimeStartupException("The framework directory cannot be null."));
        isTrue(this.frameworkDirectory.isDirectory(),
                () -> new FitRuntimeStartupException("The framework directory is not a directory."));
        this.metadata = buildPluginMetadata(this.frameworkDirectory, runtime.location());
    }

    private static PluginMetadata buildPluginMetadata(File frameworkDirectory, URL location) {
        // 框架目录下存在 fit-discrete-launcher-*.jar 的启动包
        File[] files = frameworkDirectory.listFiles((file, name) -> isLauncher(name));
        if (ArrayUtils.isEmpty(files)) {
            throw new FitRuntimeStartupException("Failed to locate launcher JAR.");
        }
        File launcher = files[0];

        Jar jar;
        try {
            jar = Jar.from(launcher);
        } catch (IOException e) {
            throw new FitRuntimeStartupException(StringUtils.format("Failed to load JAR of launcher. [location={0}]",
                    FileUtils.path(launcher)), e);
        }
        MavenCoordinate coordinate;
        try {
            coordinate = MavenCoordinate.read(jar);
        } catch (IOException e) {
            throw new FitRuntimeStartupException(StringUtils.format(
                    "Failed to read maven coordinate from JAR. [jar={0}]",
                    jar), e);
        }
        PluginKey pluginKey = new DefaultPluginKey(coordinate.groupId(),
                coordinate.artifactId(),
                Version.parse(coordinate.version()));
        return new DefaultPluginMetadata(pluginKey, location, PluginCategory.SYSTEM, Integer.MIN_VALUE);
    }

    private static boolean isLauncher(String name) {
        return StringUtils.startsWithIgnoreCase(name, "fit-discrete-launcher") && StringUtils.endsWithIgnoreCase(name,
                Jar.FILE_EXTENSION);
    }

    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    public ClassLoader pluginClassLoader() {
        return DiscreteRootPlugin.class.getClassLoader();
    }

    @Override
    public FitRuntime runtime() {
        return this.runtime;
    }

    @Override
    protected void loadPlugins() {
        File pluginDirectory = new File(this.frameworkDirectory, "plugins");
        isTrue(pluginDirectory.isDirectory(),
                () -> new FitRuntimeStartupException("The plugin directory is not a directory."));
        File[] pluginFiles =
                pluginDirectory.listFiles((file, name) -> StringUtils.endsWithIgnoreCase(name, Jar.FILE_EXTENSION));
        if (pluginFiles == null) {
            return;
        }
        for (File pluginFile : pluginFiles) {
            this.loadPlugin(FileUtils.urlOf(pluginFile));
        }
    }
}
