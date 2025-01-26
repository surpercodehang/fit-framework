/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.annotation;

import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.annotation.Forward;
import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示 REST 接口的请求映射。
 *
 * @author 季聿阶
 * @since 2023-01-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequestMapping {
    /**
     * 获取请求映射的路径列表信息。
     *
     * @return 表示请求映射的路径列表信息的 {@link String}{@code []}。
     * @see #path()
     */
    @Forward(annotation = RequestMapping.class, property = "path") String[] value() default {};

    /**
     * 获取请求映射的路径列表信息。
     *
     * @return 表示请求映射的路径列表信息的 {@link String}{@code []}。
     */
    String[] path() default {};

    /**
     * 获取请求映射的方法列表信息。
     *
     * @return 表示请求映射的方法列表信息的 {@link HttpRequestMethod}{@code []}。
     */
    HttpRequestMethod[] method() default {};

    /**
     * 获取请求映射的分组信息。
     *
     * @return 表示请求映射的分组信息的 {@link String}。
     */
    String group() default StringUtils.EMPTY;

    /**
     * 获取请求映射的简短摘要。
     *
     * @return 表示请求映射的简短摘要的 {@link String}。
     */
    String summary() default StringUtils.EMPTY;

    /**
     * 获取请求映射的描述信息。
     *
     * @return 表示请求映射的描述信息的 {@link String}。
     */
    String description() default StringUtils.EMPTY;

    /**
     * 获取请求映射的返回值的描述信息。
     *
     * @return 表示请求映射的返回值的描述信息的 {@link String}。
     */
    String returnDescription() default StringUtils.EMPTY;
}
