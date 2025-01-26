/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 为 {@link Jar.EntryCollection} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-01-12
 */
final class JarEntryCollection implements Jar.EntryCollection {
    private final List<String> keys;
    private final Map<String, Jar.Entry> entries;

    /**
     * 使用包含记录的数量初始化 {@link JarEntryCollection} 类的新实例。
     *
     * @param capacity 表示容量的 32 位整数。
     */
    JarEntryCollection(int capacity) {
        this.keys = new ArrayList<>(capacity);
        this.entries = new HashMap<>(capacity);
    }

    /**
     * 添加一条记录。
     *
     * @param entry 表示待添加的记录的 {@link Jar.Entry}。
     */
    void add(Jar.Entry entry) {
        String key = keyOfEntry(entry.name());
        this.keys.add(key);
        this.entries.put(key, entry);
    }

    private static String keyOfEntry(String name) {
        if (name.length() > 0 && name.charAt(name.length() - 1) == JarEntryLocation.ENTRY_PATH_SEPARATOR) {
            return name.substring(0, name.length() - 1);
        } else {
            return name;
        }
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    @Override
    public Jar.Entry get(int index) {
        return this.entries.get(this.keys.get(index));
    }

    @Override
    public Jar.Entry get(String name) {
        return this.entries.get(keyOfEntry(name));
    }

    @Override
    public Stream<Jar.Entry> stream() {
        return this.keys.stream().map(this.entries::get);
    }

    @Override
    public java.util.Iterator<Jar.Entry> iterator() {
        return this.new Iterator();
    }

    @Override
    public String toString() {
        return "size=" + this.size();
    }

    private final class Iterator implements java.util.Iterator<Jar.Entry> {
        private final java.util.Iterator<String> keys;

        private Iterator() {
            this.keys = JarEntryCollection.this.keys.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.keys.hasNext();
        }

        @Override
        public Jar.Entry next() {
            String key = this.keys.next();
            return JarEntryCollection.this.entries.get(key);
        }
    }
}
