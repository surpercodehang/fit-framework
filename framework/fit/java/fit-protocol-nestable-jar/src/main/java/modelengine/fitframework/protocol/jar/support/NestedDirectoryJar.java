/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarLocation;

import java.security.Permission;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 为 {@link Jar} 提供基于内嵌目录条目的实现。
 *
 * @author 梁济时
 * @since 2022-09-19
 */
final class NestedDirectoryJar implements Jar {
    private final Jar.Entry entry;
    private final JarLocation location;

    private final JarEntryCollection entries;

    /**
     * 使用内嵌的 JAR 目录初始化 {@link NestedDirectoryJar} 类的新实例。
     *
     * @param entry 表示用作 JAR 的文件条目的 {@link Jar.Entry}。
     * @throws IllegalArgumentException {@code entry} 为 {@code null} 或不是一个目录。
     */
    NestedDirectoryJar(Jar.Entry entry) {
        if ((this.entry = entry) == null) {
            throw new IllegalArgumentException("The entry of nested directory JAR cannot be null.");
        } else if (!this.entry.directory()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "The entry of nested JAR is not a directory. [entry=%s]", entry));
        } else {
            this.location = this.entry.location().asJar();
            List<Entry> buildEntries = entry.jar()
                    .entries()
                    .stream()
                    .filter(current -> current != entry)
                    .filter(current -> current.name().startsWith(entry.name()))
                    .map(current -> this.new Entry(current))
                    .collect(Collectors.toList());
            this.entries = new JarEntryCollection(buildEntries.size());
            buildEntries.forEach(this.entries::add);
        }
    }

    @Override
    public String comment() {
        return this.entry.comment();
    }

    @Override
    public JarLocation location() {
        return this.location;
    }

    @Override
    public Permission permission() {
        return this.entry.jar().permission();
    }

    @Override
    public EntryCollection entries() {
        return this.entries;
    }

    @Override
    public String toString() {
        return this.location.toString();
    }

    /**
     * 为 {@link Jar.Entry} 提供基于内嵌目录 JAR 中条目的实现。
     *
     * @author 梁济时
     * @since 2022-09-19
     */
    private final class Entry extends EmptyJarEntryDecorator {
        private final String name;
        private final JarEntryLocation location;

        /**
         * 使用原始的 JAR 条目初始化 {@link Entry} 类的新实例。
         *
         * @param origin 表示原始的 JAR 条目的 {@link Jar.Entry}。
         */
        private Entry(Jar.Entry origin) {
            super(origin);

            this.name = origin.name().substring(NestedDirectoryJar.this.entry.name().length());
            this.location = JarEntryLocation.custom()
                    .jar(NestedDirectoryJar.this.location())
                    .entry(this.name)
                    .build();
        }

        @Override
        public Jar jar() {
            return NestedDirectoryJar.this;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public JarEntryLocation location() {
            return this.location;
        }
    }
}
