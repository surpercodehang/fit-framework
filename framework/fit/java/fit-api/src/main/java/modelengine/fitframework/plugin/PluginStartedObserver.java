/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin;

import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanFactoryOrderComparator;

/**
 * 表示插件启动完成的观察者。
 *
 * @author 季聿阶
 * @since 2022-09-11
 */
@FunctionalInterface
public interface PluginStartedObserver {
    /**
     * 当指定插件启动完成之后调用的方法。
     *
     * @param plugin 表示指定插件的 {@link Plugin}。
     */
    void onPluginStarted(Plugin plugin);

    /**
     * 通知所有容器中所有实现了 {@link PluginStartedObserver} 接口的 Bean。
     *
     * @param plugin 表示已经启动完毕的插件的 {@link Plugin}。
     */
    static void notify(Plugin plugin) {
        if (plugin == null) {
            return;
        }
        plugin.container()
                .all(PluginStartedObserver.class)
                .stream()
                .sorted(BeanFactoryOrderComparator.INSTANCE)
                .map(BeanFactory::<PluginStartedObserver>get)
                .forEach(observer -> observer.onPluginStarted(plugin));
    }
}
