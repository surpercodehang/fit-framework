/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.container;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanFactoryOrderComparator;

/**
 * 为 Bean 容器初始化后提供观察者。
 *
 * @author 梁济时
 * @since 2022-06-08
 */
@FunctionalInterface
public interface BeanContainerInitializedObserver {
    /**
     * 当 Bean 容器被初始化后调用的方法。
     *
     * @param container 表示初始化的容器的 {@link BeanContainer}。
     */
    void onBeanContainerInitialized(BeanContainer container);

    /**
     * 通知所有容器中所有实现了 {@link BeanContainerInitializedObserver} 接口的 Bean。
     *
     * @param container 表示已初始化完成的 Bean 容器的 {@link BeanContainer}。
     */
    static void notify(BeanContainer container) {
        if (container == null) {
            return;
        }
        container.all(BeanContainerInitializedObserver.class)
                .stream()
                .sorted(BeanFactoryOrderComparator.INSTANCE)
                .map(BeanFactory::<BeanContainerInitializedObserver>get)
                .forEach(observer -> observer.onBeanContainerInitialized(container));
    }
}
