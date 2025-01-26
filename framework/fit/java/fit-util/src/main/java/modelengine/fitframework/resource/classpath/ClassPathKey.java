/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.classpath;

import modelengine.fitframework.resource.classpath.support.FileClassPathKey;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 为类路径提供键的定义。
 * <p>类路径的键用以唯一标识一个类路径，可借此对类路径进行缓存。</p>
 *
 * @author 梁济时
 * @since 2022-07-27
 */
public interface ClassPathKey {
    /**
     * 获取类路径的键的名称。
     *
     * @return 表示类路径键的名称的 {@link String}。
     */
    default String name() {
        return this.toString();
    }

    /**
     * 创建类路径实例。
     *
     * @return 表示新创建的类路径实例的 {@link ClassPath}。
     * @throws IOException 创建类路径实例过程发生输入输出异常。
     */
    ClassPath create() throws IOException;

    /**
     * 从指定的类加载程序中加载类路径的键的集合。
     *
     * @param loader 表示类加载程序的 {@link ClassLoader}。
     * @return 表示从类加载程序中加载到的类路径的键的集合的 {@link Set}{@code <}{@link ClassPathKey}{@code >}。
     * @throws IOException 加载过程发生输入输出异常。
     */
    static Set<ClassPathKey> load(ClassLoader loader) throws IOException {
        if (loader == null) {
            return Collections.emptySet();
        }
        Set<ClassPathKey> keys = new HashSet<>();
        if (loader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader) loader).getURLs();
            for (URL url : urls) {
                URI uri;
                try {
                    uri = url.toURI();
                } catch (URISyntaxException ex) {
                    throw new IllegalStateException(StringUtils.format(
                            "Failed to construct URI from URL. [url={0}, error={1}]",
                            url.toExternalForm(), ex.getMessage()), ex);
                }
                UriClassPathKeyResolver.current().resolve(uri).ifPresent(keys::add);
            }
        }
        if (loader == ClassLoader.getSystemClassLoader()) {
            String classpathProperty = System.getProperty("java.class.path");
            String[] classpathStrings = StringUtils.split(classpathProperty, File.pathSeparatorChar);
            for (String classpathString : classpathStrings) {
                String actual = StringUtils.trim(classpathString);
                if (StringUtils.isEmpty(actual)) {
                    continue;
                }
                keys.add(new FileClassPathKey(new File(actual)));
            }
        }
        keys.addAll(load(loader.getParent()));
        return keys;
    }
}
