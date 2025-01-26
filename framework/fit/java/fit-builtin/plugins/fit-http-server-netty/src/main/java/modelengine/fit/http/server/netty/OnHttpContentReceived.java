/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;

import java.io.IOException;

/**
 * 表示收到 Http 请求的内容的事件。
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public interface OnHttpContentReceived {
    /**
     * 当收到 Http 请求内容时触发的回调。
     *
     * @param content 表示 Http 请求内容的 {@link HttpContent}。
     * @throws IOException 当发生 I/O 异常时。
     */
    void receiveHttpContent(HttpContent content) throws IOException;

    /**
     * 当收到最后一个 Http 请求内容时触发的回调。
     *
     * @param content 表示最后一个 Http 请求内容的 {@link LastHttpContent}。
     * @throws IOException 当发生 I/O 异常时。
     */
    void receiveLastHttpContent(LastHttpContent content) throws IOException;
}
