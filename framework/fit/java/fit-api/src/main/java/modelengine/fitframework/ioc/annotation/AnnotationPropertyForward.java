/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation;

import modelengine.fitframework.util.convert.Converter;

/**
 * 为注解的转发提供定义。
 *
 * @author 梁济时
 * @since 2023-01-28
 */
public interface AnnotationPropertyForward {
    /**
     * 获取转发到的注解的属性。
     *
     * @return 表示转发到的注解属性的 {@link AnnotationProperty}。
     */
    AnnotationProperty target();

    /**
     * 获取转发注解时应用的值转换程序的类型。
     *
     * @return 表示注解值的转换程序的类型的 {@link Class}。
     */
    Class<? extends Converter> converterClass();
}
