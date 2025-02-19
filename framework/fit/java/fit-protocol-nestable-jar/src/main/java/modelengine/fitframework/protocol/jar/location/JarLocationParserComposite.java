/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.location;

import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarLocation;
import modelengine.fitframework.protocol.jar.JarLocationParser;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;

/**
 * 表示 {@link JarLocationParser} 的组合实现，可以在多个解析器中寻找合适的解析器进行解析。
 *
 * @author 高三海
 * @author 季聿阶
 * @since 2025-02-18
 */
public class JarLocationParserComposite implements JarLocationParser {
    /**
     * 表示 {@link JarLocationParser} 的单例对象。
     */
    public static final JarLocationParserComposite INSTANCE = new JarLocationParserComposite();

    private final Deque<JarLocationParser> parsers = new LinkedList<>();

    private JarLocationParserComposite() {
        this.parsers.add(StandardJarLocationParser.INSTANCE);
    }

    @Override
    public JarLocation parseJar(String url) {
        for (JarLocationParser parser : this.parsers) {
            JarLocation jar = parser.parseJar(url);
            if (jar != null) {
                return jar;
            }
        }
        throw new UnsupportedOperationException(String.format(Locale.ROOT,
                "No supported parser to parse JAR. [url=%s]",
                url));
    }

    @Override
    public JarEntryLocation parseEntry(String url) {
        for (JarLocationParser parser : this.parsers) {
            JarEntryLocation entry = parser.parseEntry(url);
            if (entry != null) {
                return entry;
            }
        }
        throw new UnsupportedOperationException(String.format(Locale.ROOT,
                "No supported parser to parse JAR entry. [url=%s]",
                url));
    }

    /**
     * 向组合解析器中注册一个指定的解析器。
     *
     * @param parser 表示待注册的解析器的 {@link JarLocationParser}。
     */
    public void register(JarLocationParser parser) {
        if (parser != null && parser != StandardJarLocationParser.INSTANCE) {
            this.parsers.addFirst(parser);
        }
    }

    /**
     * 从组合解析器中取消注册一个指定的解析器。
     *
     * @param parser 表示待取消注册的解析器的 {@link JarLocationParser}。
     */
    public void unregister(JarLocationParser parser) {
        if (parser != null && parser != StandardJarLocationParser.INSTANCE) {
            this.parsers.remove(parser);
        }
    }
}
