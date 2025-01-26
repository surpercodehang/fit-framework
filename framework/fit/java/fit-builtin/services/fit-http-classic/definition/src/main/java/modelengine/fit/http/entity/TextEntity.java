/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity;

import static java.nio.charset.StandardCharsets.UTF_8;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.support.DefaultTextEntity;

import java.nio.charset.Charset;

/**
 * 表示文本格式的消息体数据。
 *
 * @author 季聿阶
 * @since 2022-10-11
 */
public interface TextEntity extends Entity {
    /**
     * 获取实体的纯文本内容。
     *
     * @return 表示实体纯文本内容的 {@link String}。
     */
    String content();

    /**
     * 通过指定的字节数组，按照 {@link java.nio.charset.StandardCharsets#UTF_8} 创建文本消息体数据。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param bytes 表示指定字节数组的 {@code byte[]}。
     * @return 表示创建出来的文本消息体数据的 {@link TextEntity}。
     */
    static TextEntity create(HttpMessage httpMessage, byte[] bytes) {
        return create(httpMessage, bytes, UTF_8);
    }

    /**
     * 通过指定的字节数组，按照指定编码格式创建文本消息体数据。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param bytes 表示指定字节数组的 {@code byte[]}。
     * @param charset 表示指定编码格式的 {@link Charset}。
     * @return 表示创建出来的文本消息体数据的 {@link TextEntity}。
     */
    static TextEntity create(HttpMessage httpMessage, byte[] bytes, Charset charset) {
        return new DefaultTextEntity(httpMessage, new String(bytes, charset));
    }

    /**
     * 通过指定的文本内容创建文本消息体数据。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param content 表示指定文本内容的 {@link String}。
     * @return 表示创建出来的文本消息体数据的 {@link TextEntity}。
     */
    static TextEntity create(HttpMessage httpMessage, String content) {
        return new DefaultTextEntity(httpMessage, content);
    }
}
