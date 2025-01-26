/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.retry.condition;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.retry.Condition;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表示捕获异常的重试条件。
 *
 * @author 邬涨财
 * @since 2023-02-25
 */
public class ExceptionCondition implements Condition {
    private final List<Class<? extends Throwable>> capturedExceptions;

    public ExceptionCondition() {
        this(new ArrayList<>());
    }

    public ExceptionCondition(List<Class<? extends Throwable>> capturedExceptions) {
        this.capturedExceptions = Validation.notNull(capturedExceptions, "Captured exceptions can not be null");
    }

    @Override
    public boolean matches(int attemptTimes, long executionTimeMillis, Throwable cause) {
        return CollectionUtils.isEmpty(this.capturedExceptions) || this.capturedExceptions.stream()
                .filter(Objects::nonNull)
                .anyMatch(throwable -> throwable.isInstance(cause));
    }
}
