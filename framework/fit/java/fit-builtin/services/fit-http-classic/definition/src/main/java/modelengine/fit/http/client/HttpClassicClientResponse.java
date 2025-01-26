/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client;

import modelengine.fit.http.HttpClassicResponse;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.entity.TextEventStreamEntity;

import java.io.Closeable;
import java.util.Optional;

/**
 * 表示经典的客户端的 Http 响应。
 *
 * @author 季聿阶
 * @since 2022-11-25
 */
public interface HttpClassicClientResponse<T> extends HttpClassicResponse, Closeable {
    /**
     * 获取 Http 响应中的结构体类型的实体对象。
     *
     * @return 表示 Http 响应中的结构体类型的实体对象的 {@link Optional}{@code <}{@link ObjectEntity}{@code <}{@link
     * T}{@code >>}。
     */
    Optional<ObjectEntity<T>> objectEntity();

    /**
     * 获取 Http 响应中的文本类型的实体对象。
     *
     * @return 表示 Http 响应中的文本类型的实体对象的 {@link Optional}{@code <}{@link TextEntity}{@code >}。
     */
    Optional<TextEntity> textEntity();

    /**
     * 获取 Http 响应中的文本事件流的实体对象。
     *
     * @return 表示 Http 响应中的文本事件流的实体对象的 {@link Optional}{@code <}{@link TextEventStreamEntity}{@code >}。
     */
    Optional<TextEventStreamEntity> textEventStreamEntity();

    /**
     * 获取 Http 消息的消息体的结构化数据的二进制内容。
     * <p>如果已经调用过 {@link #entity()} 方法来获取消息体，则无法调用当前方法。</p>
     *
     * @return 表示消息体的结构化数据的二进制内容的 {@code byte[]}。
     */
    byte[] entityBytes();
}
