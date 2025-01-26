/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.validators;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.validation.ConstraintValidator;
import modelengine.fitframework.validation.constraints.Min;

/**
 * 表示 {@link Min} 约束的校验器。
 *
 * @author 兰宇晨
 * @since 2024-08-28
 */
public class MinValidator implements ConstraintValidator<Min, Object> {
    private long min;

    @Override
    public void initialize(Min constraintAnnotation) {
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof Integer) {
            int convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min;
        } else if (value instanceof Long) {
            long convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min;
        } else if (value instanceof Float) {
            float convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min;
        } else if (value instanceof Double) {
            double convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min;
        } else {
            throw new UnsupportedOperationException("Failed to validate value: invalid value.");
        }
    }

    @Override
    public Object[] args() {
        return new Object[] {this.min};
    }
}