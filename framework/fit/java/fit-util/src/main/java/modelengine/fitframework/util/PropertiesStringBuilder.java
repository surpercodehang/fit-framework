/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.support.AbstractSetDecorator;
import modelengine.fitframework.util.support.MappedIterator;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 构建一个可被 {@link Properties} 解析的字符串。
 * <p>{@link Properties} 自身包含 {@link Properties#store(Writer, String)} 和
 * {@link Properties#store(OutputStream, String)} 方法来保存数据。</p>
 * <p>但通过其自身的存储数据的方法来转换数据时，存在以下几个问题：</p>
 * <ol>
 *     <li>写入过程会自动加入时间戳来表示数据被存储的时间，然而这通常是不需要的，而且会导致在不同时间生成的数据的内容不一致的问题。</li>
 *     <li>换行符严格使用系统的换行符，这个方法是由 {@link BufferedWriter#newLine()} 方法决定的，外部无法干预。</li>
 *     <li>每组键值对在最终存储时的顺序是不可预知的，这是因为其内部数据存储在一个 {@link ConcurrentHashMap}
 *     中，其顺序与键的哈希值相关。</li>
 * </ol>
 * <p>但使用 {@link Properties} 来存储数据也有非常大的好处：</p>
 * <ol>
 *     <li>所存储的数据在使用时可直接通过 {@link Properties} 类来加载，方便使用。</li>
 *     <li>其中实现了较为丰富的转义策略，来保证内容的有效性，不必自己实现。</li>
 * </ol>
 * <p>为了使用 {@link Properties} 所带来的好处并解决之前提到的问题，通过实现该工具类来解决这些问题。</p>
 * <ol>
 *     <li>所构建字符串的内容可被 {@link Properties} 解析。</li>
 *     <li>内容的顺序按照遵从添加次序，可预期。</li>
 *     <li>可定义所使用的换行符，默认为 UNIX 风格（LF）。</li>
 *     <li>去掉生成时间的注释，避免不同时刻生成的内容存在差异。</li>
 * </ol>
 *
 * @author 梁济时
 * @since 2021-11-05
 */
public class PropertiesStringBuilder {
    private final Properties properties;
    private LineSeparator lineSeparator;

    /**
     * 初始化 {@link PropertiesStringBuilder} 类的新实例。
     */
    public PropertiesStringBuilder() {
        this.properties = new OrderedProperties();
        this.lineSeparator = LineSeparator.LF;
    }

    /**
     * 设置一个属性。
     *
     * @param key 表示属性的键的 {@link String}。
     * @param value 表示属性的值的 {@link String}。
     * @return 表示当前构建器的 {@link PropertiesStringBuilder}。
     */
    public PropertiesStringBuilder setProperty(String key, String value) {
        this.properties.setProperty(key, value);
        return this;
    }

    /**
     * 设置使用的换行符。
     * <p>默认为 {@link LineSeparator#LF UNIX风格}。</p>
     *
     * @param lineSeparator 表示换行符的类型的 {@link LineSeparator}。
     * @return 表示当前构建器的 {@link PropertiesStringBuilder}。
     */
    public PropertiesStringBuilder setLineSeparator(LineSeparator lineSeparator) {
        this.lineSeparator = ObjectUtils.nullIf(lineSeparator, LineSeparator.LF);
        return this;
    }

    /**
     * 构建字符串。
     *
     * @return 包含属性信息的字符串的 {@link String}。
     */
    public String build() {
        byte[] bytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            this.properties.store(writer, null);
            bytes = out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store properties to memory stream.", e);
        }
        String content = new String(bytes, StandardCharsets.UTF_8);
        String[] lines = content.split(System.lineSeparator());
        return Arrays.stream(lines)
                .filter(PropertiesStringBuilder::isNotRedundant)
                .collect(Collectors.joining(this.lineSeparator.value()));
    }

    private static boolean isNotRedundant(String line) {
        String trimmed = StringUtils.trimStart(line);
        return !trimmed.startsWith("#");
    }

    private static class OrderedProperties extends Properties {
        private final List<Object> keys;

        private OrderedProperties() {
            this.keys = new LinkedList<>();
        }

        @Override
        public Object put(Object key, Object value) {
            Object previousValue = super.put(key, value);
            if (previousValue != null) {
                this.keys.remove(key);
            }
            this.keys.add(key);
            return previousValue;
        }

        // 兼容 JDK8 之后版本
        @Override
        @Nonnull
        public Set<Map.Entry<Object, Object>> entrySet() {
            return new AbstractSetDecorator<Map.Entry<Object, Object>>(super.entrySet()) {
                @Override
                @Nonnull
                public Iterator<Map.Entry<Object, Object>> iterator() {
                    return new MappedIterator<>(OrderedProperties.this.keys.iterator(),
                            key -> new AbstractMap.SimpleEntry<>(key, OrderedProperties.this.get(key)));
                }
            };
        }

        // 兼容 JDK8 及之前版本
        @Override
        public synchronized Enumeration<Object> keys() {
            return new Enumeration<Object>() {
                private int index = 0;

                @Override
                public boolean hasMoreElements() {
                    return this.index < OrderedProperties.this.keys.size();
                }

                @Override
                public Object nextElement() {
                    return OrderedProperties.this.keys.get(this.index++);
                }
            };
        }
    }
}
