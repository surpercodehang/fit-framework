/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.starter.spring;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.util.StringUtils;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 表示 FIT 定义的动态代理的 {@link FactoryBean}。
 *
 * @author 杭潇
 * @since 2025-02-22
 */
public class FitProxyFactoryBean<T> implements FactoryBean<T> {
    private final BrokerClient brokerClient;
    private final Class<T> interfaceType;

    public FitProxyFactoryBean(BrokerClient brokerClient, Class<T> interfaceType) {
        this.brokerClient = brokerClient;
        this.interfaceType = interfaceType;
    }

    @Override
    public T getObject() {
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            Genericable genericable = method.getAnnotation(Genericable.class);
            if (genericable == null) {
                throw new IllegalStateException(StringUtils.format(
                        "The method must be annotated with Genericable. [method={0}]",
                        method.getName()));
            }
            String genericableId = genericable.id();
            return this.brokerClient.getRouter(this.interfaceType, genericableId).route().invoke(args);
        };
        return cast(Proxy.newProxyInstance(this.interfaceType.getClassLoader(),
                new Class[] {this.interfaceType},
                invocationHandler));
    }

    @Override
    public Class<?> getObjectType() {
        return this.interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
