/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示顺序的注解。
 * <p>该注解提供了一组通用的优先级顺序，其中优先级之间都存在一定的空间，供用户进行自定义扩展。</p>
 *
 * @author 张浩亮
 * @author 季聿阶
 * @since 2021-08-21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Order {
    /** 预先定义的顺序，表示最低的优先级。 */
    int LOWEST = Integer.MAX_VALUE;

    /** 预先定义的顺序，表示非常低的优先级。 */
    int PRETTY_LOW = 10000;

    /** 预先定义的顺序，表示低的优先级。 */
    int LOW = 1000;

    /** 预先定义的顺序，表示比较低的优先级。 */
    int NEARLY_LOW = 100;

    /** 预先定义的顺序，表示一般的优先级。 */
    int MEDIUM = 0;

    /** 预先定义的顺序，表示比较高的优先级。 */
    int NEARLY_HIGH = -NEARLY_LOW;

    /** 预先定义的顺序，表示高的优先级。 */
    int HIGH = -LOW;

    /** 预先定义的顺序，表示非常高的优先级。 */
    int PRETTY_HIGH = -PRETTY_LOW;

    /** 预先定义的顺序，表示最高的优先级。 */
    int HIGHEST = Integer.MIN_VALUE;

    /**
     * 表示顺序的数字，数字越小优先级越高。
     * <p>默认顺序为 {@link #MEDIUM}。</p>
     *
     * @return 表示顺序的数字的 {@code int}。
     */
    int value() default MEDIUM;
}
