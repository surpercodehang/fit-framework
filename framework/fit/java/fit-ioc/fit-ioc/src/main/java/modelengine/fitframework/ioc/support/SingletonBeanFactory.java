/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.CircularDependencyException;
import modelengine.fitframework.ioc.DependencyDefinitionException;
import modelengine.fitframework.ioc.DependencyNotFoundException;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycle;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycles;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.StringUtils;

/**
 * 为 {@link BeanFactory} 提供用以单例Bean的实现。
 *
 * @author 梁济时
 * @since 2022-08-04
 */
public class SingletonBeanFactory extends AbstractBeanFactory implements BeanFactory {
    private volatile Object bean;
    private volatile boolean creating;
    private final Object lock;

    private BeanLifecycle lifecycle;

    /**
     * 使用Bean的生命周期创建 {@link SingletonBeanFactory} 类的新实例。
     *
     * @param lifecycle 表示Bean的生命周期的 {@link BeanLifecycle}。
     * @throws IllegalArgumentException {@code lifecycle} 为 {@code null}。
     */
    public SingletonBeanFactory(BeanLifecycle lifecycle) {
        super(lifecycle);

        this.bean = null;
        this.creating = false;
        this.lock = LockUtils.newSynchronizedLock();
    }

    @Override
    protected Object get0(Object... arguments) {
        if (arguments.length > 0) {
            throw new IllegalArgumentException(StringUtils.format(
                    "A singleton bean cannot accept any initial arguments. [name={0}, type={1}]",
                    this.metadata().name(),
                    this.metadata().type().getTypeName()));
        }
        if (this.bean != null) {
            return cast(this.bean);
        }
        synchronized (this.lock) {
            if (this.bean != null) {
                return cast(this.bean);
            }
            if (this.creating) {
                throw new CircularDependencyException(StringUtils.format(
                        "Circular dependency occurs when creating singleton bean. [metadata={0}]",
                        this.metadata()));
            }
            this.create();
        }
        return cast(this.bean);
    }

    private void create() {
        this.metadata().dependencies().forEach(this::checkDependency);
        this.lifecycle = BeanLifecycles.intercept(this.lifecycle());
        Object creatingBean;
        this.creating = true;
        try {
            creatingBean = this.lifecycle.create(new Object[0]);
            this.bean = this.lifecycle.decorate(creatingBean);
        } finally {
            this.creating = false;
        }
        this.lifecycle.inject(creatingBean);
        this.lifecycle.initialize(creatingBean);
    }

    private void checkDependency(String name) {
        BeanFactory dependency = this.container()
                .factory(name)
                .orElseThrow(() -> new DependencyNotFoundException(StringUtils.format(
                        "Dependency required but not found. [bean={0}, dependency={1}]",
                        this.metadata().name(),
                        name)));
        if (!dependency.metadata().singleton()) {
            throw new DependencyDefinitionException(StringUtils.format(
                    "Depends on a non-singleton bean. [bean={0}, dependency={1}]",
                    this.metadata().name(),
                    name));
        }
        dependency.get();
    }

    @Override
    protected void onDisposed() {
        synchronized (this.lock) {
            if (this.bean != null && this.lifecycle != null) {
                this.lifecycle.destroy(this.bean);
            }
            this.bean = null;
        }
        super.onDisposed();
    }

    @Override
    public String toString() {
        return this.metadata().toString();
    }
}
