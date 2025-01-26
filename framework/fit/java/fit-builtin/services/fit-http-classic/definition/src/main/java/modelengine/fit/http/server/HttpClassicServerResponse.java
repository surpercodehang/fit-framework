/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import modelengine.fit.http.HttpClassicResponse;
import modelengine.fit.http.HttpResource;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.WritableBinaryEntity;
import modelengine.fit.http.header.ConfigurableCookieCollection;
import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fit.http.protocol.ServerResponse;
import modelengine.fit.http.server.support.DefaultHttpClassicServerResponse;

import java.io.Closeable;
import java.io.IOException;

/**
 * 表示经典的服务端的 Http 响应。
 *
 * @author 季聿阶
 * @since 2022-11-25
 */
public interface HttpClassicServerResponse extends HttpClassicResponse, Closeable {
    /**
     * 设置 Http 响应的状态码。
     *
     * @param statusCode 表示 Http 响应的状态码的 {@code int}。
     */
    void statusCode(int statusCode);

    /**
     * 设置 Http 响应的状态信息。
     *
     * @param reasonPhrase 表示 Http 响应的状态信息的 {@link String}。
     */
    void reasonPhrase(String reasonPhrase);

    /**
     * 获取 Http 响应的消息头集合。
     * <p>注意：如果要修改 Cookie 相关的信息，请不要直接在当前对象中操作，请使用 {@link #cookies()} 进行修改。</p>
     *
     * @return 表示 Http 响应的消息头集合的 {@link ConfigurableMessageHeaders}。
     */
    @Override
    ConfigurableMessageHeaders headers();

    /**
     * 获取 Http 响应的 Cookie 集合。
     *
     * @return 表示 Http 响应的 Cookie 集合的 {@link ConfigurableCookieCollection}。
     */
    @Override
    ConfigurableCookieCollection cookies();

    /**
     * 设置 Http 响应的消息体的结构化数据。
     * <p><b>注意：该方法与 {@link #writableBinaryEntity()} 不能同时使用。</b></p>
     *
     * @param entity 表示待设置的 Http 响应的消息体的结构化数据的 {@link Entity}。
     */
    void entity(Entity entity);

    /**
     * 获取 Http 响应的返回输出流。
     * <p><b>注意：该方法与 {@link #entity(Entity)} 不能同时使用，调用该方法时，会立即将 Http 消息头发送。</b></p>
     *
     * @return 表示 Http 响应的返回输出流的 {@link WritableBinaryEntity}。
     * @throws IOException 写入过程发生 IO 异常。
     */
    WritableBinaryEntity writableBinaryEntity() throws IOException;

    /**
     * 发送当前 Http 响应。
     */
    void send();

    /**
     * 创建经典的服务端的 Http 响应对象。
     *
     * @param httpResource 表示 Http 的资源的 {@link HttpResource}。
     * @param serverResponse 表示服务端的 Http 响应的 {@link ServerResponse}。
     * @return 表示创建的经典的服务端的 Http 响应对象的 {@link HttpClassicServerResponse}。
     */
    static HttpClassicServerResponse create(HttpResource httpResource, ServerResponse serverResponse) {
        return new DefaultHttpClassicServerResponse(httpResource, serverResponse);
    }
}
