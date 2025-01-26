/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.convert;

import modelengine.fitframework.annotation.Forward;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示直接使用的注解，将转发到原始注解 {@link IntValue}，在转发过程将对值进行类型转换。
 *
 * @author 梁济时
 * @since 2023-01-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@IntValue
public @interface StringValue {
    /**
     * 表示字符串类型的值，将转发到 {@link IntValue#value()}。
     *
     * @return 表示注解的值的 {@link String}。
     */
    @Forward(annotation = IntValue.class, converter = AnnotationValueConverter.class)
    String value() default "0";
}
