/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.FitableExecutor;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.inspection.Validation;

import java.util.List;

/**
 * 表示 {@link FitableExecutor} 的抽象单播调用父类。
 *
 * @author 季聿阶
 * @since 2023-03-28
 */
public abstract class AbstractUnicastFitableExecutor implements FitableExecutor {
    @Override
    public final Object execute(Fitable fitable, List<Target> targets, InvocationContext context, Object[] args) {
        Validation.notNull(targets, "The targets cannot be null.");
        Validation.equals(targets.size(), 1, "Too more targets.");
        return this.execute(fitable, targets.get(0), context, args);
    }

    /**
     * 执行指定的服务的指定地址。
     *
     * @param fitable 表示指定服务实现的 {@link Fitable}。
     * @param target 表示指定地址的 {@link Target}。
     * @param context 表示调用上下文的 {@link InvocationContext}。
     * @param args 表示调用参数列表的 {@link Object}{@code []}。
     * @return 表示调用结果的 {@link Object}。
     */
    protected abstract Object execute(Fitable fitable, Target target, InvocationContext context, Object[] args);
}
