/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import modelengine.fitframework.protocol.jar.support.Locations;

import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;

/**
 * 为 {@link JarFile} 提供基于 {@link Jar} 的适配程序。
 *
 * @author 梁济时
 * @since 2022-09-22
 */
public final class NestableJarFile extends JarFile {
    private final Jar jar;
    private final Map<String, Entry> entries;

    /**
     * 使用待适配的 JAR 初始化 {@link NestableJarFile} 类的新实例。
     *
     * @param jar 表示待适配的 JAR 的 {@link Jar}。
     * @throws IllegalArgumentException {@code jar} 为 {@code null}。
     * @throws IOException 解析 JAR 过程发生输入输出异常。
     */
    public NestableJarFile(Jar jar) throws IOException {
        super(validateJar(jar).location().file());

        this.jar = jar;
        this.entries = new LinkedHashMap<>(this.jar.entries().size());
        for (Jar.Entry entry : jar.entries()) {
            Entry adapter = new Entry(entry);
            this.entries.put(adapter.getName(), adapter);
        }
    }

    private static Jar validateJar(Jar jar) {
        if (jar == null) {
            throw new IllegalArgumentException("The adapting JAR cannot be null.");
        } else {
            return jar;
        }
    }

    @Override
    public Entry getJarEntry(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        Entry entry = this.entries.get(name);
        if (entry == null && name.charAt(name.length() - 1) != JarEntryLocation.ENTRY_PATH_SEPARATOR) {
            entry = this.entries.get(name + JarEntryLocation.ENTRY_PATH_SEPARATOR);
        }
        return entry;
    }

    @Override
    public Entry getEntry(String name) {
        return this.getJarEntry(name);
    }

    @Override
    public Enumeration<JarEntry> entries() {
        return new EntryEnumeration(this.entries.values().iterator());
    }

    @Override
    public Stream<JarEntry> stream() {
        int characteristics = Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL;
        Iterator<? extends JarEntry> iterator = this.entries.values().iterator();
        Spliterator<JarEntry> spliterator = Spliterators.spliterator(iterator, this.size(), characteristics);
        return StreamSupport.stream(spliterator, false);
    }

    @Override
    public synchronized InputStream getInputStream(ZipEntry ze) throws IOException {
        Entry entry;
        if (ze instanceof Entry) {
            entry = (Entry) ze;
        } else {
            entry = this.entries.get(ze.getName());
        }
        return entry.entry.read();
    }

    @Override
    public String getComment() {
        return this.jar.comment();
    }

    @Override
    public String getName() {
        // 该属性会在 ZipFile 类型中用作对文件权限的判断，因此此处必然返回所在文件的路径。
        return Locations.path(this.jar.location().file());
    }

    @Override
    public int size() {
        return this.jar.entries().size();
    }

    /**
     * 获取 {@link Jar} 的权限。
     *
     * @return 表示权限的 {@link Permission}。
     */
    public Permission getPermission() {
        return this.jar.permission();
    }

    /**
     * 为 {@link JarEntry} 提供基于 {@link Jar.Entry} 的适配程序。
     *
     * @author 梁济时
     * @since 2022-09-22
     */
    public static final class Entry extends JarEntry {
        private final Jar.Entry entry;

        /**
         * 使用待适配的 JAR 条目初始化 {@link Entry} 类的新实例。
         *
         * @param entry 表示待适配的 JAR 条目的 {@link Jar.Entry}。
         * @throws IllegalArgumentException {@code entry} 为 {@code null}。
         */
        private Entry(Jar.Entry entry) {
            super(validateEntry(entry).name());
            this.entry = entry;
        }

        private static Jar.Entry validateEntry(Jar.Entry entry) {
            if (entry == null) {
                throw new IllegalArgumentException("The adapting JAR entry cannot be null.");
            } else {
                return entry;
            }
        }

        @Override
        public String getName() {
            return this.entry.name();
        }

        @Override
        public long getTime() {
            return this.entry.timeOfLastModification().getTime();
        }

        @Override
        public long getSize() {
            return this.entry.sizeOfUncompressed();
        }

        @Override
        public long getCompressedSize() {
            return this.entry.sizeOfCompressed();
        }

        @Override
        public long getCrc() {
            return Integer.toUnsignedLong(this.entry.crc32());
        }

        @Override
        public int getMethod() {
            return this.entry.methodOfCompression().id();
        }

        @Override
        public String getComment() {
            return this.entry.comment();
        }

        @Override
        public boolean isDirectory() {
            return this.entry.directory();
        }

        @Override
        public byte[] getExtra() {
            return this.entry.extra();
        }

        /**
         * 获取 {@link JarEntry} 的输入流。
         *
         * @return 表示输入流的 {@link JarEntry}。
         * @throws IOException 当读取过程中发生输入输出异常时。
         */
        public InputStream getInputStream() throws IOException {
            return this.entry.read();
        }
    }

    private static final class EntryEnumeration implements Enumeration<JarEntry> {
        private final Iterator<Entry> iterator;

        private EntryEnumeration(Iterator<Entry> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasMoreElements() {
            return this.iterator.hasNext();
        }

        @Override
        public JarEntry nextElement() {
            return this.iterator.next();
        }
    }
}
