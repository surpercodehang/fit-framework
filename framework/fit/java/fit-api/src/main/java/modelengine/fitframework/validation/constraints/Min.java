/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.constraints;

import modelengine.fitframework.validation.Validated;
import modelengine.fitframework.validation.validators.MinValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示校验元素是否满足下限值约束的注解。
 *
 * @author 兰宇晨
 * @since 2024-08-28
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(MinValidator.class)
@Validated
public @interface Min {
    /**
     * 表示校验元素的下限值。
     *
     * @return 表示校验元素的下限值的 {@code long}。
     */
    long min();

    /**
     * 表示校验失败的信息。
     *
     * @return 表示校验失败的信息的 {@link String}。
     */
    String message() default "must be greater than the minimum value.";

    /**
     * 表示校验的分组。
     *
     * @return 表示校验分组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] groups() default {};
}
