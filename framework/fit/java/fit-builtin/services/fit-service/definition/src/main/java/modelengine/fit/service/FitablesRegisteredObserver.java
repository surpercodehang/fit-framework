/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanFactoryOrderComparator;

/**
 * 表示全部的服务实现都已经注册完毕的事件。
 *
 * @author 季聿阶
 * @since 2022-09-12
 */
@FunctionalInterface
public interface FitablesRegisteredObserver {
    /**
     * 当所有的服务实现都已经注册完毕时，调用的方法。
     */
    void onFitablesRegistered();

    /**
     * 通知所有容器中所有实现了 {@link FitablesRegisteredObserver} 接口的 Bean。
     *
     * @param container 表示已初始化完成的 Bean 容器的 {@link BeanContainer}。
     */
    static void notify(BeanContainer container) {
        if (container == null) {
            return;
        }
        container.all(FitablesRegisteredObserver.class)
                .stream()
                .sorted(BeanFactoryOrderComparator.INSTANCE)
                .map(BeanFactory::<FitablesRegisteredObserver>get)
                .forEach(FitablesRegisteredObserver::onFitablesRegistered);
    }
}
