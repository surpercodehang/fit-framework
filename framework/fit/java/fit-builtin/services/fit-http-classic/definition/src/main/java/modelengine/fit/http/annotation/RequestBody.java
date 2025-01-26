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
 * 表示 REST 接口的请求映射中的消息体参数。
 *
 * @author 季聿阶
 * @since 2023-01-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@RequestParam(in = Source.BODY)
public @interface RequestBody {
    /**
     * 获取消息体参数的键值的 {@link String}。
     *
     * @return 表示消息体中需要获取的参数的键值的 {@link String}。
     * @see #key()
     */
    @Forward(annotation = RequestBody.class, property = "key") String value() default StringUtils.EMPTY;

    /**
     * 获取消息体参数的键值的 {@link String}。
     *
     * @return 表示消息体中需要获取的参数的键值的 {@link String}。
     */
    @Forward(annotation = RequestParam.class, property = "name") String key() default StringUtils.EMPTY;

    /**
     * 获取消息体参数是否为必须的标志。
     *
     * @return 如果消息体参数必须存在，则返回 {@code true}，否则，返回 {@code false}。
     */
    @Forward(annotation = RequestParam.class, property = "required") boolean required() default true;
}
