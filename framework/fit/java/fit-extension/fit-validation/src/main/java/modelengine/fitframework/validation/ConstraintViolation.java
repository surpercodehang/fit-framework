/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation;

import modelengine.fitframework.pattern.builder.BuilderFactory;

import java.lang.reflect.Method;

/**
 * 表示约束校验失败的数据类。
 *
 * @author 邬涨财
 * @since 2023-03-08
 */
public interface ConstraintViolation {
    /**
     * 获取校验失败的信息。
     *
     * @return 表示校验失败的信息的 {@link String}。
     */
    String message();

    /**
     * 获取校验的属性名。
     *
     * @return 表示校验的属性名的 {@link String}。
     */
    String propertyName();

    /**
     * 获取校验的属性值。
     *
     * @return 表示校验的属性值的 {@link Object}。
     */
    Object propertyValue();

    /**
     * 获取校验的方法。
     *
     * @return 表示校验的方法的 {@link Method}。
     */
    Method validationMethod();

    /**
     * 获取校验注解的参数。
     *
     * @return 表示校验注解的参数的 {@link Object}{@code []}。
     */
    Object[] args();

    /**
     * {@link ConstraintViolation} 的构建器。
     */
    interface Builder {
        /**
         * 向构建器设置校验失败的信息。
         *
         * @param message 表示校验失败的信息的 {@link String}。
         * @return 表示构建器的 {@link Builder}。
         */
        Builder message(String message);

        /**
         * 向构建器设置校验的属性名。
         *
         * @param propertyName 表示校验的属性名的 {@link String}。
         * @return 表示构建器的 {@link Builder}。
         */
        Builder propertyName(String propertyName);

        /**
         * 向构建器设置校验的属性值。
         *
         * @param propertyValue 表示校验的属性值的 {@link String}。
         * @return 表示构建器的 {@link Builder}。
         */
        Builder propertyValue(Object propertyValue);

        /**
         * 向构建器设置校验的方法。
         *
         * @param validationMethod 表示校验的方法的 {@link String}。
         * @return 表示构建器的 {@link Builder}。
         */
        Builder validationMethod(Method validationMethod);

        /**
         * 向构建器设置校验注解的参数。
         *
         * @param args 表示校验注解的参数的 {@link Object}{@code []}。
         * @return 表示构建器的 {@link Builder}。
         */
        Builder args(Object args);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link ConstraintViolation}。
         */
        ConstraintViolation build();
    }

    /**
     * 获取 {@link ConstraintViolation} 的构建器。
     *
     * @return 表示 {@link ConstraintViolation} 的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(null);
    }

    /**
     * 获取 {@link ConstraintViolation} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link ConstraintViolation}。
     * @return 表示 {@link ConstraintViolation} 的构建器的 {@link Builder}。
     */
    static Builder builder(ConstraintViolation value) {
        return BuilderFactory.get(ConstraintViolation.class, Builder.class).create(value);
    }
}
