/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import modelengine.fitframework.resource.support.DefaultClassPath;
import modelengine.fitframework.util.wildcard.Pattern;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * 表示类路径。
 *
 * @author 梁济时
 * @since 2023-01-17
 */
public interface ClassPath {
    /**
     * 获取类路径所在的类加载程序。
     *
     * @return 表示类加载程序的 {@link ClassLoader}。
     */
    ClassLoader loader();

    /**
     * 获取类路径的定位符。
     *
     * @return 表示类路径的定位符的 {@link URL}。
     * @throws MalformedURLException URL 格式不正确。
     */
    URL url() throws MalformedURLException;

    /**
     * 获取类路径中的资源树。
     *
     * @return 表示资源树的 {@link ResourceTree}。
     */
    ResourceTree resources();

    /**
     * 解析指定样式的资源。
     *
     * @param pattern 表示资源的样式的 {@link Pattern}{@code <}{@link String}{@code >}。
     * @return 表示符合样式的资源的 {@link List}{@code <}{@link ClassPathResource}{@code >}。
     */
    List<ClassPathResource> resolve(Pattern<String> pattern);

    /**
     * 为指定类加载程序中的指定 URL 创建类路径。
     *
     * @param loader 表示类加载程序的 {@link ClassLoader}。
     * @param url 表示类路径的位置的 {@link URL}。
     * @return 表示类路径的 {@link ClassPath}。
     */
    static ClassPath of(ClassLoader loader, URL url) {
        return DefaultClassPath.create(loader, url);
    }

    /**
     * 为指定类加载程序中的指定 URL 创建类路径。
     *
     * @param loader 表示类加载程序的 {@link ClassLoader}。
     * @param resources 表示类路径中的资源树的 {@link ResourceTree}。
     * @return 表示类路径的 {@link ClassPath}。
     */
    static ClassPath of(ClassLoader loader, ResourceTree resources) {
        return DefaultClassPath.create(loader, resources);
    }

    /**
     * 从指定的类加载程序中加载类路径信息。
     * <p>将通过 {@link ClassLoader#getParent()} 进行递归加载，当一个类路径的 {@link URL} 重复出现时，将优先使用
     * {@link ClassLoader#getParent()} 中的一个。</p>
     *
     * @param loader 表示待从中加载类路径的类加载程序的 {@link ClassLoader}。
     * @param recursive 若为 {@code true}，则通过 {@link ClassLoader#getParent()} 递归在所有 {@link ClassLoader}
     *                 中查找，否则仅在当前 {@link ClassLoader} 中查找。
     * @return 表示从类加载程序中加载到的所有类路径的 {@link List}{@code <}{@link ClassPath}{@code >}。
     */
    static List<ClassPath> fromClassLoader(ClassLoader loader, boolean recursive) {
        return DefaultClassPath.from(loader, recursive);
    }
}
