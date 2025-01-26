/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import modelengine.fitframework.protocol.jar.support.Locations;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 表示 JAR 的位置。
 *
 * @author 梁济时
 * @since 2022-10-07
 */
public interface JarEntryLocation {
    /**
     * 表示条目名称中的路径分隔符。
     */
    char ENTRY_PATH_SEPARATOR = '/';

    /**
     * 获取记录所在归档件的位置。
     *
     * @return 表示归档件的位置的 {@link JarLocation}。
     */
    JarLocation jar();

    /**
     * 获取 JAR 中条目的名称。
     *
     * @return 表示条目名称的 {@link String}。
     */
    String entry();

    /**
     * 返回 JAR 位置的 URL 表现形式。
     *
     * @return 表示当前 JAR 位置的 URL 的 {@link URL}。
     * @throws MalformedURLException 当归档记录位置中的 URL 格式不正确时。
     */
    URL toUrl() throws MalformedURLException;

    /**
     * 返回以当前记录作为 JAR 使用时的位置。
     *
     * @return 表示 JAR 位置的 {@link JarLocation}。
     */
    JarLocation asJar();

    /**
     * 为 {@link JarEntryLocation} 提供构建程序。
     *
     * @author 梁济时
     * @since 2022-10-07
     */
    interface Builder {
        /**
         * 设置记录所在的归档件。
         *
         * @param jar 表示所在归档件的 {@link JarLocation}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder jar(JarLocation jar);

        /**
         * 设置 JAR 中条目的名称。
         *
         * @param entry 表示条目名称的 {@link String}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder entry(String entry);

        /**
         * 构建 JAR 的位置信息。
         *
         * @return 表示新构建的位置信息的 {@link JarEntryLocation}。
         */
        JarEntryLocation build();
    }

    /**
     * 返回一个构建程序，用以构建新的 JAR 位置信息。
     *
     * @return 表示用以构建 JAR 位置信息的构建程序的 {@link Builder}。
     */
    static Builder custom() {
        return Locations.createBuilderForJarEntryLocation(null);
    }

    /**
     * 使用当前位置信息作为初始值，返回一个构建程序，用以构建位置信息的新实例。
     *
     * @return 表示用以位置信息新实例的构建程序的 {@link Builder}。
     */
    default Builder copy() {
        return Locations.createBuilderForJarEntryLocation(this);
    }

    /**
     * 从指定的文本中解析 JAR 中记录的位置信息。
     *
     * @param url 表示包含 JAR 中记录的位置信息的 {@link URL}。
     * @return 表示解析到的 JAR 中记录的位置信息的 {@link JarEntryLocation}。
     * @throws IllegalArgumentException {@code url} 为 {@code null} 或未包含有效的 JAR 中记录位置信息。
     */
    static JarEntryLocation parse(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("The URL to parse JAR entry location cannot be null.");
        } else {
            return parse(url.toExternalForm());
        }
    }

    /**
     * 从指定的文本中解析 JAR 中记录的位置信息。
     *
     * @param url 表示包含 JAR 中记录的位置信息的 URL 字符串的 {@link String}。
     * @return 表示解析到的 JAR 中记录位置信息的 {@link JarEntryLocation}。
     * @throws IllegalArgumentException {@code url} 为 {@code null} 或未包含有效的 JAR 中记录位置信息。
     */
    static JarEntryLocation parse(String url) {
        return Locations.parseJarEntry(url);
    }
}
