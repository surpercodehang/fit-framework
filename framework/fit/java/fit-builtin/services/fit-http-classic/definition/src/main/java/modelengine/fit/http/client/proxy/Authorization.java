/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy;

import modelengine.fit.http.client.proxy.support.authorization.ApiKeyAuthorization;
import modelengine.fit.http.client.proxy.support.authorization.AuthorizationFactoryRegistry;
import modelengine.fit.http.client.proxy.support.authorization.BasicAuthorization;
import modelengine.fit.http.client.proxy.support.authorization.BearerAuthorization;
import modelengine.fit.http.client.proxy.support.authorization.EmptyAuthorization;
import modelengine.fit.http.server.handler.Source;

import java.util.Map;

/**
 * 管理鉴权信息的接口定义。
 *
 * @author 王攀博
 * @since 2024-11-26
 */
public interface Authorization {
    /**
     * 表示鉴权信息管理对象的类型。
     */
    String AUTH_TYPE_KEY = "type";

    /**
     * 表示鉴权信息管理对象工厂的注册中心。
     */
    AuthorizationFactoryRegistry AUTHORIZATION_FACTORY_REGISTRY = new AuthorizationFactoryRegistry();

    /**
     * 设置鉴权信息。
     *
     * @param key 表示鉴权信息的参数键值 {@link String}。
     * @param value 表示鉴权信息值的 {@link Object}。
     */
    void set(String key, Object value);

    /**
     * 基于设置的鉴权信息构建 Http 请求构建器。
     *
     * @param builder 表示通信请求的构建者的 {@link RequestBuilder}。
     */
    void assemble(RequestBuilder builder);

    /**
     * 表示基于用户名密码构建 Basic 鉴权信息。
     *
     * @param userName 表示要传入的鉴权信息的用户名的 {@link String}。
     * @param password 表示要传入的鉴权信息的密码的 {@link String}。
     * @return 表示创建的鉴权信息对象的 {@link Authorization}。
     */
    static Authorization createBasic(String userName, String password) {
        return new BasicAuthorization(userName, password);
    }

    /**
     * 表示基于用户名密码构建 ApiKey 鉴权信息管理对象。
     *
     * @param key 表示要传入的鉴权信息的键的 {@link String}。
     * @param value 表示要传入的鉴权信息的值的 {@link String}。
     * @param httpSource 表示要将鉴权信息存在的位置的 {@link Source}。
     * @return 表示创建的鉴权信息对象的 {@link Authorization}。
     */
    static Authorization createApiKey(String key, String value, Source httpSource) {
        return new ApiKeyAuthorization(key, value, httpSource);
    }

    /**
     * 表示基于用户名密码构建 Bearer 鉴权信息管理对象。
     *
     * @param token 表示要传入的鉴权信息的令牌的 {@link String}。
     * @return 表示创建的鉴权信息对象的 {@link Authorization}。
     */
    static Authorization createBearer(String token) {
        return new BearerAuthorization(token);
    }

    /**
     * 表示创建一个空的鉴权信息管理对象。
     *
     * @return 表示创建的鉴权信息对象的 {@link Authorization}。
     */
    static Authorization createEmpty() {
        return new EmptyAuthorization();
    }

    /**
     * 表示基于给定的鉴权信息创建鉴权信息管理对象。
     *
     * @param authorization 表示鉴权信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 表示创建的鉴权信息对象的 {@link Authorization}。
     */
    static Authorization create(Map<String, Object> authorization) {
        return AUTHORIZATION_FACTORY_REGISTRY.create(authorization);
    }
}
