/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.mvc.request;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.entity.Entity;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.test.domain.mvc.MockMvc;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 为模拟 {@link MockMvc} 提供常见的共用方法。
 *
 * @author 王攀博
 * @since 2024-04-09
 */
public interface RequestBuilder {
    /**
     * 为请求插件结构体添加键值对参数。
     *
     * @param name 表示请求体内请求参数的键值 {@link String}。
     * @param value 表示请求体内请求参数的值 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder param(String name, String value);

    /**
     * 为请求插件结构体添加键值对参数。
     *
     * @param name 表示请求体内请求参数的键值 {@link String}。
     * @param values 表示待设置的请求参数列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder param(String name, List<String> values);

    /**
     * 设置客户端请求结果的类型。
     *
     * @param responseType 表示请求结果的返回值类型 {@link Type}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder responseType(Type responseType);

    /**
     * 设置客户端请求的端口号。
     *
     * @param port 表示客户端请求的端口号 {@link int}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder port(int port);

    /**
     * 设置请求结构体的消息头。
     *
     * @param name 表示待设置的消息头的名字的 {@link String}。
     * @param header 表示待设置的消息头内容的 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder header(String name, String header);

    /**
     * 设置请求结构体的消息头。
     *
     * @param name 表示待设置的消息头的名字的 {@link String}。
     * @param headers 表示待设置的消息头内容列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder header(String name, List<String> headers);

    /**
     * 设置请求结构体的消息体内容。
     *
     * @param entity 表示 Http 请求的消息体内容的 {@link Entity}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder entity(Entity entity);

    /**
     * 设置 Http 请求的 Form 格式的消息体内容。
     *
     * @param formEntity 表示 Http 请求的 Form 格式的消息体内容的 {@link MultiValueMap}{@code <}{@link String}{@code ,
     * }{@link String}{@code >}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder formEntity(MultiValueMap<String, String> formEntity);

    /**
     * 设置 Http 请求的 Json 格式的消息体内容。
     *
     * @param jsonObject 表示 Http 请求的 Json 格式的消息体内容的 {@link Object}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder jsonEntity(Object jsonObject);

    /**
     * 设置 Http 客户端。
     *
     * @param httpClassicClient 表示测试需要的客户端。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    MockRequestBuilder client(HttpClassicClient httpClassicClient);

    /**
     * 构建客户端的请求参数。
     *
     * @return 表示返回构建完成的客户端的请求参数 {@link RequestParam}。
     */
    RequestParam build();
}
