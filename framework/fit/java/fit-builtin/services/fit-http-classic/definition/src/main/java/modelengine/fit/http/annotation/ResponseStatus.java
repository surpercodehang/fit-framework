/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.annotation;

import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.annotation.Forward;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示 REST 接口的响应映射中的状态码。
 *
 * @author 季聿阶
 * @since 2023-01-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ResponseStatus {
    /**
     * 获取响应状态码。
     *
     * @return 表示响应状态码的 {@link HttpResponseStatus}。
     * @see #code()
     */
    @Forward(annotation = ResponseStatus.class,
            property = "code") HttpResponseStatus value() default HttpResponseStatus.OK;

    /**
     * 获取响应状态码。
     *
     * @return 表示响应状态码的 {@link HttpResponseStatus}。
     */
    HttpResponseStatus code() default HttpResponseStatus.OK;
}
