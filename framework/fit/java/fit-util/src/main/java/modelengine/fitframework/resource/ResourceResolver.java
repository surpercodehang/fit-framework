/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import modelengine.fitframework.resource.support.ClassLoaderResourceResolver;

import java.io.IOException;

/**
 * 为资源提供解析程序。
 *
 * @author 梁济时
 * @since 2022-07-21
 */
public interface ResourceResolver {
    /**
     * 表示资源路径的分隔符。
     */
    char PATH_SEPARATOR = '/';

    /**
     * 表示资源路径中的通配符。
     */
    char WILDCARD = '*';

    /**
     * 解析指定样式的资源。
     *
     * @param pattern 表示用以匹配资源的样式的 {@link String}。
     * @return 表示匹配到的资源的 {@link Resource}{@code []}。
     * @throws IOException 解析过程发生输入输出异常。
     */
    Resource[] resolve(String pattern) throws IOException;

    /**
     * 获取一个资源解析程序，用以在指定类加载程序中解析资源。
     *
     * @param loader 表示资源所在的类加载程序的 {@link ClassLoader}。
     * @return 表示资源解析程序的 {@link ResourceResolver}。
     * @throws IllegalArgumentException {@code loader} 为 {@code null}。
     */
    static ResourceResolver forClassLoader(ClassLoader loader) {
        return new ClassLoaderResourceResolver(loader);
    }
}
