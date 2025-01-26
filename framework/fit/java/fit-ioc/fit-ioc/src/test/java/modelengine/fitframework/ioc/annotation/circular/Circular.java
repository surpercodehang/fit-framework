/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.circular;

import modelengine.fitframework.annotation.Forward;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示循环转发的注解。
 *
 * @author 梁济时
 * @since 2022-05-31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Circular {
    /**
     * 表示循环转发的第一个值。
     *
     * @return 表示值的 {@link String}。
     */
    @Forward(annotation = Circular.class, property = "key2")
    String key1() default "";

    /**
     * 表示循环转发的第二个值。
     *
     * @return 表示值的 {@link String}。
     */
    @Forward(annotation = Circular.class, property = "key1")
    String key2() default "";
}
