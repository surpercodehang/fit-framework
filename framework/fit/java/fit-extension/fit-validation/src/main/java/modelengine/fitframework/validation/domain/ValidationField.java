/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.validation.domain;

import modelengine.fitframework.inspection.Validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 校验类型为字段的元数据类。
 *
 * @author 邬涨财
 * @since 2023-05-18
 */
public class ValidationField extends AbstractValidationMetadata {
    private final Field field;

    /**
     * 使用指定的字段、分组、值和验证方法初始化 {@link ValidationField} 的新实例。
     *
     * @param field 表示字段的 {@link Field}。
     * @param groups 表示分组的 {@link Class}{@code <?>[]}。
     * @param value 表示值的 {@link Object}。
     * @param validationMethod 表示验证方法的 {@link Method}。
     * @throws IllegalArgumentException 当 {@code field}、{@code groups} 或 {@code validationMethod} 为 {@code null} 时。
     */
    public ValidationField(Field field, Class<?>[] groups, Object value, Method validationMethod) {
        super(groups, value, validationMethod);
        this.field = Validation.notNull(field, "The field cannot be null when construct validation filed.");
    }

    @Override
    public AnnotatedElement element() {
        return this.field;
    }

    @Override
    public String name() {
        return this.field.getName();
    }

    @Override
    public Annotation[] annotations() {
        return this.field.getAnnotations();
    }
}
