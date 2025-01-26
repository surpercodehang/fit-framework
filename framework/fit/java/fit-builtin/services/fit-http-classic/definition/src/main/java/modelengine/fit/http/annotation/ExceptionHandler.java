/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.annotation;

import modelengine.fitframework.annotation.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示 REST 请求的异常处理器。
 * <p>该注解仅在公开（public）方法下才会生效。</p>
 *
 * @author 季聿阶
 * @since 2023-01-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ExceptionHandler {
    /**
     * 获取处理异常的类型列表。
     *
     * @return 表示处理异常类型列表的 {@link Class}{@code ? extends }{@link Throwable}{@code []}。
     */
    Class<? extends Throwable>[] value() default {};

    /**
     * 获取异常处理器的生效范围。
     *
     * @return 表示异常处理器的生效范围的 {@link Scope}。
     */
    Scope scope() default Scope.PLUGIN;
}
