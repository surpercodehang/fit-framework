/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.support.DefaultMultiValueEntity;
import modelengine.fitframework.model.MultiValueMap;

import java.util.List;
import java.util.Optional;

/**
 * 表示多值格式的消息体数据。
 *
 * @author 季聿阶
 * @since 2022-10-12
 */
public interface MultiValueEntity extends Entity {
    /**
     * 获取所有的键的列表。
     *
     * @return 表示所有的键的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> keys();

    /**
     * 获取指定键的第一个值。
     *
     * @param key 表示指定键的 {@link String}。
     * <p>键<b>大小写敏感</b>。</p>
     * @return 表示指定键的第一个值的 {@link Optional}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 或空白字符串时。
     */
    Optional<String> first(String key);

    /**
     * 获取指定键的所有值的列表。
     *
     * @param key 表示指定键的 {@link String}。
     * <p>键<b>大小写敏感</b>。</p>
     * @return 表示指定键的所有值的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 或空白字符串时。
     */
    List<String> all(String key);

    /**
     * 获取所有的数据对的数量。
     *
     * @return 表示所有的数据对数量的 {@code int}。
     */
    int size();

    /**
     * 输出当前实体对象的文本形式。
     *
     * @return 表示当前实体对象的文本形式的 {@link String}。
     */
    @Override
    String toString();

    /**
     * 通过指定的键值对映射，创建多值格式的消息体数据。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param map 表示指定的键值对映射的 {@link MultiValueMap}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @return 表示创建出来的多值格式的消息体数据的 {@link MultiValueEntity}。
     */
    static MultiValueEntity create(HttpMessage httpMessage, MultiValueMap<String, String> map) {
        return new DefaultMultiValueEntity(httpMessage, map);
    }
}
