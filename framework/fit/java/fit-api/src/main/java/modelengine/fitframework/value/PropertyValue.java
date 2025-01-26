/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.value;

import modelengine.fitframework.value.support.FieldValue;
import modelengine.fitframework.value.support.ParameterValue;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 表示属性值。
 *
 * @author 邬涨财
 * @since 2023-11-14
 */
public interface PropertyValue {
    /**
     * 获取属性值的类型。
     *
     * @return 表示属性值的类型的 {@link Class}{@code <?>}。
     */
    Class<?> getType();

    /**
     * 获取属性值的参数化类型。
     *
     * @return 表示属性值的参数化类型的 {@link Type}。
     */
    Type getParameterizedType();

    /**
     * 获取属性值的具体元素。
     *
     * @return 表示属性值的具体元素的 {@link Optional}{@code <}{@link AnnotatedElement}{@code >}。
     */
    Optional<AnnotatedElement> getElement();

    /**
     * 获取属性值的值名。
     *
     * @return 表示属性值名的 {@link String}。
     */
    String getName();

    /**
     * 创建一个参数类型的属性值。
     *
     * @param parameter 表示需要创建的参数的 {@link Parameter}。
     * @return 表示创建后的属性值的 {@link PropertyValue}。
     * @throws IllegalArgumentException 当 {@code parameter} 为 {@code null} 时。
     */
    static PropertyValue createParameterValue(Parameter parameter) {
        return new ParameterValue(parameter);
    }

    /**
     * 创建一个字段类型的属性值。
     *
     * @param field 表示需要创建的字段的 {@link Field}。
     * @return 表示创建后的属性值的 {@link PropertyValue}。
     * @throws IllegalArgumentException 当 {@code field} 为 {@code null} 时。
     */
    static PropertyValue createFieldValue(Field field) {
        return new FieldValue(field);
    }
}
