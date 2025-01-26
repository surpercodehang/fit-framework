/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http;

import java.util.List;
import java.util.Optional;

/**
 * 表示 Http 请求属性的集合。
 *
 * @author 季聿阶
 * @since 2022-09-01
 */
public interface AttributeCollection {
    /**
     * 获取所有的属性名字的列表。
     *
     * @return 表示所有的属性名字列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> names();

    /**
     * 获取指定属性的值。
     *
     * @param name 表示指定属性名字的 {@link String}。
     * <p>属性名字<b>大小写敏感</b>。</p>
     * @return 表示指定属性名字的值的 {@link Optional}{@code <}{@link Object}{@code >}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    Optional<Object> get(String name);

    /**
     * 设置指定属性的值。
     *
     * @param name 表示指定属性的名字的 {@link String}。
     * @param value 表示指定属性的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    void set(String name, Object value);

    /**
     * 移除指定的属性。
     *
     * @param name 表示指定属性的名字的 {@link String}。
     */
    void remove(String name);

    /**
     * 获取所有的属性的数量。
     *
     * @return 表示所有的属性数量的 {@code int}。
     */
    int size();
}
