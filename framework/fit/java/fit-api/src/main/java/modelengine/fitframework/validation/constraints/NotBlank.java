/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.constraints;

import modelengine.fitframework.validation.Validated;
import modelengine.fitframework.validation.validators.NotBlankValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示校验元素是否为空白的注解。
 *
 * @author 邬涨财
 * @since 2023-03-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(NotBlankValidator.class)
@Validated
public @interface NotBlank {
    /**
     * 表示校验失败的信息。
     *
     * @return 表示校验失败的信息的 {@link String}。
     */
    String message() default "must not be blank";

    /**
     * 表示校验的分组。
     *
     * @return 表示校验分组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] groups() default {};
}
