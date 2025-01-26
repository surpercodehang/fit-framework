/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.GenericableExecutor;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.exception.FitException;

/**
 * 表示 {@link GenericableExecutor} 的单播调用实现。
 *
 * @author 季聿阶
 * @since 2023-03-27
 */
public class UnicastGenericableExecutor extends AbstractUnicastGenericableExecutor {
    @Override
    protected Object execute(Fitable fitable, InvocationContext context, Object[] args) {
        try {
            return fitable.execute(context, args);
        } catch (Throwable e) {
            throw FitException.wrap(e, fitable.genericable().id(), fitable.id());
        }
    }
}
