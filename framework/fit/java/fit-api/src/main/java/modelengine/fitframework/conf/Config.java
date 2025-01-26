/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.conf.support.HierarchicalConfig;
import modelengine.fitframework.conf.support.MapConfig;
import modelengine.fitframework.conf.support.PropertiesConfig;
import modelengine.fitframework.conf.support.ReadonlyMapConfig;
import modelengine.fitframework.conf.support.ReadonlyPropertiesConfig;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 为应用程序和插件提供配置。
 *
 * @author 梁济时
 * @since 2022-05-18
 */
public interface Config {
    /** 表示 '.' 分隔符。 */
    String SEPARATOR_DOT = ".";

    /** 表示 '-' 分隔符。 */
    String SEPARATOR_HYPHEN = "-";

    /**
     * 获取配置的名称。
     * <p>配置的名称没有功能上的含义，而是用以在调试、日志等场景中区分不同的配置实例。</p>
     *
     * @return 表示配置的名称的 {@link String}。
     */
    String name();

    /**
     * 标准化键的名字。
     * <p>将键中的 {@code '-'} 全部去除，同时将后续跟随的英文字母变为大写。</p>
     *
     * @param key 表示待标准化的键的 {@link String}。
     * @return 表示标准化后的键的 {@link String}。
     */
    static String canonicalizeKey(String key) {
        if (StringUtils.isBlank(key)) {
            return key;
        }
        String[] parts = StringUtils.split(key, SEPARATOR_HYPHEN);
        StringBuilder builder = new StringBuilder(key.length());
        builder.append(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                builder.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
            }
        }
        return builder.toString();
    }

    /**
     * 可视化键的名字。
     * <p>将键中的大写英文字母全部改为小写，同时在其前面添加 {@code '-'}。</p>
     *
     * @param key 表示待可视化的键的 {@link String}。
     * @return 表示可视化后的键的 {@link String}。
     */
    static String visualizeKey(String key) {
        if (StringUtils.isBlank(key)) {
            return key;
        }
        StringBuilder builder = new StringBuilder(key.length());
        for (int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append(SEPARATOR_HYPHEN).append(Character.toLowerCase(ch));
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    /**
     * 获取配置中包含的键的集合。
     *
     * @return 表示键的集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> keys();

    /**
     * 获取指定键的配置的值，并转换成指定类型。
     *
     * @param key 表示配置的键的 {@link String}。
     * @param type 表示值的指定类型的 {@link Type}。
     * @return 若存在配置，则为配置的值的 {@link Object}，否则为 {@code null}。
     */
    Object get(String key, Type type);

    /**
     * 获取指定键的配置的值，并转换成指定类型。
     *
     * @param key 表示配置的键的 {@link String}。
     * @param clazz 表示值的指定类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示配置的值的类型的 {@link T}。
     * @return 若存在配置，则为配置的值的 {@link T}，否则为 {@code null}。
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 对所有配置进行解密。
     *
     * @param decryptor 表示解密器的 {@link ConfigDecryptor}。
     */
    void decrypt(@Nonnull ConfigDecryptor decryptor);

    /**
     * 获取指定键的配置的值的列表，并将每一个值转换成指定类型。
     *
     * @param key 表示配置的键的 {@link String}。
     * @param elementClass 表示每一个值的指定类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示配置的键的每一个值的类型的 {@link T}。
     * @return 若存在配置，则为配置的值的列表的 {@link List}{@code <}{@link T}{@code >}，否则为 {@code null}。
     */
    default <T> List<T> list(String key, Class<T> elementClass) {
        ParameterizedType type = TypeUtils.parameterized(List.class, new Type[] {elementClass});
        Object value = this.get(key, type);
        return cast(value);
    }

    /**
     * 使用配置的名称和配置包含的值的映射创建一份可修改的配置。
     *
     * @param name 表示配置的名称的 {@link String}。
     * @param map 表示配置包含的值的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示可修改的配置的 {@link ModifiableConfig}。
     */
    static ModifiableConfig fromMap(String name, Map<String, Object> map) {
        return new MapConfig(name, map);
    }

    /**
     * 使用配置的名称和包含配置内容的属性集创建一份可修改的配置。
     *
     * @param name 表示配置的名称的 {@link String}。
     * @param properties 表示包含配置内容的属性集的 {@link Properties}。
     * @return 表示可修改的配置的 {@link ModifiableConfig}。
     */
    static ModifiableConfig fromProperties(String name, Properties properties) {
        return new PropertiesConfig(name, properties);
    }

    /**
     * 使用配置的名称及包含配置值的映射创建一份配置。
     *
     * @param name 表示配置的名称的 {@link String}。
     * @param map 表示包含配置的值的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示配置的 {@link Config}。
     */
    static Config fromReadonlyMap(String name, Map<String, Object> map) {
        return new ReadonlyMapConfig(name, map);
    }

    /**
     * 使用配置的名称及包含配置值的属性集创建一份配置。
     *
     * @param name 表示配置的名称的 {@link String}。
     * @param properties 表示包含配置值的属性集的 {@link Properties}。
     * @return 表示配置的 {@link Config}。
     */
    static Config fromReadonlyProperties(String name, Properties properties) {
        return new ReadonlyPropertiesConfig(name, properties);
    }

    /**
     * 使用配置的名称及初始值创建一份可修改的配置。
     *
     * @param name 表示配置的名称的 {@link String}。
     * @param values 表示配置的初始值的 {@link Map}{@code <?, ?>}。
     * @return 表示可修改的配置的 {@link ModifiableConfig}。
     */
    static ModifiableConfig fromHierarchical(String name, Map<?, ?> values) {
        return new HierarchicalConfig(name, values);
    }
}
