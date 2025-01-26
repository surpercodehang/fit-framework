/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.proxy.support.DefaultRequestBuilder;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.protocol.HttpRequestMethod;

/**
 * 表示 Http 请求提供建造者。
 *
 * @author 王攀博
 * @since 2024-06-08
 */
public interface RequestBuilder {
    /**
     * 设置 Http 客户端。
     *
     * @param httpClassicClient 表示测试需要的客户端。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder client(HttpClassicClient httpClassicClient);

    /**
     * 设置客户端请协议。
     *
     * @param method 表示客户端请求的协议 {@link HttpRequestMethod}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder method(HttpRequestMethod method);

    /**
     * 设置客户端请协议。
     *
     * @param protocol 表示客户端请求的协议 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder protocol(String protocol);

    /**
     * 设置客户端请求域名。
     *
     * @param domain 表示客户端请求的域名 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder domain(String domain);

    /**
     * 设置客户端请求模板。
     *
     * @param pathPattern 表示客户端请求的路径模板的 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder pathPattern(String pathPattern);

    /**
     * 设置客户端请求路径段。
     *
     * @param key 表示客户端请求的路径变量的 {@link String}。
     * @param pathVariable 表示客户端请求的路径变量的值的 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder pathVariable(String key, String pathVariable);

    /**
     * 为请求插件结构体添加键值对参数。
     *
     * @param key 表示请求体内请求参数的键的 {@link String}。
     * @param value 表示请求体内请求参数的值的 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder query(String key, String value);

    /**
     * 设置请求结构体的消息头。
     *
     * @param name 表示待设置的消息头的名字的 {@link String}。
     * @param header 表示待设置的消息头内容的 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder header(String name, String header);

    /**
     * 设置请求结构体的 Cookie。
     *
     * @param key 表示待设置的 Cookie 的键的 {@link String}。
     * @param value 表示待设置的 Cookie 的值的 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder cookie(String key, String value);

    /**
     * 设置请求结构体的消息体内容。
     *
     * @param entity 表示 Http 请求的消息体内容的 {@link Entity}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder entity(Entity entity);

    /**
     * 设置 Http 请求的 Form 格式的消息体内容。
     *
     * @param key 表示 Http 请求的 Form 格式的消息体内容键值对键的 {@link String}。
     * @param value 表示 Http 请求的 Form 格式的消息体内容键值对值的 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder formEntity(String key, String value);

    /**
     * 设置 Http 请求的 Json 格式的消息体内容。
     *
     * @param propertyValuePath 表示 Http 请求的 json 格式的消息体内容属性路径的 {@link String}。
     * @param value 表示 Http 请求的消息体内容属性值的 {@link Object}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder jsonEntity(String propertyValuePath, Object value);

    /**
     * 设置 Http 请求的鉴权类型。
     *
     * @param authorization 表示 Http 请求的鉴权信息结构的 {@link Authorization}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder authorization(Authorization authorization);

    /**
     * 设置 Http 请求的鉴权信息。
     *
     * @param key 表示 Http 鉴权信息的参数键值的 {@link String}。
     * @param value 表示鉴权信息的值的 {@link Object}。
     * @return 表示客户端请求参数的建造者 {@link RequestBuilder}。
     */
    RequestBuilder authorizationInfo(String key, Object value);

    /**
     * 构建客户端的请求参数。
     *
     * @return 表示返回构建完成的客户端的请求参数 {@link HttpClassicClientRequest}。
     */
    HttpClassicClientRequest build();

    /**
     * 创建请求构建器。
     *
     * @return 表示创建出的请求构建器的 {@link RequestBuilder}。
     */
    static RequestBuilder create() {
        return new DefaultRequestBuilder();
    }
}
