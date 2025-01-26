/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarLocation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 为归档件和归档记录的位置信息提供工具方法。
 *
 * @author 梁济时
 * @since 2023-02-20
 */
public final class Locations {
    private static final int STRING_INITIAL_CAPACITY = 128;
    private static final String JAR_PREFIX = JarLocation.JAR_PROTOCOL + JarLocation.PROTOCOL_SEPARATOR;
    private static final String FILE_PREFIX = JarLocation.FILE_PROTOCOL + JarLocation.PROTOCOL_SEPARATOR;

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Locations() {}

    private static String toString(File file, List<String> nests, String entry) {
        String url;
        try {
            url = file.toURI().toURL().toExternalForm();
        } catch (MalformedURLException ex) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to obtain URL of file. [file=%s]",
                    path(file)), ex);
        }
        if (nests.isEmpty() && entry == null) {
            return url;
        }
        StringBuilder builder = new StringBuilder(STRING_INITIAL_CAPACITY);
        builder.append(JAR_PREFIX).append(url);
        for (String nest : nests) {
            appendUrlSeparator(builder).append(nest);
        }
        appendUrlSeparator(builder);
        if (entry != null) {
            builder.append(entry);
        }
        return builder.toString();
    }

    private static StringBuilder appendUrlSeparator(StringBuilder builder) {
        if (builder.charAt(builder.length() - 1) != JarEntryLocation.ENTRY_PATH_SEPARATOR) {
            builder.append(JarLocation.URL_PATH_SEPARATOR);
        }
        return builder;
    }

    /**
     * 表示归档件和归档记录的工厂。
     *
     * @param <T> 表示归档件或归档记录的类型的 {@link T}。
     */
    @FunctionalInterface
    interface Factory<T> {
        /**
         * 创建归档件或归档记录。
         *
         * @param file 表示所属文件的 {@link File}。
         * @param nests 表示嵌套路径的 {@link List}{@code <}{@link String}{@code >}。
         * @param entry 表示所属的入口地址的 {@link String}。
         * @return 表示创建的归档件或归档记录的 {@link T}。
         */
        T create(File file, List<String> nests, String entry);
    }

    static <T> T parse(String url, Factory<T> factory) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("The URL to parse cannot be null or an empty string.");
        } else if (url.regionMatches(true, 0, JAR_PREFIX, 0, JAR_PREFIX.length())) {
            int index = url.indexOf(JarLocation.URL_PATH_SEPARATOR);
            if (index < 0) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The URL to parse must contain at least 1 '%s'. [url=%s]",
                        JarLocation.URL_PATH_SEPARATOR.length(),
                        url));
            }
            File file = parseFile(url, JAR_PREFIX.length(), index);
            index += JarLocation.URL_PATH_SEPARATOR.length();
            List<String> nests = parseNests(url, index, index = url.lastIndexOf(JarLocation.URL_PATH_SEPARATOR));
            String entry = url.substring(index + JarLocation.URL_PATH_SEPARATOR.length());
            return factory.create(file, nests, entry);
        } else if (url.regionMatches(true, 0, FILE_PREFIX, 0, FILE_PREFIX.length())) {
            return factory.create(parseFile(url, 0, url.length()), Collections.emptyList(), "");
        } else {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "Unsupported protocol of JAR or entry url. [url=%s]",
                    url));
        }
    }

    private static File parseFile(String url, int beginIndex, int endIndex) {
        URI uri;
        try {
            uri = new URI(url.substring(beginIndex, endIndex));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The root file is not a valid URI. [url=%s]",
                    url));
        }
        return new File(uri);
    }

    private static List<String> parseNests(String url, int beginIndex, int endIndex) {
        if (endIndex - beginIndex < 1) {
            return Collections.emptyList();
        }
        List<String> nests = new LinkedList<>();
        int index;
        int startPos = beginIndex;
        while ((index = url.indexOf(JarLocation.URL_PATH_SEPARATOR, startPos)) > startPos && index < endIndex) {
            nests.add(url.substring(startPos, index));
            startPos = index + JarLocation.URL_PATH_SEPARATOR.length();
        }
        nests.add(url.substring(startPos, endIndex));
        return nests;
    }

    /**
     * 从指定的文本中解析 JAR 的位置信息。
     *
     * @param url 表示包含 JAR 的位置信息的 URL 字符串的 {@link String}。
     * @return 若 {@code url} 为空白字符串，则为 {@code null}，否则为解析到的 JAR 位置信息的 {@link Jar}。
     * @throws IllegalArgumentException {@code url} 为 {@code null} 或未包含有效的 JAR 位置信息。
     */
    public static JarLocation parseJar(String url) {
        return Locations.parse(url, (file, nests, entry) -> {
            if (entry.isEmpty()) {
                return new Jar(file, nests);
            } else if (entry.charAt(entry.length() - 1) == JarEntryLocation.ENTRY_PATH_SEPARATOR) {
                List<String> actualNests = new ArrayList<>(nests.size() + 1);
                actualNests.addAll(nests);
                actualNests.add(entry);
                return new Jar(file, actualNests);
            } else {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The URL to parse does not point to a nestable JAR. [url=%s]",
                        url));
            }
        });
    }

    /**
     * 按照指定的归档件位置创建一个构建器。
     *
     * @param location 表示指定的归档件位置的 {@link JarLocation}。
     * @return 表示创建的归档件位置的构建器的 {@link JarLocation.Builder}。
     */
    public static JarLocation.Builder createBuilderForJarLocation(JarLocation location) {
        return new Jar.Builder(location);
    }

    /**
     * 从指定的文本中解析 JAR 中记录的位置信息。
     *
     * @param url 表示包含 JAR 中记录的位置信息的 URL 字符串的 {@link String}。
     * @return 若 {@code url} 为空白字符串，则为 {@code null}，否则为解析到的 JAR 中记录位置信息的 {@link Jar}。
     * @throws IllegalArgumentException {@code url} 为 {@code null} 或未包含有效的 JAR 位置信息。
     */
    public static JarEntryLocation parseJarEntry(String url) {
        return Locations.parse(url, (file, nests, entry) -> {
            if (entry.isEmpty()) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The URL to parse does not point to an entry in a nestable JAR. [url=%s]",
                        url));
            } else {
                JarLocation jar = JarLocation.custom().file(file).nests(nests).build();
                return new JarEntry(jar, entry);
            }
        });
    }

    /**
     * 按照指定的归档记录位置创建一个构建器。
     *
     * @param location 表示指定的归档记录位置的 {@link JarEntryLocation}。
     * @return 表示创建的归档记录位置的构建器的 {@link JarEntryLocation.Builder}。
     */
    public static JarEntryLocation.Builder createBuilderForJarEntryLocation(JarEntryLocation location) {
        return new JarEntry.Builder(location);
    }

    /**
     * 为 {@link JarLocation} 提供默认实现。
     *
     * @author 梁济时
     * @since 2023-01-16
     */
    private static final class Jar implements JarLocation {
        private final File file;
        private final List<String> nests;

        private URL url;
        private String string;
        private int hash;

        /**
         * 使用 JAR 所在的文件及嵌套路径初始化 {@link JarEntry} 类的新实例。
         *
         * @param file 表示 JAR 所在文件的 {@link File}。
         * @param nests 表示 JAR 的嵌套路径的 {@link List}{@code <}{@link String}{@code >}。
         * @throws IllegalArgumentException {@code file} 为 {@code null}。
         */
        private Jar(File file, List<String> nests) {
            if (file == null) {
                throw new IllegalArgumentException("The file of a JAR location cannot be null.");
            }
            try {
                this.file = file.getCanonicalFile();
            } catch (IOException ex) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The file of JAR location is not canonical. [path=%s]",
                        path(file)));
            }
            this.nests = Optional.ofNullable(nests)
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(nest -> !nest.isEmpty())
                    .collect(Collectors.toList());
        }

        @Override
        public File file() {
            return this.file;
        }

        @Override
        public List<String> nests() {
            return Collections.unmodifiableList(this.nests);
        }

        @Override
        public URL toUrl() throws MalformedURLException {
            if (this.url == null) {
                this.url = new URL(this.toString());
            }
            return this.url;
        }

        @Override
        public JarLocation parent() {
            if (this.nests.isEmpty()) {
                return null;
            }
            List<String> subNests = this.nests.subList(this.nests.size() - 1, this.nests.size());
            return new Jar(this.file, subNests);
        }

        @Override
        public JarEntryLocation entry(String name) {
            return JarEntryLocation.custom().jar(this).entry(name).build();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj != null && obj.getClass() == this.getClass()) {
                Jar another = (Jar) obj;
                return Objects.equals(this.file, another.file) && this.nests.size() == another.nests.size()
                        && IntStream.range(0, this.nests.size())
                        .allMatch(index -> Objects.equals(this.nests.get(index), another.nests.get(index)));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            if (this.hash == 0) {
                this.hash = Objects.hash(this.getClass(), this.file, this.nests);
            }
            return this.hash;
        }

        @Override
        public String toString() {
            if (this.string == null) {
                this.string = Locations.toString(this.file, this.nests, null);
            }
            return this.string;
        }

        /**
         * 为 {@link JarLocation.Builder} 提供默认实现。
         *
         * @author 梁济时
         * @since 2022-01-16
         */
        private static final class Builder implements JarLocation.Builder {
            private File file;
            private final List<String> nests;

            /**
             * 使用作为初始值的位置信息初始化 {@link JarEntry.Builder} 类的新实例。
             *
             * @param location 表示作为初始值的位置信息的 {@link JarEntryLocation}。
             */
            public Builder(JarLocation location) {
                if (location == null) {
                    this.file = null;
                    this.nests = new LinkedList<>();
                } else {
                    this.file = location.file();
                    this.nests = new LinkedList<>(location.nests());
                }
            }

            @Override
            public JarLocation.Builder file(File file) {
                this.file = file;
                return this;
            }

            @Override
            public JarLocation.Builder nest(String nest) {
                this.nests.add(nest);
                return this;
            }

            @Override
            public JarLocation.Builder nests(Iterable<String> nests) {
                if (nests != null) {
                    for (String nest : nests) {
                        this.nests.add(nest);
                    }
                }
                return this;
            }

            @Override
            public JarLocation build() {
                return new Jar(this.file, this.nests);
            }
        }
    }

    /**
     * 为 {@link JarEntryLocation} 提供默认实现。
     *
     * @author 梁济时
     * @since 2022-10-07
     */
    private static final class JarEntry implements JarEntryLocation {
        private final JarLocation jar;
        private final String entry;

        private URL url;
        private String string;
        private int hash;

        /**
         * 使用 JAR 所在的文件及嵌套路径初始化 {@link JarEntry} 类的新实例。
         *
         * @param jar 表示记录所在归档件的位置的 {@link JarLocation}。
         * @param entry 表示 JAR 中条目名称的 {@link String}。
         * @throws IllegalArgumentException {@code jar} 为 {@code null} 或 {@code entry} 为空字符串。
         */
        private JarEntry(JarLocation jar, String entry) {
            if (jar == null) {
                throw new IllegalArgumentException("The owning JAR of an entry cannot be null.");
            } else if (entry == null || entry.isEmpty()) {
                throw new IllegalArgumentException("The name of entry in JAR cannot be null or an empty string.");
            } else {
                this.jar = jar;
                this.entry = entry;
            }
        }

        @Override
        public JarLocation jar() {
            return this.jar;
        }

        @Override
        public String entry() {
            return this.entry;
        }

        @Override
        public URL toUrl() throws MalformedURLException {
            if (this.url == null) {
                this.url = new URL(this.toString());
            }
            return this.url;
        }

        @Override
        public JarLocation asJar() {
            return this.jar.copy().nest(this.entry).build();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj != null && obj.getClass() == this.getClass()) {
                JarEntry another = (JarEntry) obj;
                return Objects.equals(this.jar, another.jar) && Objects.equals(this.entry, another.entry);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            if (this.hash == 0) {
                this.hash = Arrays.hashCode(new Object[] {this.getClass(), this.jar, this.entry});
            }
            return this.hash;
        }

        @Override
        public String toString() {
            if (this.string == null) {
                this.string = Locations.toString(this.jar.file(), this.jar.nests(), this.entry);
            }
            return this.string;
        }

        /**
         * 为 {@link JarEntryLocation.Builder} 提供默认实现。
         *
         * @author 梁济时
         * @since 2022-10-07
         */
        private static final class Builder implements JarEntryLocation.Builder {
            private JarLocation jar;
            private String entry;

            /**
             * 使用作为初始值的位置信息初始化 {@link Builder} 类的新实例。
             *
             * @param location 表示作为初始值的位置信息的 {@link JarEntryLocation}。
             */
            public Builder(JarEntryLocation location) {
                if (location == null) {
                    this.jar = null;
                    this.entry = null;
                } else {
                    this.jar = location.jar();
                    this.entry = location.entry();
                }
            }

            @Override
            public JarEntryLocation.Builder jar(JarLocation jar) {
                this.jar = jar;
                return this;
            }

            @Override
            public JarEntryLocation.Builder entry(String entry) {
                this.entry = entry;
                return this;
            }

            @Override
            public JarEntryLocation build() {
                return new JarEntry(this.jar, this.entry);
            }
        }
    }

    /**
     * 获取指定文件的标准化路径。
     *
     * @param file 表示待获取路径的文件的 {@link File}。
     * @return 表示文件的路径的 {@link String}。
     * @throws IllegalStateException 当标准化失败时。
     */
    public static String path(File file) {
        if (file == null) {
            return null;
        }

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to canonicalize file. [file=%s]", file.getPath()),
                    e);
        }
    }
}
