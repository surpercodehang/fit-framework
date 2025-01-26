/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于表示 Bean。
 *
 * @author 季聿阶
 * @since 2023-02-22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Alias
public @interface Component {
    /**
     * 获取 Bean 的名字。
     *
     * @return 表示 Bean 名字的 {@link String}。
     * @see #name()
     */
    @Forward(annotation = Component.class, property = "name") String value() default StringUtils.EMPTY;

    /**
     * 获取 Bean 的名字。
     *
     * @return 表示 Bean 名字的 {@link String}。
     * @see Alias
     */
    @Forward(annotation = Alias.class, property = "value") String name() default StringUtils.EMPTY;
}
