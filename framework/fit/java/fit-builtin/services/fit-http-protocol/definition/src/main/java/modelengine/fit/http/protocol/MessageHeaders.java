/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import java.util.List;
import java.util.Optional;

/**
 * 表示只读的 Http 消息头集合。
 *
 * @author 季聿阶
 * @since 2022-07-06
 */
public interface MessageHeaders {
    /**
     * 获取所有的消息头的名字的列表。
     *
     * @return 表示所有的消息头名字列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> names();

    /**
     * 判断指定消息头是否存在。
     *
     * @param name 表示指定消息头名字的 {@link String}。
     * <p>消息头名字<b>大小写不敏感</b>。</p>
     * @return 当指定消息头存在时，返回 {@code true}，否则，返回 {@code false}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    boolean contains(String name);

    /**
     * 获取指定消息头的第一个值。
     *
     * @param name 表示指定消息头的名字的 {@link String}。
     * <p>消息头名字<b>大小写不敏感</b>。</p>
     * @return 表示指定消息头名字的第一个值的 {@link Optional}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    Optional<String> first(String name);

    /**
     * 获取指定消息头的第一个值。
     *
     * @param name 表示指定消息头的名字的 {@link String}。
     * <p>消息头名字<b>大小写不敏感</b>。</p>
     * @return 表示指定消息头名字的第一个值的 {@link String}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     * @throws IllegalStateException 当指定消息头名字的第一个值不存在时。
     */
    String require(String name);

    /**
     * 获取指定消息头的所有值的列表。
     *
     * @param name 表示指定消息头的名字的 {@link String}。
     * <p>消息头名字<b>大小写不敏感</b>。</p>
     * @return 表示指定消息头名字的所有值的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    List<String> all(String name);

    /**
     * 获取所有的消息头的数量。
     *
     * @return 表示所有的消息头数量的 {@code int}。
     */
    int size();
}
