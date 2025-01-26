/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.annotation;

import modelengine.fit.http.server.handler.Source;
import modelengine.fitframework.annotation.Forward;
import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示 REST 接口的请求映射中的查询参数或表单参数。
 *
 * @author 季聿阶
 * @since 2023-01-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
public @interface RequestParam {
    /**
     * 获取查询参数或表单参数的名字。
     *
     * @return 表示查询参数或表单参数名字的 {@link String}。
     * @see #name()
     */
    @Forward(annotation = RequestParam.class, property = "name") String value() default StringUtils.EMPTY;

    /**
     * 获取查询参数或表单参数的名字。
     *
     * @return 表示查询参数或表单参数名字的 {@link String}。
     */
    String name() default StringUtils.EMPTY;

    /**
     * 获取查询参数或表单参数是否为必须的标志。
     *
     * @return 如果查询参数或表单参数必须存在，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean required() default true;

    /**
     * 获取查询参数或表单参数的默认值。
     *
     * @return 表示查询参数或表单参数的默认值的 {@link String}。
     */
    String defaultValue() default DefaultValue.VALUE;

    /**
     * 获取参数的位置。
     *
     * @return 表示参数位置的 {@link Source}。
     */
    Source in() default Source.QUERY;
}
