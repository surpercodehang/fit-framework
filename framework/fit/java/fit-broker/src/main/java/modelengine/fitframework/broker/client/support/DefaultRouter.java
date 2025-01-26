/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.InvokerFactory;
import modelengine.fitframework.broker.client.Router;

import java.lang.reflect.Method;

/**
 * {@link Router} 的默认实现。
 *
 * @author 季聿阶
 * @since 2021-06-17
 */
public class DefaultRouter implements Router {
    private final InvokerFactory invokerFactory;
    private final String genericableId;
    private final boolean isMicro;
    private final Method genericableMethod;

    DefaultRouter(InvokerFactory invokerFactory, String genericableId, boolean isMicro, Method genericableMethod) {
        this.invokerFactory = notNull(invokerFactory, "The invoker factory cannot be null.");
        this.genericableId = notBlank(genericableId, "The genericable id to route cannot be blank.");
        this.isMicro = isMicro;
        this.genericableMethod = genericableMethod;
    }

    @Override
    public Invoker route(Filter filter) {
        return this.invokerFactory.create(this.genericableId, this.isMicro, this.genericableMethod, filter);
    }
}
