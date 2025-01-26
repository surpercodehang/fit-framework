/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation.constraints;

import modelengine.fitframework.validation.ConstraintValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示约束的注解。
 *
 * @author 邬涨财
 * @since 2023-03-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Constraint {
    /**
     * 表示约束所对应的校验器的类型。
     *
     * @return 表示校验器的类型的 {@link Class}{@code <? extends }{@link ConstraintValidator}{@code <?, ?>>[]}。
     */
    Class<? extends ConstraintValidator<?, ?>>[] value();
}
