/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示别名信息。
 * <p>为 {@link Fitable} 作用于类型上的实现提供别名定义，或是其他场景也可以复用此注解。</p>
 *
 * @author 梁济时
 * @author 季聿阶
 * @see Stereotype
 * @see Fitable
 * @since 2020-12-14
 */
@Documented
@Repeatable(Aliases.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Alias {
    /**
     * 获取别名。
     *
     * @return 表示别名的 {@link String}。
     */
    String value() default StringUtils.EMPTY;
}
