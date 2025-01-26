/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为配置提供路径的定义。
 *
 * @author 梁济时
 * @since 2022-12-29
 */
final class ConfigPath {
    /**
     * 表示空的配置路径。
     */
    static final ConfigPath EMPTY = new ConfigPath(Collections.emptyList());

    /**
     * 表示配置路径的分隔符。
     */
    static final char SEPARATOR = '.';

    private final List<String> keys;
    private ConfigPath parent;
    private String name;

    /**
     * 使用路径中的键的序列初始化 {@link ConfigPath} 类的新实例。
     *
     * @param keys 表示路径中键的序列的 {@link List}{@code <}{@link String}{@code >}。
     */
    private ConfigPath(List<String> keys) {
        this.keys = Collections.unmodifiableList(keys);
    }

    /**
     * 获取路径中键的序列。
     *
     * @return 表示键的序列的 {@link List}{@code <}{@link String}{@code >}。
     */
    public List<String> keys() {
        return this.keys;
    }

    /**
     * 获取配置路径的长度。
     *
     * @return 表示配置路径长度的 32 位整数。
     */
    int length() {
        return this.keys.size();
    }

    /**
     * 获取指定索引处的路径项。
     *
     * @param index 表示路径项所在索引的 32 位整数。
     * @return 表示该索引处的路径项的 {@link String}。
     */
    String get(int index) {
        return this.keys.get(index);
    }

    /**
     * 获取一个值，该值指示路径是否是空的。
     *
     * @return 若路径是空的，则为 {@code true}，否则为 {@code false}。
     */
    boolean empty() {
        return this.keys.isEmpty();
    }

    /**
     * 获取父路径。
     *
     * @return 表示父路径的 {@link ConfigPath}。
     */
    ConfigPath parent() {
        if (this.empty()) {
            return EMPTY;
        }
        if (this.parent == null) {
            if (this.length() > 1) {
                this.parent = of(this.keys.subList(0, this.length() - 1));
            } else {
                this.parent = EMPTY;
            }
        }
        return this.parent;
    }

    /**
     * 获取指定键的子路径。
     *
     * @param key 表示子路径的键的 {@link String}。
     * @return 表示子路径的 {@link ConfigPath}。
     */
    ConfigPath child(String key) {
        List<String> newKeys = new ArrayList<>(this.keys.size() + 1);
        newKeys.addAll(this.keys);
        newKeys.add(key);
        return of(newKeys);
    }

    /**
     * 获取配置的名称。
     *
     * @return 表示配置的名称的 {@link String}。
     */
    String name() {
        if (this.name == null) {
            int index = this.length() - 1;
            this.name = index < 0 ? null : this.get(index);
        }
        return this.name;
    }

    /**
     * 在指定的映射中获取当前路径所对应的嵌套映射。
     *
     * @param map 表示待在其中获取嵌套映射的根映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param isCreateNew 若为 {@code true}，则在所需映射不存在时，创建映射实例，否则不创建映射实例，直接返回 {@code null}。
     * @return 若存在对应的嵌套映射，则为表示该映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}，否则为
     * {@code null}。
     */
    Map<String, Object> get(Map<String, Object> map, boolean isCreateNew) {
        Map<String, Object> current = map;
        for (String key : this.keys) {
            Object value = current.get(key);
            if (value instanceof Map) {
                current = cast(value);
                continue;
            }
            if (isCreateNew) {
                Map<String, Object> next = new HashMap<>();
                current.put(key, next);
                current = next;
            } else {
                current = null;
                break;
            }
        }
        return current;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ConfigPath) {
            return CollectionUtils.equals(this.keys, ((ConfigPath) obj).keys);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        Object[] array = new Object[this.length() + 1];
        int index = 0;
        array[index++] = this.getClass();
        for (int i = 0; i < this.length(); i++) {
            array[index++] = this.get(i);
        }
        return Arrays.hashCode(array);
    }

    @Override
    public String toString() {
        return StringUtils.join(SEPARATOR, this.keys);
    }

    /**
     * 从指定的字符串中解析配置路径信息。
     *
     * @param value 表示包含配置路径信息的字符串的 {@link String}。
     * @return 表示解析到的配置路径的 {@link ConfigPath}。
     */
    static ConfigPath parse(String value) {
        if (value == null) {
            return EMPTY;
        }
        String[] splitKeys = StringUtils.split(value, SEPARATOR);
        return of(Arrays.asList(splitKeys));
    }

    /**
     * 为指定键的序列生成配置路径。
     *
     * @param keys 表示键的序列的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示以该键序作为路径的 {@link ConfigPath}。
     */
    static ConfigPath of(List<String> keys) {
        List<String> actual = Optional.ofNullable(keys)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (actual.isEmpty()) {
            return EMPTY;
        } else {
            return new ConfigPath(actual);
        }
    }
}
