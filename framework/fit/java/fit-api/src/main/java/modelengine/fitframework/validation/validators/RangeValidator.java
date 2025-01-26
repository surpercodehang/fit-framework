/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.validators;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.validation.ConstraintValidator;
import modelengine.fitframework.validation.constraints.Range;

/**
 * 表示 {@link Range} 约束的校验器。
 *
 * @author 邬涨财
 * @since 2023-03-08
 */
public class RangeValidator implements ConstraintValidator<Range, Object> {
    private long min;
    private long max;

    @Override
    public void initialize(Range constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof Integer) {
            int convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min && convertedValue <= this.max;
        } else if (value instanceof Long) {
            long convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min && convertedValue <= this.max;
        } else if (value instanceof Float) {
            float convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min && convertedValue <= this.max;
        } else if (value instanceof Double) {
            double convertedValue = ObjectUtils.cast(value);
            return convertedValue >= this.min && convertedValue <= this.max;
        } else {
            throw new UnsupportedOperationException("Failed to validate value: invalid value.");
        }
    }

    @Override
    public Object[] args() {
        return new Object[] {this.min, this.max};
    }
}
