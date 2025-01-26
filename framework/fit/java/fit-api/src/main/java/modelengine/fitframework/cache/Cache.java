/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.cache;

/**
 * 表示缓存实例。
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
public interface Cache {
    /**
     * 获取当前缓存实例的名字。
     *
     * @return 表示当前缓存实例名字的 {@link String}。
     */
    String name();

    /**
     * 判断当前缓存实例中是否包含指定键的值。
     *
     * @param key 表示指定键的 {@link Object}。
     * @return 如果包含，返回 {@code true}，否则，返回 {@code false}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 时。
     */
    boolean contains(Object key);

    /**
     * 获取指定键的值。
     *
     * @param key 表示指定键的 {@link Object}。
     * @return 表示指定键的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 时。
     */
    Object get(Object key);

    /**
     * 设置指定键的值。
     *
     * @param key 表示指定键的 {@link Object}。
     * @param value 表示待设置的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 时。
     */
    void put(Object key, Object value);

    /**
     * 移除指定键的值。
     *
     * @param key 表示指定键的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 时。
     */
    void remove(Object key);
}
