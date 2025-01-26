/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.authorization;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.client.proxy.Authorization;
import modelengine.fit.http.client.proxy.AuthorizationFactory;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 鉴权信息管理对象工厂的注册中心。
 *
 * @author 王攀博
 * @since 2024-12-10
 */
public class AuthorizationFactoryRegistry {
    private final Map<String, AuthorizationFactory> registry = new ConcurrentHashMap<>();
    private final AuthorizationFactory defaultAuthorizationFactory = new EmptyAuthorizationFactory();

    /**
     * 表示默认构造鉴权信息管理对象工厂的注册中心。
     */
    public AuthorizationFactoryRegistry() {
        this.registry.put(ApiKeyAuthorizationFactory.TYPE, new ApiKeyAuthorizationFactory());
        this.registry.put(BasicAuthorizationFactory.TYPE, new BasicAuthorizationFactory());
        this.registry.put(BearerAuthorizationFactory.TYPE, new BearerAuthorizationFactory());
        this.registry.put(EmptyAuthorizationFactory.TYPE, this.defaultAuthorizationFactory);
    }

    private boolean isBuiltIn(String type) {
        return StringUtils.equals(type, ApiKeyAuthorizationFactory.TYPE)
                || StringUtils.equals(type, BasicAuthorizationFactory.TYPE)
                || StringUtils.equals(type, BearerAuthorizationFactory.TYPE);
    }

    /**
     * 向鉴权信息工厂注册中心注册。
     *
     * @param type 表示待注册的工厂类型的 {@link String}。
     * @param factory 表示待注册的工厂的 {@link AuthorizationFactory}。
     */
    public void register(String type, AuthorizationFactory factory) {
        if (StringUtils.isBlank(type) || this.isBuiltIn(type) || factory == null) {
            return;
        }
        this.registry.put(type, factory);
    }

    /**
     * 向鉴权信息工厂注册中心取消注册。
     *
     * @param type 表示待取消注册的工厂类型的 {@link String}。
     */
    public void unregister(String type) {
        if (StringUtils.isBlank(type) || this.isBuiltIn(type)) {
            return;
        }
        this.registry.remove(type);
    }

    /**
     * 表示基于给定的鉴权信息创建鉴权信息管理对象。
     *
     * @param authorization 表示鉴权信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 表示创建的鉴权信息对象的 {@link Authorization}。
     */
    public Authorization create(Map<String, Object> authorization) {
        if (authorization == null) {
            return this.defaultAuthorizationFactory.create(null);
        }
        Object authType = authorization.get(Authorization.AUTH_TYPE_KEY);
        if (!(authType instanceof String)) {
            return this.defaultAuthorizationFactory.create(authorization);
        }
        String type = cast(authType);
        return this.registry.getOrDefault(type, this.defaultAuthorizationFactory).create(authorization);
    }
}
