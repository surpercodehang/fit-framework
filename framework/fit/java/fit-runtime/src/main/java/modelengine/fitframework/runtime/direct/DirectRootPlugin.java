/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.direct;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.model.Version;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginCategory;
import modelengine.fitframework.plugin.PluginKey;
import modelengine.fitframework.plugin.PluginMetadata;
import modelengine.fitframework.plugin.support.DefaultPluginKey;
import modelengine.fitframework.plugin.support.DefaultPluginMetadata;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.support.AbstractRootPlugin;
import modelengine.fitframework.util.ObjectUtils;

import java.net.URL;

/**
 * 为根插件提供直接调用启动的实现。
 *
 * @author 梁济时
 * @since 2023-02-07
 */
final class DirectRootPlugin extends AbstractRootPlugin {
    private static final String APP_GROUP_CONFIG_KEY = "application.group";
    private static final String APP_NAME_CONFIG_KEY = "application.name";
    private static final String APP_VERSION_CONFIG_KEY = "application.version";

    private final FitRuntime runtime;
    private final PluginMetadata metadata;

    DirectRootPlugin(FitRuntime runtime) {
        this.runtime = runtime;
        this.metadata = buildPluginMetadata(runtime.config(), runtime.location());
    }

    private static PluginMetadata buildPluginMetadata(Config config, URL location) {
        String group = config.get(APP_GROUP_CONFIG_KEY, String.class);
        String name = config.get(APP_NAME_CONFIG_KEY, String.class);
        String version = config.get(APP_VERSION_CONFIG_KEY, String.class);
        // fit-runtime 包在 lib 目录中，lib 目录的上级目录为应用程序根目录
        PluginKey pluginKey = new DefaultPluginKey(ObjectUtils.nullIf(group, "direct.group"),
                ObjectUtils.nullIf(name, "direct"),
                Version.parse(ObjectUtils.nullIf(version, "0.0.1-direct")));
        return new DefaultPluginMetadata(pluginKey, location, PluginCategory.SYSTEM, Integer.MIN_VALUE);
    }

    @Nonnull
    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Nonnull
    @Override
    public ClassLoader pluginClassLoader() {
        return DirectRootPlugin.class.getClassLoader();
    }

    @Nonnull
    @Override
    public FitRuntime runtime() {
        return this.runtime;
    }

    @Override
    protected void loadPlugins() {
        // 插件都被加载到一起，没有单独的加载插件逻辑
    }

    @Override
    public Plugin loadPlugin(URL plugin) {
        return this;
    }

    @Override
    public Plugin unloadPlugin(URL plugin) {
        return this;
    }
}
