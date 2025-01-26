/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.validators;

import modelengine.fitframework.validation.ConstraintValidator;
import modelengine.fitframework.validation.constraints.Positive;

/**
 * 表示 {@link Positive} 约束的校验器。
 *
 * @author 吕博文
 * @since 2024-07-29
 */
public class PositiveValidator implements ConstraintValidator<Positive, Number> {
    @Override
    public boolean isValid(Number value) {
        if (value == null) {
            return false;
        }
        return value.doubleValue() > 0;
    }
}
