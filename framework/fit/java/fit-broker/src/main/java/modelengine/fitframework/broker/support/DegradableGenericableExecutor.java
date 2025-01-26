/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.GenericableExecutor;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.exception.DegradableException;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ExceptionUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 表示 {@link GenericableExecutor} 的可降级的实现。
 *
 * @author 季聿阶
 * @since 2023-03-27
 */
public class DegradableGenericableExecutor extends AbstractUnicastGenericableExecutor {
    private static final Logger log = Logger.get(DegradableGenericableExecutor.class);

    private final GenericableExecutor executor;

    DegradableGenericableExecutor(GenericableExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected Object execute(Fitable fitable, InvocationContext context, Object[] args) {
        try {
            return this.executor.execute(Collections.singletonList(fitable), context, args);
        } catch (DegradableException e) {
            return this.performDegradation(e, fitable, context, args);
        } catch (MethodInvocationException e) {
            Throwable cause = ExceptionUtils.getActualCause(e);
            if (cause instanceof DegradableException) {
                return this.performDegradation(ObjectUtils.cast(cause), fitable, context, args);
            } else {
                throw FitException.wrap(cause, fitable.genericable().id(), fitable.id());
            }
        } catch (Throwable e) {
            throw FitException.wrap(e, fitable.genericable().id(), fitable.id());
        }
    }

    private Object performDegradation(DegradableException exception, Fitable fitable, InvocationContext context,
            Object[] args) {
        Set<UniqueFitableId> executedFitables = new HashSet<>();
        executedFitables.add(fitable.toUniqueId());
        Optional<Fitable> opCurrentFitable = this.getDegradationFitable(fitable);
        DegradableException actualException = exception;
        while (opCurrentFitable.isPresent()) {
            Fitable currentFitable = opCurrentFitable.get();
            if (executedFitables.contains(currentFitable.toUniqueId())) {
                log.warn("Circular degradation, exit. [id={}]", currentFitable.toUniqueId());
                break;
            }
            executedFitables.add(currentFitable.toUniqueId());
            try {
                log.debug("Prepare to execute degradation. [id={}]", currentFitable.toUniqueId());
                return this.executor.execute(Collections.singletonList(currentFitable), context, args);
            } catch (DegradableException e) {
                e.associateFitable(currentFitable.genericable().id(), currentFitable.id());
                actualException = e;
                opCurrentFitable = this.getDegradationFitable(currentFitable);
            } catch (MethodInvocationException e) {
                Throwable cause = ExceptionUtils.getActualCause(e);
                if (cause instanceof DegradableException) {
                    actualException = ObjectUtils.cast(cause);
                    actualException.associateFitable(currentFitable.genericable().id(), currentFitable.id());
                    opCurrentFitable = this.getDegradationFitable(currentFitable);
                } else {
                    throw FitException.wrap(cause, currentFitable.genericable().id(), currentFitable.id());
                }
            } catch (Throwable e) {
                throw FitException.wrap(e, currentFitable.genericable().id(), currentFitable.id());
            }
        }
        throw actualException;
    }

    private Optional<Fitable> getDegradationFitable(Fitable fitable) {
        for (Fitable target : fitable.genericable().fitables()) {
            if (Objects.equals(target.id(), fitable.degradationFitableId())) {
                return Optional.of(target);
            }
        }
        return Optional.empty();
    }
}
