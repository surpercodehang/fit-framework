/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.client.InvokerFactory;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.RouterFactory;

import java.lang.reflect.Method;

/**
 * 为 {@link RouterFactory} 提供默认实现。
 *
 * @author 季聿阶
 * @since 2021-10-26
 */
public class DefaultRouterFactory implements RouterFactory {
    private final InvokerFactory invokerFactory;

    public DefaultRouterFactory(InvokerFactory invokerFactory) {
        this.invokerFactory = notNull(invokerFactory, "The invoker factory cannot be null.");
    }

    @Override
    public Router create(String genericableId, boolean isMicro, Method genericableMethod) {
        return new DefaultRouter(this.invokerFactory, genericableId, isMicro, genericableMethod);
    }
}
