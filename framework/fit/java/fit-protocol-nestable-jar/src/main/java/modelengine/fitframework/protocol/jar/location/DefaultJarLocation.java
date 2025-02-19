/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.location;

import modelengine.fitframework.protocol.jar.FilesCache;
import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarLocation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 为 {@link JarLocation} 提供默认实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2023-01-16
 */
final class DefaultJarLocation implements JarLocation {
    private final File file;
    private final List<String> nests;

    private URL url;
    private String string;
    private int hash;

    /**
     * 使用 JAR 所在的文件及嵌套路径初始化 {@link DefaultJarEntryLocation} 类的新实例。
     *
     * @param file 表示 JAR 所在文件的 {@link File}。
     * @param nests 表示 JAR 的嵌套路径的 {@link List}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException {@code file} 为 {@code null}。
     */
    DefaultJarLocation(File file, List<String> nests) {
        if (file == null) {
            throw new IllegalArgumentException("The file of a JAR location cannot be null.");
        }
        this.file = FilesCache.instance().getCanonicalFile(file);
        this.nests = Optional.ofNullable(nests)
                .stream()
                .flatMap(Collection::stream)
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
        return new DefaultJarLocation(this.file, subNests);
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
            DefaultJarLocation another = (DefaultJarLocation) obj;
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
    static final class Builder implements JarLocation.Builder {
        private File file;
        private final List<String> nests;

        /**
         * 使用作为初始值的位置信息初始化 {@link DefaultJarEntryLocation.Builder} 类的新实例。
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
            return new DefaultJarLocation(this.file, this.nests);
        }
    }
}
