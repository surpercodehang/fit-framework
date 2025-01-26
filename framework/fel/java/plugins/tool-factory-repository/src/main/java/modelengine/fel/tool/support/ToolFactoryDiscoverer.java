/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.tool.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.tool.ToolFactory;
import modelengine.fel.tool.ToolFactoryRepository;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link ToolFactory} 的自动装配器。
 *
 * @author 易文渊
 * @since 2024-08-15
 */
@Component
public class ToolFactoryDiscoverer implements PluginStartedObserver, PluginStoppingObserver {
    private final ToolFactoryRepository factoryRepository;

    /**
     * 创建工具工厂的自动装配器实例。
     *
     * @param factoryRepository 表示工具工厂存储的 {@link ToolFactoryRepository}。
     */
    public ToolFactoryDiscoverer(ToolFactoryRepository factoryRepository) {
        this.factoryRepository = notNull(factoryRepository, "The tool factory repository cannot be null.");
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        scanToolFactory(plugin).forEach(this.factoryRepository::register);
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        scanToolFactory(plugin).forEach(this.factoryRepository::unregister);
    }

    private static List<ToolFactory> scanToolFactory(Plugin plugin) {
        return plugin.container()
                .factories(ToolFactory.class)
                .stream()
                .map(BeanFactory::<ToolFactory>get)
                .collect(Collectors.toList());
    }
}