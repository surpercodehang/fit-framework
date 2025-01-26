/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.constraints;

import modelengine.fitframework.validation.Validated;
import modelengine.fitframework.validation.validators.PositiveValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示判断元素值是否为正的注解。
 *
 * @author 吕博文
 * @since 2024-08-01
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(PositiveValidator.class)
@Validated
public @interface Positive {
    /**
     * 表示校验失败的信息。
     *
     * @return 表示校验失败的信息的 {@link String}。
     */
    String message() default "must be positive";

    /**
     * 表示校验的分组。
     *
     * @return 表示校验分组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] groups() default {};
}
