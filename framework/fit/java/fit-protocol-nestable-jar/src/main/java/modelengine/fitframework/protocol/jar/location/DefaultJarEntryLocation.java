/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.location;

import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarLocation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link JarEntryLocation} 提供默认实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-10-07
 */
final class DefaultJarEntryLocation implements JarEntryLocation {
    private final JarLocation jar;
    private final String entry;

    private URL url;
    private String string;
    private int hash;

    /**
     * 使用 JAR 所在的文件及嵌套路径初始化 {@link DefaultJarEntryLocation} 类的新实例。
     *
     * @param jar 表示记录所在归档件的位置的 {@link JarLocation}。
     * @param entry 表示 JAR 中条目名称的 {@link String}。
     * @throws IllegalArgumentException {@code jar} 为 {@code null} 或 {@code entry} 为空字符串。
     */
    DefaultJarEntryLocation(JarLocation jar, String entry) {
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
            DefaultJarEntryLocation another = (DefaultJarEntryLocation) obj;
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
    static final class Builder implements JarEntryLocation.Builder {
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
            return new DefaultJarEntryLocation(this.jar, this.entry);
        }
    }
}
