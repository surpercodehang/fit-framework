/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client;

import modelengine.fit.http.HttpClassicRequest;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.header.ConfigurableCookieCollection;
import modelengine.fit.http.protocol.ConfigurableMessageHeaders;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.model.MultiValueMap;

import java.lang.reflect.Type;

/**
 * 表示经典的客户端的 Http 请求。
 *
 * @author 季聿阶
 * @since 2022-11-25
 */
public interface HttpClassicClientRequest extends HttpClassicRequest {
    /**
     * 获取 Http 请求的可修改的消息头集合。
     * <p>注意：如果要修改 Cookie 相关的信息，请不要直接在当前对象中操作，请使用 {@link #cookies()} 进行修改。</p>
     *
     * @return 表示 Http 请求的可修改消息头集合的 {@link ConfigurableMessageHeaders}。
     */
    @Override
    ConfigurableMessageHeaders headers();

    /**
     * 获取 Http 请求的可修改的 Cookie 集合。
     *
     * @return 表示 Http 请求的可修改的 Cookie 集合的 {@link ConfigurableCookieCollection}。
     */
    @Override
    ConfigurableCookieCollection cookies();

    /**
     * 设置 Http 请求的消息体内容。
     *
     * @param entity 表示 Http 请求的消息体内容的 {@link Entity}。
     */
    void entity(Entity entity);

    /**
     * 设置 Http 请求的 Form 格式的消息体内容。
     *
     * @param form 表示 Http 请求的 Form 格式的消息体内容的 {@link MultiValueMap}{@code <}{@link String}{@code ,
     * }{@link String}{@code >}。
     */
    void formEntity(MultiValueMap<String, String> form);

    /**
     * 设置 Http 请求的 Json 格式的消息体内容。
     *
     * @param jsonObject 表示 Http 请求的 Json 格式的消息体内容的 {@link Object}。
     */
    void jsonEntity(Object jsonObject);

    /**
     * 发送当前 Http 请求，交换 Http 响应。
     *
     * @return 表示交换回来的 Http 响应的 {@link HttpClassicClientResponse}。
     * @throws modelengine.fitframework.exception.TimeoutException 当发生客户端超时异常时。
     * @throws modelengine.fitframework.exception.ClientException 当发生客户端异常时。
     */
    HttpClassicClientResponse<Object> exchange();

    /**
     * 发送当前 Http 请求，交换 Http 响应。
     *
     * @param responseType 表示期待的返回值类型的 {@link Type}。
     * @param <T> 表示期待的返回值类型的 {@link T}。
     * @return 表示交换回来的 Http 响应的 {@link HttpClassicClientResponse}。
     * @throws modelengine.fitframework.exception.TimeoutException 当发生客户端超时异常时。
     * @throws modelengine.fitframework.exception.ClientException 当发生客户端异常时。
     */
    <T> HttpClassicClientResponse<T> exchange(Type responseType);

    /**
     * 延迟发送当前 Http 请求，交换 Http 流式响应。
     *
     * @return 表示交换回来的 Http 流式响应的 {@link Choir}{@code <}{@link Object}{@code >}。
     */
    Choir<Object> exchangeStream();

    /**
     * 延迟发送当前 Http 请求，交换 Http 流式响应。
     *
     * @param responseType 表示期待的返回的流式数据值类型的 {@link Type}。
     * @param <T> 表示期待的返回的流式数据值类型的 {@link T}。
     * @return 表示交换回来的 Http 流式响应的 {@link Choir}{@code <}{@link T}{@code >}。
     */
    <T> Choir<T> exchangeStream(Type responseType);
}
