/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.support;

import modelengine.fitframework.event.Event;
import modelengine.fitframework.event.EventHandler;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.type.ParameterizedTypeResolver;
import modelengine.fitframework.type.ParameterizedTypeResolvingResult;
import modelengine.fitframework.type.TypeMatcher;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 为插件提供事件发布程序。
 *
 * @author 梁济时
 * @since 2023-01-29
 */
final class PluginEventPublisher implements EventPublisher {
    private final Plugin plugin;

    PluginEventPublisher(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <E extends Event> void publishEvent(E event) {
        List<BeanFactory> factories = this.plugin.container().factories(EventHandler.class);
        List<RuntimeException> exceptions = new ArrayList<>();
        for (BeanFactory factory : factories) {
            if (handleable(factory, event)) {
                EventHandler<E> handler = factory.get();
                try {
                    handler.handleEvent(event);
                } catch (RuntimeException ex) {
                    exceptions.add(ex);
                }
            }
        }
        this.processExceptions(exceptions);
    }

    private void processExceptions(List<RuntimeException> exceptions) {
        if (CollectionUtils.isEmpty(exceptions)) {
            return;
        }
        if (exceptions.size() == 1) {
            throw exceptions.get(0);
        }
        IllegalStateException combinedException = new IllegalStateException(exceptions.get(0));
        for (int i = 1; i < exceptions.size(); i++) {
            combinedException.addSuppressed(exceptions.get(i));
        }
        throw combinedException;
    }

    private static <E extends Event> boolean handleable(BeanFactory factory, E event) {
        ParameterizedTypeResolvingResult result =
                ParameterizedTypeResolver.resolve(factory.metadata().type(), EventHandler.class);
        return result.resolved() && TypeMatcher.match(event.getClass(), result.parameters().get(0));
    }
}
