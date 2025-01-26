/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation;

import modelengine.fitframework.validation.domain.ValidationField;
import modelengine.fitframework.validation.domain.ValidationParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 表示校验的元数据。
 *
 * @author 白鹏坤
 * @author 邬涨财
 * @since 2023-04-23
 */
public interface ValidationMetadata {
    /**
     * 获取分组信息。
     *
     * @return 表示分组信息集合的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] groups();

    /**
     * 获取校验元素的属性值。
     *
     * @return 表示校验元素的属性值的 {@link Object}。
     */
    Object value();

    /**
     * 获取校验的方法。
     *
     * @return 表示校验方法的 {@link Method}。
     */
    Method getValidationMethod();

    /**
     * 获取校验的元素。
     *
     * @return 表示校验的元素 {@link AnnotatedElement}。
     */
    AnnotatedElement element();

    /**
     * 获取校验元素的属性名。
     *
     * @return 表示校验的元素的属性名的 {@link String}。
     */
    String name();

    /**
     * 获取校验元素的所有注解。
     *
     * @return 表示校验元素的所有注解的 {@link Annotation}{@code []}。
     */
    Annotation[] annotations();

    /**
     * 创建一个 {@link ValidationMetadata} 对象，表示需要校验的字段的元数据。
     *
     * @param field 表示需要校验的字段的 {@link Field}。
     * @param groups 表示需要校验的分组的 {@link Class}{@code <?>[]}。
     * @param value 表示字段值的 {@link Object}。
     * @param validationMethod 表示校验的方法的 {@link Method}.
     * @return 表示创建后的校验元数据的 {@link ValidationMetadata}。
     */
    static ValidationMetadata createValidationField(Field field, Class<?>[] groups, Object value,
            Method validationMethod) {
        return new ValidationField(field, groups, value, validationMethod);
    }

    /**
     * 创建一个 {@link ValidationMetadata} 对象，表示需要校验的参数的元数据。
     *
     * @param parameter 表示需要校验的参数的 {@link Parameter}。
     * @param groups 表示需要校验的分组的 {@link Class}{@code <?>[]}。
     * @param value 表示参数值的 {@link Object}。
     * @param validationMethod 表示校验的方法的 {@link Method}.
     * @return 表示创建后的校验元数据的 {@link ValidationMetadata}。
     */
    static ValidationMetadata createValidationParameter(Parameter parameter, Class<?>[] groups, Object value,
            Method validationMethod) {
        return new ValidationParameter(parameter, groups, value, validationMethod);
    }
}
