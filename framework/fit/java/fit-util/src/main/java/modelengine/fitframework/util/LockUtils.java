/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Validation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * 为 {@link Lock} 提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class LockUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private LockUtils() {}

    /**
     * 初始化一个可重入锁的新实例。
     *
     * @return 表示可重入锁的 {@link Lock}。
     */
    public static Lock newReentrantLock() {
        return new ReentrantLock();
    }

    /**
     * 初始化一个可重入的读写锁的新实例。
     *
     * @return 表示可重入读写锁的 {@link ReadWriteLock}。
     */
    public static ReadWriteLock newReentrantReadWriteLock() {
        return new ReentrantReadWriteLock();
    }

    /**
     * 初始化一个轻量级的同步块锁的新实例。
     *
     * @return 表示同步块锁的 {@link Object}。
     */
    public static Object newSynchronizedLock() {
        return new byte[0];
    }

    /**
     * 使用指定的 {@link Lock} 实例作为同步锁，执行方法并返回结果。
     *
     * @param lock 表示作为同步锁的 {@link Lock}。
     * @param supplier 表示用以获取结果的方法的 {@link BooleanSupplier}。
     * @return 表示 {@code supplier} 方法返回值的 {@code boolean}。
     * @throws IllegalArgumentException 当 {@code supplier} 为 {@code null} 时。
     */
    public static boolean synchronize(Lock lock, BooleanSupplier supplier) {
        Validation.notNull(supplier, "The supplier to get value cannot be null.");
        if (lock == null) {
            return supplier.getAsBoolean();
        }
        lock.lock();
        try {
            return supplier.getAsBoolean();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 使用指定的 {@link Lock} 实例作为同步锁，执行方法并返回结果。
     *
     * @param lock 表示作为同步锁的 {@link Lock}。
     * @param supplier 表示用以获取结果的方法的 {@link IntSupplier}。
     * @return 表示 {@code supplier} 方法返回值的 {@code int}。
     * @throws IllegalArgumentException 当 {@code supplier} 为 {@code null} 时。
     */
    public static int synchronize(Lock lock, IntSupplier supplier) {
        Validation.notNull(supplier, "The supplier to get value cannot be null.");
        if (lock == null) {
            return supplier.getAsInt();
        }
        lock.lock();
        try {
            return supplier.getAsInt();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 使用指定的 {@link Lock} 实例作为同步锁，执行指定的方法。
     *
     * @param lock 表示作为同步锁的 {@link Lock}。
     * @param action 表示待同步执行的方法的 {@link Runnable}。
     * @throws IllegalArgumentException 当 {@code action} 为 {@code null} 时。
     */
    public static void synchronize(Lock lock, Runnable action) {
        Validation.notNull(action, "The action to perform synchronously cannot be null.");
        if (lock == null) {
            action.run();
            return;
        }
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 使用指定的 {@link Lock} 实例作为同步锁，执行方法并返回结果。
     *
     * @param lock 表示作为同步锁的 {@link Lock}。
     * @param supplier 表示用以获取结果的方法的 {@link Supplier}{@code <}{@link T}{@code >}。
     * @param <T> 表示返回值的类型的 {@link T}。
     * @return 表示 {@code supplier} 方法返回的值的 {@link T}。
     * @throws IllegalArgumentException 当 {@code supplier} 为 {@code null} 时。
     */
    public static <T> T synchronize(Lock lock, Supplier<T> supplier) {
        Validation.notNull(supplier, "The supplier to get value cannot be null.");
        if (lock == null) {
            return supplier.get();
        }
        lock.lock();
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }
}
