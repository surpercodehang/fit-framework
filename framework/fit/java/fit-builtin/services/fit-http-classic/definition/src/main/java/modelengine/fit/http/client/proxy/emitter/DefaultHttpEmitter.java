/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.emitter;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.client.HttpClientException;
import modelengine.fit.http.client.proxy.Authorization;
import modelengine.fit.http.client.proxy.HttpEmitter;
import modelengine.fit.http.client.proxy.PropertyValueApplier;
import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.util.List;

/**
 * 表示 Http 客户端的代理执行者的默认实现。
 *
 * @author 王攀博
 * @since 2024-06-14
 */

public class DefaultHttpEmitter implements HttpEmitter {
    private final List<PropertyValueApplier> appliers;
    private final HttpClassicClient client;
    private final HttpRequestMethod method;
    private final String protocol;
    private final String domain;
    private final String pathPattern;
    private final Authorization authorization;

    /**
     * 表示提供 Http 客户端的代理执行者的默认实现的构造。
     *
     * @param appliers 表示和每一个入参对应的属性填充器的 {@link PropertyValueApplier}。
     * @param client 表示 Http 客户端的 {@link HttpClassicClient}。
     * @param method 表示 Http 请求的方法的 {@link HttpRequestMethod}。
     * @param protocol 表示 Http 请求的协议的 {@link String}。
     * @param domain 表示 Http 请求域名的 {@link String}。
     * @param pathPattern 表示 Http 请求的路径模板的 {@link String}。
     * @param authorization 表示 Http 请求的鉴权信息的 {@link Authorization}。
     */
    public DefaultHttpEmitter(List<PropertyValueApplier> appliers, HttpClassicClient client, HttpRequestMethod method,
            String protocol, String domain, String pathPattern, Authorization authorization) {
        this.appliers = notNull(appliers, "The emitter cannot be null.");
        this.client = notNull(client, "The client cannot be null.");
        this.method = notNull(method, "The method cannot be null.");
        this.protocol = notBlank(protocol, "The protocol cannot be null.");
        this.domain = notBlank(domain, "The domain cannot be null.");
        this.pathPattern = notBlank(pathPattern, "The path pattern cannot be null.");
        this.authorization = authorization;
    }

    @Override
    public <T> HttpClassicClientResponse<T> emit(Object[] args) throws HttpClientException {
        Validation.equals(args.length,
                this.appliers.size(),
                () -> new HttpClientException(StringUtils.format(
                        "The args length not equals to the size of appliers. [args={0}, appliers={1}]",
                        args.length,
                        this.appliers.size())));

        RequestBuilder requestBuilder = RequestBuilder.create()
                .client(this.client)
                .method(this.method)
                .protocol(this.protocol)
                .domain(this.domain)
                .pathPattern(this.pathPattern)
                .authorization(this.authorization);

        for (int i = 0; i < args.length; ++i) {
            this.appliers.get(i).apply(requestBuilder, args[i]);
        }
        HttpClassicClientRequest request = requestBuilder.build();
        return this.client.exchange(request, Object.class);
    }
}
