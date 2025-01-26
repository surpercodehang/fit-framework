/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.aggregated;

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
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 为根插件提供聚合启动场景的实现。
 *
 * @author 梁济时
 * @since 2023-01-31
 */
final class AggregatedRootPlugin extends AbstractRootPlugin {
    private static final String PLUGIN_ENTRY_PREFIX = "FIT-INF/plugins/";

    private final FitRuntime runtime;
    private final Jar jar;
    private final PluginMetadata metadata;

    /**
     * 使用所属的运行时初始化 {@link AggregatedRootPlugin} 类的新实例。
     *
     * @param runtime 表示所属运行时的 {@link FitRuntime}。
     */
    AggregatedRootPlugin(FitRuntime runtime) {
        this.runtime = runtime;

        try {
            this.jar = Jar.from(this.runtime.location());
        } catch (IOException e) {
            throw new FitRuntimeStartupException(StringUtils.format("Failed to load JAR of root plugin. [location={0}]",
                    this.runtime.location().toExternalForm()), e);
        }
        MavenCoordinate coordinate;
        try {
            coordinate = MavenCoordinate.read(this.jar);
        } catch (IOException e) {
            throw new FitRuntimeStartupException(StringUtils.format(
                    "Failed to read maven coordinate from JAR. [jar={0}]",
                    runtime.location().toExternalForm()), e);
        }
        PluginKey pluginKey = new DefaultPluginKey(coordinate.groupId(),
                coordinate.artifactId(),
                Version.parse(coordinate.version()));
        this.metadata =
                new DefaultPluginMetadata(pluginKey, runtime.location(), PluginCategory.SYSTEM, Integer.MIN_VALUE);
    }

    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    public ClassLoader pluginClassLoader() {
        return AggregatedRootPlugin.class.getClassLoader();
    }

    @Override
    public FitRuntime runtime() {
        return this.runtime;
    }

    @Override
    protected void loadPlugins() {
        List<Jar.Entry> pluginEntries = this.jar.entries()
                .stream()
                .filter(entry -> StringUtils.startsWithIgnoreCase(entry.name(), PLUGIN_ENTRY_PREFIX))
                .filter(entry -> StringUtils.endsWithIgnoreCase(entry.name(), Jar.FILE_EXTENSION))
                .collect(Collectors.toList());
        for (Jar.Entry pluginEntry : pluginEntries) {
            URL pluginUrl;
            try {
                pluginUrl = pluginEntry.location().asJar().toUrl();
            } catch (MalformedURLException e) {
                String message =
                        StringUtils.format("Failed to obtain URL of plugin. [location={0}]", pluginEntry.location());
                throw new FitRuntimeStartupException(message, e);
            }
            this.loadPlugin(pluginUrl);
        }
    }
}
