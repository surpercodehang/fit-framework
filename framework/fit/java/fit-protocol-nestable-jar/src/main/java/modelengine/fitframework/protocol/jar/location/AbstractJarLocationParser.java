/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.location;

import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarLocation;
import modelengine.fitframework.protocol.jar.JarLocationParser;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 表示 {@link JarLocationParser} 的抽象父类。
 *
 * @author 高三海
 * @author 季聿阶
 * @since 2025-02-18
 */
public abstract class AbstractJarLocationParser implements JarLocationParser {
    private static final String JAR_PREFIX = JarLocation.JAR_PROTOCOL + JarLocation.PROTOCOL_SEPARATOR;
    private static final String FILE_PREFIX = JarLocation.FILE_PROTOCOL + JarLocation.PROTOCOL_SEPARATOR;

    @Override
    public JarLocation parseJar(String url) {
        return this.parse(url, (file, nests, entry) -> {
            if (entry.isEmpty()) {
                return new DefaultJarLocation(file, nests);
            } else if (entry.charAt(entry.length() - 1) == JarEntryLocation.ENTRY_PATH_SEPARATOR) {
                List<String> actualNests = new ArrayList<>(nests.size() + 1);
                actualNests.addAll(nests);
                actualNests.add(entry);
                return new DefaultJarLocation(file, actualNests);
            } else {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The URL to parse does not point to a nestable JAR. [url=%s]",
                        url));
            }
        });
    }

    @Override
    public JarEntryLocation parseEntry(String url) {
        return this.parse(url, (file, nests, entry) -> {
            if (entry.isEmpty()) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The URL to parse does not point to an entry in a nestable JAR. [url=%s]",
                        url));
            } else {
                JarLocation jar = JarLocation.custom().file(file).nests(nests).build();
                return new DefaultJarEntryLocation(jar, entry);
            }
        });
    }

    private <T> T parse(String url, Locations.Factory<T> factory) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("The URL to parse cannot be null or an empty string.");
        } else if (url.regionMatches(true, 0, JAR_PREFIX, 0, JAR_PREFIX.length())) {
            int index = url.lastIndexOf(JarLocation.URL_PATH_SEPARATOR);
            if (index < 0) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The URL to parse must contain at least 1 '%s'. [url=%s]",
                        JarLocation.URL_PATH_SEPARATOR.length(),
                        url));
            }
            if (!this.isSupported(url, JAR_PREFIX.length(), index)) {
                return null;
            }

            File file;
            List<String> nests;
            int separatorIndex = url.indexOf(this.getSeparator(), JAR_PREFIX.length());
            if (separatorIndex < JAR_PREFIX.length() || separatorIndex >= index) {
                file = this.parseFile(url, JAR_PREFIX.length(), index);
                nests = Collections.emptyList();
            } else {
                file = this.parseFile(url, JAR_PREFIX.length(), separatorIndex);
                nests = this.parseNests(url, separatorIndex + this.getSeparator().length(), index);
            }

            String entry = url.substring(index + JarLocation.URL_PATH_SEPARATOR.length());
            return factory.create(file, nests, entry);
        } else if (url.regionMatches(true, 0, FILE_PREFIX, 0, FILE_PREFIX.length())) {
            return factory.create(this.parseFile(url), Collections.emptyList(), "");
        } else {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "Unsupported protocol of JAR or entry url. [url=%s]",
                    url));
        }
    }

    private File parseFile(String url) {
        try {
            URI uri = new URI(url);
            return new File(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The root file is not a valid URI. [url=%s]",
                    url));
        }
    }

    private File parseFile(String url, int start, int stop) {
        try {
            String uriStr = this.process(url.substring(start, stop));
            URI uri = new URI(uriStr);
            return new File(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The root file is not a valid URI. [url=%s]",
                    url));
        }
    }

    private List<String> parseNests(String url, int beginIndex, int endIndex) {
        if (endIndex - beginIndex < 1) {
            return Collections.emptyList();
        }
        List<String> nests = new LinkedList<>();
        int index;
        int startPos = beginIndex;
        while ((index = url.indexOf(this.getSeparator(), startPos)) > startPos && index < endIndex) {
            nests.add(url.substring(startPos, index));
            startPos = index + this.getSeparator().length();
        }
        nests.add(url.substring(startPos, endIndex));
        return nests;
    }

    /**
     * 判断当前解析器是否支持解析指定的 URL 字符串。
     *
     * @param url 表示包含指定 URL 的字符串的 {@link String}。
     * @param start 表示指定 URL 内容的起始位置的 {@code int}，包含 {@code start}。
     * @param stop 表示指定 URL 内容的结束位置的 {@code int}，不包含 {@code stop}。
     * @return 如果支持解析，则返回 {@code true}，否则，返回 {@code false}。
     */
    protected abstract boolean isSupported(String url, int start, int stop);

    /**
     * 获取当前解析器的分隔符。
     *
     * @return 表示当前解析器的分隔符的 {@link String}。
     */
    protected abstract String getSeparator();

    /**
     * 对 URI 内容进行特殊处理，有些解析器存在特殊逻辑，需要预留，默认不处理。
     *
     * @param toProcess 表示待特殊处理的 URI 内容的 {@link String}。
     * @return 表示特殊处理完毕的 URI 内容的 {@link String}。
     */
    protected abstract String process(String toProcess);
}
