/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import modelengine.fit.http.header.support.DefaultParameterCollection;

import java.util.List;
import java.util.Optional;

/**
 * 表示 Http 消息头的参数集合。
 *
 * @author 季聿阶
 * @since 2022-09-01
 */
public interface ParameterCollection {
    /**
     * 获取所有的参数键的列表。
     *
     * @return 表示所有的参数键列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> keys();

    /**
     * 获取指定参数的值。
     *
     * @param key 表示指定参数键的 {@link String}。
     * <p>参数键<b>大小写敏感</b>。</p>
     * @return 表示指定参数键的值的 {@link Optional}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 或空白字符串时。
     */
    Optional<String> get(String key);

    /**
     * 设置指定参数的值。
     *
     * @param key 表示指定参数的键的 {@link String}。
     * @param value 表示指定参数的值的 {@link String}。
     * @return 表示当前 Http 消息头的参数集合的 {@link ParameterCollection}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 或空白字符串时。
     */
    ParameterCollection set(String key, String value);

    /**
     * 获取所有的参数的数量。
     *
     * @return 表示所有的参数数量的 {@code int}。
     */
    int size();

    /**
     * 获取所有参数的文本内容。
     *
     * @return 表示所有参数的文本内容的 {@link String}。
     */
    @Override
    String toString();

    /**
     * 创建一个空的参数集合。
     *
     * @return 表示创建后的空的参数集合的 {@link ParameterCollection}。
     */
    static ParameterCollection create() {
        return new DefaultParameterCollection();
    }
}
