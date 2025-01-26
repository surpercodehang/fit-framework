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
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.exception.RetryableException;
import modelengine.fitframework.util.ExceptionUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;

/**
 * 表示 {@link GenericableExecutor} 的重试调用实现。
 *
 * @author 季聿阶
 * @since 2023-03-27
 */
public class RetryableGenericableExecutor extends AbstractUnicastGenericableExecutor {
    private final GenericableExecutor executor;

    RetryableGenericableExecutor(GenericableExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected Object execute(Fitable fitable, InvocationContext context, Object[] args) {
        int retry = context.retry() + 1;
        RetryableException actualException;
        do {
            try {
                retry--;
                return this.executor.execute(Collections.singletonList(fitable), context, args);
            } catch (RetryableException e) {
                actualException = e;
            } catch (MethodInvocationException e) {
                Throwable cause = ExceptionUtils.getActualCause(e);
                if (cause instanceof RetryableException) {
                    actualException = ObjectUtils.cast(cause);
                } else {
                    throw FitException.wrap(cause, fitable.genericable().id(), fitable.id());
                }
            } catch (Throwable e) {
                throw FitException.wrap(e, fitable.genericable().id(), fitable.id());
            }
        } while (retry > 0);
        actualException.associateFitable(fitable.genericable().id(), fitable.id());
        throw actualException;
    }
}
