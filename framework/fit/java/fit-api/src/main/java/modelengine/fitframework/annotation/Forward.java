/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

import modelengine.fitframework.util.convert.Converter;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 转发注解的值。
 *
 * @author 梁济时
 * @since 2022-06-15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Forward {
    /**
     * 指示转发到的注解类型。
     *
     * @return 表示转发到的注解类型的 {@link Class}{@code <}{@link Annotation}{@code >}。
     */
    Class<? extends Annotation> annotation() default Annotation.class;

    /**
     * 指示转发到的注解的属性。
     *
     * @return 表示注解属性名称的 {@link String}。
     */
    String property() default "";

    /**
     * 指示在转发过程应用的值转换程序的类型。
     * <p>需要存在默认构造方法，将通过默认构造方法对类型转换程序进行实例化。</p>
     *
     * @return 表示值转换程序的类型的 {@link Class}。
     */
    Class<? extends Converter> converter() default Converter.class;
}
