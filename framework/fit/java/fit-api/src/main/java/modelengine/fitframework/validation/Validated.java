/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation;

import modelengine.fitframework.validation.constraints.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示校验的注解。
 * <p>用来标识所要校验的类和参数。若校验的是类，则该类方法的所有带有 {@link Constraint}
 * 参数会被校验；若校验的是参数，则校验该参数对象所有的带有 {@link Constraint} 字段。
 * </p>
 *
 * @author 邬涨财
 * @since 2023-03-14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD})
public @interface Validated {
    /**
     * 表示校验的分组。
     *
     * @return 表示校验分组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] value() default {};
}
