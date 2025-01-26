/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import modelengine.fitframework.protocol.jar.support.Locations;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * 为归档件提供位置。
 *
 * @author 梁济时
 * @since 2023-01-16
 */
public interface JarLocation {
    /**
     * 表示文件协议的名称。
     */
    String FILE_PROTOCOL = "file";

    /**
     * 表示 JAR 协议的名称。
     */
    String JAR_PROTOCOL = "jar";

    /**
     * 表示 URL 的分隔符。
     */
    String URL_PATH_SEPARATOR = "!/";

    /**
     * 表示协议的分隔符。
     */
    char PROTOCOL_SEPARATOR = ':';

    /**
     * 获取 JAR 所在的文件。
     *
     * @return 表示 JAR 所在文件的 {@link File}。
     */
    File file();

    /**
     * 获取 JAR 的内嵌路径。
     *
     * @return 表示 JAR 的内嵌路径的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> nests();

    /**
     * 返回 JAR 位置的 URL 表现形式。
     *
     * @return 表示当前 JAR 位置的 URL 的 {@link URL}。
     * @throws MalformedURLException 当归档件位置中的 URL 格式不正确时。
     */
    URL toUrl() throws MalformedURLException;

    /**
     * 获取上层位置。
     *
     * @return 若当前位置是一个嵌套位置，则为表示其上层位置的 {@link JarLocation}，否则为 {@code null}。
     */
    JarLocation parent();

    /**
     * 获取归档件中指定名称的归档记录的位置信息。
     *
     * @param name 表示归档记录的名称的 {@link String}。
     * @return 表示归档记录的位置信息的 {@link JarEntryLocation}。
     * @throws IllegalArgumentException {@code name} 为空字符串。
     */
    JarEntryLocation entry(String name);

    /**
     * 从指定的文本中解析 JAR 的位置信息。
     *
     * @param url 表示包含 JAR 的位置信息的 {@link URL}。
     * @return 表示解析到的 JAR 位置信息的 {@link JarLocation}。
     * @throws IllegalArgumentException {@code url} 为 {@code null} 或未包含有效的 JAR 位置信息。
     */
    static JarLocation parse(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("The URL to parse JAR location cannot be null.");
        } else {
            return parse(url.toExternalForm());
        }
    }

    /**
     * 从指定的文本中解析 JAR 的位置信息。
     *
     * @param url 表示包含 JAR 的位置信息的 URL 字符串的 {@link String}。
     * @return 表示解析到的 JAR 位置信息的 {@link JarLocation}。
     * @throws IllegalArgumentException {@code url} 为 {@code null} 或未包含有效的 JAR 位置信息。
     */
    static JarLocation parse(String url) {
        return Locations.parseJar(url);
    }

    /**
     * 为 {@link JarEntryLocation} 提供构建程序。
     *
     * @author 梁济时
     * @since 2022-10-07
     */
    interface Builder {
        /**
         * 设置 JAR 所在的文件。
         *
         * @param file 表示 JAR 所在文件的 {@link File}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder file(File file);

        /**
         * 追加一个嵌套路径。
         *
         * @param nest 表示待追加的嵌套路径的 {@link String}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder nest(String nest);

        /**
         * 追加一组嵌套路径。
         *
         * @param nests 表示待追加的嵌套路径的 {@link Iterable}{@code <}{@link String}{@code >}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder nests(Iterable<String> nests);

        /**
         * 构建 JAR 的位置信息。
         *
         * @return 表示新构建的位置信息的 {@link JarLocation}。
         * @throws IllegalArgumentException 未设置 {@link #file(File)}。
         */
        JarLocation build();
    }

    /**
     * 返回一个构建程序，用以构建新的 JAR 位置信息。
     *
     * @return 表示用以构建 JAR 位置信息的构建程序的 {@link Builder}。
     */
    static Builder custom() {
        return Locations.createBuilderForJarLocation(null);
    }

    /**
     * 使用当前位置信息作为初始值，返回一个构建程序，用以构建位置信息的新实例。
     *
     * @return 表示用以位置信息新实例的构建程序的 {@link Builder}。
     */
    default JarLocation.Builder copy() {
        return Locations.createBuilderForJarLocation(this);
    }
}
