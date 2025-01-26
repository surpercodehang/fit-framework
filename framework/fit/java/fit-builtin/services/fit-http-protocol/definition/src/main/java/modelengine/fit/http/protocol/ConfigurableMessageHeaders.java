/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import modelengine.fit.http.protocol.support.DefaultMessageHeaders;

import java.util.List;

/**
 * 表示可读可写的 Http 消息头集合。
 *
 * @author 季聿阶
 * @since 2022-07-07
 */
public interface ConfigurableMessageHeaders extends MessageHeaders {
    /**
     * 添加一个指定的消息头。
     *
     * @param name 表示待添加的消息头的名字的 {@link String}。
     * <p>消息头名字<b>大小写不敏感</b>。</p>
     * @param header 表示待添加的消息头内容的 {@link String}。
     * @return 表示当前的消息头集合的 {@link ConfigurableMessageHeaders}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    ConfigurableMessageHeaders add(String name, String header);

    /**
     * 设置一个指定的消息头。
     *
     * @param name 表示待设置的消息头的名字的 {@link String}。
     * <p>消息头名字<b>大小写不敏感</b>。</p>
     * @param header 表示待设置的消息头内容的 {@link String}。
     * <p>当 {@code header} 为 {@code null} 或空白字符串时，则清除指定的消息头。</p>
     * @return 表示当前的消息头集合的 {@link ConfigurableMessageHeaders}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    ConfigurableMessageHeaders set(String name, String header);

    /**
     * 设置一个指定的消息头。
     *
     * @param name 表示待设置的消息头的名字的 {@link String}。
     * <p>消息头名字<b>大小写不敏感</b>。</p>
     * @param headers 表示待设置的消息头内容列表的 {@link List}{@code <}{@link String}{@code >}。
     * <p>当 {@code headers} 为空或者仅包含 {@code null} 或空白字符串时，则清除指定的消息头。</p>
     * @return 表示当前的消息头集合的 {@link ConfigurableMessageHeaders}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    ConfigurableMessageHeaders set(String name, List<String> headers);

    /**
     * 清除一个指定的消息头。
     *
     * @param name 表示待清除的消息头的名字的 {@link String}。
     * <p>消息头名字<b>大小写不敏感</b>。</p>
     * @return 表示当前的消息头集合的 {@link ConfigurableMessageHeaders}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 或空白字符串时。
     */
    ConfigurableMessageHeaders clear(String name);

    /**
     * 创建一个新的可读可写的消息头集合。
     *
     * @return 表示创建出来的可读可写的消息头集合的 {@link ConfigurableMessageHeaders}。
     */
    static ConfigurableMessageHeaders create() {
        return new DefaultMessageHeaders();
    }
}
