/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.scan;

import modelengine.fitframework.jvm.scan.support.ClassLoaderPackageScanner;

import java.util.Collection;

/**
 * 为包提供扫描程序。
 *
 * @author 梁济时
 * @since 2023-02-01
 */
public interface PackageScanner {
    /**
     * 扫描指定包。
     *
     * @param toScanPackages 表示待扫描的包的 {@link Collection}{@code <}{@link String}{@code >}。
     */
    void scan(Collection<String> toScanPackages);

    /**
     * 使用指定的入口类作为基础进行扫描。
     *
     * @param entry 表示入口类的 {@link Class}。
     */
    @Deprecated
    void scan(Class<?> entry);

    /**
     * 为包扫描提供回调方法。
     *
     * @author 梁济时
     * @since 2023-02-01
     */
    @FunctionalInterface
    interface Callback {
        /**
         * 通知扫描到了类型。
         *
         * @param scanner 表示扫描到类型的扫描程序的 {@link PackageScanner}。
         * @param clazz 表示扫描到的类型的 {@link Class}。
         */
        void notify(PackageScanner scanner, Class<?> clazz);
    }

    /**
     * 创建包扫描器。
     *
     * @param loader 表示待扫描的类加载程序的 {@link ClassLoader}。
     * @param callback 表示当扫描到类型时的回调方法的 {@link Callback}。
     * @return 表示创建出来的包扫描器的 {@link PackageScanner}。
     * @throws IllegalArgumentException 当 {@code loader} 或 {@code callback} 为 {@code null} 时。
     */
    static PackageScanner forClassLoader(ClassLoader loader, Callback callback) {
        return new ClassLoaderPackageScanner(loader, callback);
    }
}
