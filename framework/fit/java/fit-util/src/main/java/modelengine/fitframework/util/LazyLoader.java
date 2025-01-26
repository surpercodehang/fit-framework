/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * 为延迟加载提供公共组件。
 *
 * @param <T> 表示具体延迟加载的对象的类型的 {@link T}。
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-09-27
 */
public class LazyLoader<T> implements Supplier<T> {
    private final Supplier<T> factory;
    private final Lock lock;
    private final AtomicBoolean isLoaded = new AtomicBoolean(false);

    private volatile T instance;

    /**
     * 使用用以实例化对象的工厂初始化 {@link LazyLoader} 类的新实例。
     *
     * @param factory 表示用以实例化对象的工厂的 {@link Supplier}{@code <}{@link T}{@code >}。
     */
    public LazyLoader(Supplier<T> factory) {
        this.factory = notNull(factory, "The factory for lazy loader cannot be null.");
        this.lock = LockUtils.newReentrantLock();
    }

    /**
     * 获取对象实例。
     *
     * @return 表示对象实例的 {@link T}。
     */
    @Override
    public final T get() {
        if (this.instance != null) {
            return this.instance;
        }
        LockUtils.synchronize(this.lock, () -> {
            if (this.instance == null) {
                this.instance = this.factory.get();
                this.isLoaded.set(true);
            }
        });
        return this.instance;
    }

    /**
     * 判断当前延迟加载器是否已经加载了实例。
     *
     * @return 如果已经加载过，则返回 {@code true}，否则，返回 {@code false}。
     */
    public final boolean isLoaded() {
        return this.isLoaded.get();
    }

    /**
     * 为指定的用以创建实例的方法提供懒加载方式实现的单例的装饰程序。
     *
     * @param factory 表示用以创建对象的方法的 {@link Supplier}。
     * @param <T> 表示所创建的对象的实际类型。
     * @return 表示装饰后的用以创建实例的方法的 {@link LazyLoader}。
     * @throws IllegalArgumentException {@code factory} 为 {@code null}。
     */
    public static <T> LazyLoader<T> of(Supplier<T> factory) {
        return new LazyLoader<>(factory);
    }
}
