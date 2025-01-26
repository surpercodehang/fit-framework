/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.value.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.value.PropertyValue;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 表示字段类型的属性值。
 *
 * @author 邬涨财
 * @since 2023-11-14
 */
public class FieldValue implements PropertyValue {
    private final Field field;

    public FieldValue(Field field) {
        this.field = notNull(field, "The field cannot be null.");
    }

    @Override
    public Class<?> getType() {
        return this.field.getType();
    }

    @Override
    public Type getParameterizedType() {
        return this.field.getGenericType();
    }

    @Override
    public Optional<AnnotatedElement> getElement() {
        return Optional.of(this.field);
    }

    @Override
    public String getName() {
        return this.field.getName();
    }
}
