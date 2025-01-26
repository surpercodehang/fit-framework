/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import modelengine.fitframework.protocol.jar.support.DefaultJarCache;

import java.io.IOException;

/**
 * 为 JAR 提供缓存。
 *
 * @author 梁济时
 * @since 2023-02-17
 */
public interface JarCache {
    /**
     * 获取指定位置的 JAR。
     *
     * @param location 表示 JAR 的位置的 {@link JarLocation}。
     * @return 表示该位置的 JAR 的 {@link Jar}。
     * @throws IllegalArgumentException {@code location} 为 {@code null}。
     * @throws JarNotFoundException 该位置不存在 JAR。
     * @throws JarFormatException JAR 格式不正确。
     * @throws IOException 当加载 JAR 过程发生输入输出异常时。
     */
    Jar get(JarLocation location) throws IOException;

    /**
     * 获取 Jar 文件的缓存。
     *
     * @return 表示 Jar 文件的缓存的 {@link JarCache}。
     */
    static JarCache instance() {
        return DefaultJarCache.INSTANCE;
    }
}
