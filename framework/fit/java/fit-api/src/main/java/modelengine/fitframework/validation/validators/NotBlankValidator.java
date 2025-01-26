/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.validators;

import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.validation.ConstraintValidator;
import modelengine.fitframework.validation.constraints.NotBlank;

/**
 * 表示 {@link NotBlank} 约束的校验器。
 *
 * @author 邬涨财
 * @since 2023-03-08
 */
public class NotBlankValidator implements ConstraintValidator<NotBlank, String> {
    @Override
    public boolean isValid(String value) {
        return StringUtils.isNotBlank(value);
    }
}
