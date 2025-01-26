/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.value.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.value.PropertyValue;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 表示参数类型的属性值。
 *
 * @author 邬涨财
 * @since 2023-11-14
 */
public class ParameterValue implements PropertyValue {
    private final Parameter parameter;

    public ParameterValue(Parameter parameter) {
        this.parameter = notNull(parameter, "The parameter cannot be null.");
    }

    @Override
    public Class<?> getType() {
        return this.parameter.getType();
    }

    @Override
    public Type getParameterizedType() {
        return this.parameter.getParameterizedType();
    }

    @Override
    public Optional<AnnotatedElement> getElement() {
        return Optional.of(this.parameter);
    }

    @Override
    public String getName() {
        return this.parameter.getName();
    }
}
