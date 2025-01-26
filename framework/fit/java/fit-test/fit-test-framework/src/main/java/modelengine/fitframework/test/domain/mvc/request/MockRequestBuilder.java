/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.mvc.request;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.model.MultiValueMap;
import modelengine.fitframework.test.domain.mvc.MockMvc;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 为模拟 {@link MockMvc} 中的客户端提供请求的构建者。
 *
 * @author 王攀博
 * @since 2024-04-09
 */
public class MockRequestBuilder implements RequestBuilder {
    private static final String BASE_URL = "http://127.0.0.1:";

    private final HttpRequestMethod method;
    private final MultiValueMap<String, String> params = MultiValueMap.create();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, List<String>> headersMap = new HashMap<>();
    private String url;
    private Type responseType;
    private int port;
    private HttpClassicClient httpClassicClient;
    private Entity entity;
    private Object jsonObject;
    private MultiValueMap<String, String> formEntity;

    MockRequestBuilder(HttpRequestMethod method, String url) {
        this.method = Validation.notNull(method, "The request method cannot be blank.");
        this.url = Validation.notNull(url, "The request url cannot be blank.");
    }

    /**
     * 为请求插件结构体添加键值对参数。
     *
     * @param name 表示请求体内请求参数的键值 {@link String}。
     * @param value 表示请求体内请求参数的值 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder param(String name, String value) {
        this.params.add(name, value);
        return this;
    }

    /**
     * 为请求插件结构体添加键值对参数。
     *
     * @param name 表示请求体内请求参数的键值 {@link String}。
     * @param values 表示待设置的请求参数列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder param(String name, List<String> values) {
        this.params.addAll(name, values);
        return this;
    }

    /**
     * 设置客户端请求结果的类型。
     *
     * @param responseType 表示请求结果的返回值类型 {@link Type}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder responseType(Type responseType) {
        this.responseType = responseType;
        return this;
    }

    /**
     * 设置客户端请求的端口号。
     *
     * @param port 表示客户端请求的端口号 {@link int}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder port(int port) {
        this.port = port;
        return this;
    }

    /**
     * 设置请求结构体的消息头。
     *
     * @param name 表示待设置的消息头的名字的 {@link String}。
     * @param header 表示待设置的消息头内容的 {@link String}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder header(String name, String header) {
        this.headers.put(name, header);
        return this;
    }

    /**
     * 设置请求结构体的消息头。
     *
     * @param name 表示待设置的消息头的名字的 {@link String}。
     * @param headers 表示待设置的消息头内容列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder header(String name, List<String> headers) {
        this.headersMap.put(name, headers);
        return this;
    }

    /**
     * 设置请求结构体的消息体内容。
     *
     * @param entity 表示 Http 请求的消息体内容的 {@link Entity}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder entity(Entity entity) {
        this.entity = entity;
        return this;
    }

    /**
     * 设置 Http 请求的 Form 格式的消息体内容。
     *
     * @param formEntity 表示 Http 请求的 Form 格式的消息体内容的 {@link MultiValueMap}{@code <}{@link String}{@code ,
     * }{@link String}{@code >}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder formEntity(MultiValueMap<String, String> formEntity) {
        this.formEntity = formEntity;
        return this;
    }

    /**
     * 设置 Http 请求的 Json 格式的消息体内容。
     *
     * @param jsonObject 表示 Http 请求的 Json 格式的消息体内容的 {@link Object}。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder jsonEntity(Object jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    /**
     * 设置 Http 客户端。
     *
     * @param httpClassicClient 表示测试需要的客户端。
     * @return 表示客户端请求参数的建造者 {@link MockRequestBuilder}。
     */
    @Override
    public MockRequestBuilder client(HttpClassicClient httpClassicClient) {
        this.httpClassicClient = httpClassicClient;
        return this;
    }

    /**
     * 构建客户端的请求参数。
     *
     * @return 表示返回构建完成的客户端的请求参数 {@link RequestParam}。
     */
    @Override
    public RequestParam build() {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL + this.port + this.url);
        if (!this.params.isEmpty()) {
            urlBuilder.append("?");
            this.params.forEach((key, values) -> {
                values.forEach((value) -> urlBuilder.append(key).append("=").append(value).append("&"));
            });
            urlBuilder.setLength(urlBuilder.length() - 1);
        }
        this.url = urlBuilder.toString();

        HttpClassicClientRequest request = this.httpClassicClient.createRequest(this.method, this.url);
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            request.headers().set(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, List<String>> entry : this.headersMap.entrySet()) {
            request.headers().set(entry.getKey(), entry.getValue());
        }
        if (this.entity != null) {
            request.entity(this.entity);
        } else if (this.jsonObject != null) {
            request.jsonEntity(this.jsonObject);
        } else if (this.formEntity != null) {
            request.formEntity(this.formEntity);
        }
        return new RequestParam(this.responseType, request);
    }
}
