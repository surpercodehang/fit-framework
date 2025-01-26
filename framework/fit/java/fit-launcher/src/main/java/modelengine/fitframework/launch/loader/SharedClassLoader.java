/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.launch.loader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 为应用程序使用的公共类库提供类加载程序。
 *
 * @author 梁济时
 * @since 2022-06-28
 */
public class SharedClassLoader extends AbstractClassLoader {
    private SharedClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    private static void add(URLClassLoader loader, URL[] jars) {
        Method method;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Method not found: URLClassLoader.addURL(URL)", e);
        }
        method.setAccessible(true);
        for (URL jar : jars) {
            try {
                method.invoke(loader, jar);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to access URLClassLoader.addUrl(URL) method.", e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to add JAR to URLClassLoader.", e.getCause());
            }
        }
    }

    /**
     * 创建公共类加载程序。
     * <p>如果当前类加载程序（加载了 {@link SharedClassLoader} 的类加载程序）是一个 {@link URLClassLoader}，则直接使用当前类加载程序。</p>
     *
     * @param home 表示 FIT 根目录的 {@link File}。
     * @return 表示公共类加载程序的 {@link URLClassLoader}。
     */
    public static URLClassLoader create(File home) {
        URL[] sharedJars = jars(new File(home, "shared"));
        ClassLoader current = SharedClassLoader.class.getClassLoader();
        URLClassLoader sharedClassLoader;
        if (current instanceof URLClassLoader) {
            sharedClassLoader = (URLClassLoader) current;
            SharedClassLoader.add(sharedClassLoader, sharedJars);
        } else {
            sharedClassLoader = new SharedClassLoader(sharedJars, current);
        }
        return sharedClassLoader;
    }
}
