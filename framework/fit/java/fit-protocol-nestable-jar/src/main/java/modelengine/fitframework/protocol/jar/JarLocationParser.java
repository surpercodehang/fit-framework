/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import modelengine.fitframework.protocol.jar.location.JarLocationParserComposite;

import java.net.URL;

/**
 * 表示 JAR 位置信息的解析器。
 *
 * @author 高三海
 * @author 季聿阶
 * @since 2025-02-18
 */
public interface JarLocationParser {
    /**
     * 从指定的 URL 中解析 JAR 的位置信息。
     *
     * @param url 表示包含 JAR 的位置信息的 {@link URL}。
     * @return 表示解析到的 JAR 位置信息的 {@link JarLocation}。
     * @throws IllegalArgumentException 当 {@code url} 为 {@code null} 或未包含有效的 JAR 位置信息时。
     */
    default JarLocation parseJar(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("The URL to parse JAR location cannot be null.");
        }
        return this.parseJar(url.toExternalForm());
    }

    /**
     * 从指定的 URL 字符串中解析 JAR 的位置信息。
     *
     * @param url 表示包含 JAR 的位置信息的 {@link String}。
     * @return 表示解析到的 JAR 位置信息的 {@link JarLocation}。
     * @throws IllegalArgumentException 当 {@code url} 为空白字符串或未包含有效的 JAR 位置信息时。
     */
    JarLocation parseJar(String url);

    /**
     * 从指定的 URL 中解析 JAR 中记录的位置信息。
     *
     * @param url 表示包含 JAR 中记录的位置信息的 {@link URL}。
     * @return 表示解析到的 JAR 中记录的位置信息的 {@link JarEntryLocation}。
     * @throws IllegalArgumentException 当 {@code url} 为 {@code null} 或未包含有效的 JAR 中记录位置信息时。
     */
    default JarEntryLocation parseEntry(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("The URL to parse JAR entry location cannot be null.");
        }
        return this.parseEntry(url.toExternalForm());
    }

    /**
     * 从指定的 URL 字符串中解析 JAR 中记录的位置信息。
     *
     * @param url 表示包含 JAR 中记录的位置信息的 {@link String}。
     * @return 表示解析到的 JAR 中记录的位置信息的 {@link JarEntryLocation}。
     * @throws IllegalArgumentException 当 {@code url} 为空白字符串或未包含有效的 JAR 中记录位置信息时。
     */
    JarEntryLocation parseEntry(String url);

    /**
     * 获取 {@link JarLocationParser} 的单例对象。
     *
     * @return 表示 JAR 位置信息的解析器的 {@link JarLocationParser}。
     */
    static JarLocationParser instance() {
        return JarLocationParserComposite.INSTANCE;
    }

    /**
     * 注册一个指定的解析器，用于进行特定 JAR 格式的解析。
     *
     * @param parser 表示待注册的解析器的 {@link JarLocationParser}。
     */
    static void register(JarLocationParser parser) {
        JarLocationParserComposite.INSTANCE.register(parser);
    }

    /**
     * 取消注册一个指定的解析器，用于去除对特定 JAR 格式的解析。
     *
     * @param parser 表示待取消注册的解析器的 {@link JarLocationParser}。
     */
    static void unregister(JarLocationParser parser) {
        JarLocationParserComposite.INSTANCE.unregister(parser);
    }
}
