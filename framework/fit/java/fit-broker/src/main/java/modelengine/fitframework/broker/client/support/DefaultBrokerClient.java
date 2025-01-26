/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.support;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.RouterFactory;
import modelengine.fitframework.broker.client.RouterRetrievalFailureException;
import modelengine.fitframework.broker.util.AnnotationUtils;
import modelengine.fitframework.util.GenericableUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 为 {@link BrokerClient} 提供默认实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-08-19
 */
public class DefaultBrokerClient implements BrokerClient {
    private final RouterFactory routerFactory;

    public DefaultBrokerClient(RouterFactory routerFactory) {
        this.routerFactory = notNull(routerFactory, "The router factory cannot be null.");
    }

    @Override
    public Router getRouter(Class<?> genericableClass) {
        notNull(genericableClass, () -> new RouterRetrievalFailureException("The genericable class cannot be null."));
        isTrue(genericableClass.isInterface(),
                () -> new RouterRetrievalFailureException(StringUtils.format(
                        "The genericable class is not a interface. [class={0}]",
                        genericableClass.getName())));
        String genericableId = AnnotationUtils.getGenericableId(genericableClass)
                .orElseThrow(() -> new RouterRetrievalFailureException(StringUtils.format(
                        "No genericable id declared in class. [class={0}]",
                        genericableClass.getName())));
        try {
            Method genericableMethod = GenericableUtils.getMacroGenericableMethod(genericableClass)
                    .orElseThrow(() -> new RouterRetrievalFailureException(StringUtils.format(
                            "No genericable method declared in class. [class={0}]",
                            genericableClass.getName())));
            return this.getRouter(genericableId, false, genericableMethod);
        } catch (IllegalStateException e) {
            throw new RouterRetrievalFailureException(e.getMessage(), e);
        }
    }

    @Override
    public Router getRouter(Class<?> genericableClass, String genericableId) {
        notNull(genericableClass, () -> new RouterRetrievalFailureException("The genericable class cannot be null."));
        isTrue(genericableClass.isInterface(),
                () -> new RouterRetrievalFailureException(StringUtils.format(
                        "The genericable class is not a interface. [class={0}]",
                        genericableClass.getName())));
        notBlank(genericableId, () -> new RouterRetrievalFailureException("The genericable id cannot be blank."));
        for (Method method : genericableClass.getDeclaredMethods()) {
            boolean isPresent = AnnotationUtils.getGenericableId(method)
                    .filter(methodGenericableId -> Objects.equals(methodGenericableId, genericableId))
                    .isPresent();
            if (isPresent) {
                return this.getRouter(genericableId, false, method);
            }
        }
        throw new RouterRetrievalFailureException(StringUtils.format(
                "No specified genericable id declared in method. [class={0}, genericableId={1}]",
                genericableClass.getName(),
                genericableId));
    }

    @Override
    public Router getRouter(String genericableId, boolean isMicro, Method genericableMethod) {
        return this.routerFactory.create(genericableId, isMicro, genericableMethod);
    }

    @Override
    public Genericable getGenericable(Class<?> genericableClass) {
        return this.getRouter(genericableClass).route().getGenericable();
    }

    @Override
    public Genericable getGenericable(Class<?> genericableClass, String genericableId) {
        return this.getRouter(genericableClass, genericableId).route().getGenericable();
    }
}
