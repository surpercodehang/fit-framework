/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreator;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDecorator;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycle;

/**
 * 为 {@link BeanLifecycle} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public class DefaultBeanLifecycle implements BeanLifecycle {
    private final BeanMetadata metadata;
    private final BeanCreator creator;
    private final BeanDecorator decorator;
    private final BeanInjector injector;
    private final BeanInitializer initializer;
    private final BeanDestroyer destroyer;

    /**
     * 使用Bean的定义、创建程序、装饰程序、注入程序、初始化程序和销毁程序初始化 {@link DefaultBeanLifecycle} 类的新实例。
     *
     * @param metadata 表示Bean的元数据的 {@link BeanMetadata}。
     * @param creator 表示Bean的创建程序的 {@link BeanCreator}。
     * @param decorator 表示Bean的装饰程序的 {@link BeanDecorator}。
     * @param injector 表示Bean的注入程序的 {@link BeanInjector}。
     * @param initializer 表示Bean的初始化程序的 {@link BeanInitializer}。
     * @param destroyer 表示Bean的销毁程序的 {@link BeanDestroyer}。
     */
    public DefaultBeanLifecycle(BeanMetadata metadata, BeanCreator creator, BeanDecorator decorator,
            BeanInjector injector, BeanInitializer initializer, BeanDestroyer destroyer) {
        this.metadata = metadata;
        this.creator = creator;
        this.decorator = decorator;
        this.injector = injector;
        this.initializer = initializer;
        this.destroyer = destroyer;
    }

    @Override
    public BeanMetadata metadata() {
        return this.metadata;
    }

    @Override
    public Object create(Object[] arguments) {
        if (this.creator == null) {
            return null;
        } else {
            return this.creator.create(arguments);
        }
    }

    @Override
    public Object decorate(Object bean) {
        if (this.decorator == null) {
            return bean;
        } else {
            return this.decorator.decorate(bean);
        }
    }

    @Override
    public void inject(Object bean) {
        if (this.injector != null) {
            this.injector.inject(bean);
        }
    }

    @Override
    public void initialize(Object bean) {
        if (this.initializer != null) {
            this.initializer.initialize(bean);
        }
    }

    @Override
    public void destroy(Object bean) {
        if (this.destroyer != null) {
            try {
                this.destroyer.destroy(bean);
            } catch (Throwable ignored) {
                // ignore
            }
        }
    }
}
