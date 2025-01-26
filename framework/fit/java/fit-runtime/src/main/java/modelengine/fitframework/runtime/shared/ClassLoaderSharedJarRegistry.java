/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.shared;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.plugin.SharedJarRegistry;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 为 {@link SharedJarRegistry} 提供基于 {@link ClassLoader} 的实现。
 *
 * @author 梁济时
 * @since 2022-06-27
 */
public class ClassLoaderSharedJarRegistry implements SharedJarRegistry {
    private final SharedUrlClassLoader loader;

    /**
     * 使用类加载程序创建 {@link ClassLoaderSharedJarRegistry} 类的新实例。
     *
     * @param loader 表示将JAR注册到的类加载程序的 {@link SharedUrlClassLoader}。
     * @throws IllegalArgumentException {@code loader} 为 {@code null} 或不是 {@link URLClassLoader}。
     */
    public ClassLoaderSharedJarRegistry(SharedUrlClassLoader loader) {
        this.loader = notNull(loader, "The shared URL class loader cannot be null.");
    }

    @Override
    public void register(URL jar) {
        this.loader.addURL(jar);
    }
}
