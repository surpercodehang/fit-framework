/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.validators;

import modelengine.fitframework.util.ArrayUtils;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.validation.ConstraintValidator;
import modelengine.fitframework.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * 表示 {@link NotEmpty} 约束的校验器。
 *
 * @author 邬涨财
 * @since 2023-03-08
 */
public class NotEmptyValidator implements ConstraintValidator<NotEmpty, Object> {
    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof List) {
            return CollectionUtils.isNotEmpty(ObjectUtils.cast(value));
        } else if (value instanceof Map) {
            return MapUtils.isNotEmpty(ObjectUtils.cast(value));
        } else if (value instanceof String) {
            return StringUtils.isNotEmpty(ObjectUtils.cast(value));
        } else if (value instanceof Object[]) {
            return ArrayUtils.isNotEmpty(ObjectUtils.cast(value));
        } else {
            throw new UnsupportedOperationException("Failed to validate value: invalid value.");
        }
    }
}
