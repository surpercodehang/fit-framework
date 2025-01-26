/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示 Bean 仅当指定类型存在时生效。
 *
 * @author 梁济时
 * @since 2023-05-18
 */
@Conditional(IfClassExistCondition.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IfClassExist {
    /**
     * 表示 Bean 生效所需存在的所有类型的名称。
     *
     * @return 表示类型名称的 {@link String}{@code []}。
     */
    String[] value() default {};
}
