/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import modelengine.fit.http.AttributeCollection;
import modelengine.fit.http.HttpClassicRequest;
import modelengine.fit.http.HttpResource;
import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.ServerRequest;
import modelengine.fit.http.server.support.DefaultHttpClassicServerRequest;

import java.io.Closeable;

/**
 * 表示经典的服务端的 Http 请求。
 *
 * @author 季聿阶
 * @since 2022-07-07
 */
public interface HttpClassicServerRequest extends HttpClassicRequest, Closeable {
    /**
     * 获取 Http 请求的所有属性集合。
     *
     * @return 表示 Http 请求的所有属性集合的 {@link AttributeCollection}。
     */
    AttributeCollection attributes();

    /**
     * 表示 Http 请求的本地地址。
     *
     * @return 表示本地地址的 {@link Address}。
     */
    Address localAddress();

    /**
     * 表示 Http 请求的远端地址。
     *
     * @return 表示远端地址的 {@link Address}。
     */
    Address remoteAddress();

    /**
     * 获取 Http 请求是否为安全的的标记。
     *
     * @return 如果 Http 请求安全，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isSecure();

    /**
     * 获取 Http 消息的消息体的结构化数据的二进制内容。
     *
     * @return 表示消息体的结构化数据的二进制内容的 {@code byte[]}。
     */
    byte[] entityBytes();

    /**
     * 创建经典的服务端的 Http 请求对象。
     *
     * @param httpResource 表示 Http 的资源的 {@link HttpResource}。
     * @param serverRequest 表示服务端的 Http 请求的 {@link ServerRequest}。
     * @return 表示创建出来的经典的服务端的 Http 请求对象的 {@link HttpClassicServerRequest}。
     */
    static HttpClassicServerRequest create(HttpResource httpResource, ServerRequest serverRequest) {
        return new DefaultHttpClassicServerRequest(httpResource, serverRequest);
    }
}
