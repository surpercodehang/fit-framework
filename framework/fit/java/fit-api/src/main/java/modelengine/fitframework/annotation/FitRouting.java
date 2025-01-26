/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于 Fit 动态路由的实现。
 *
 * @author 季聿阶
 * @since 2020-04-17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface FitRouting {
    /**
     * Fit 泛服务实现对应的泛服务类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * <p>用于索引，快速匹配动态路由实现与对应的泛服务定义。</p>
     *
     * @return 表示该动态路由实现对应的泛服务定义的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     */
    Class<?> generic();

    /**
     * 动态路由的实现的唯一标识。
     *
     * @return 标识动态路由实现的唯一标识的 {@link String}。
     */
    String id() default "";
}
