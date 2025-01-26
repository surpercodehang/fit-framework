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
 * 用于 FIT 泛服务的定义。
 *
 * @author 张群辉
 * @author 季聿阶
 * @since 2020-01-18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Genericable {
    /**
     * 获取泛服务定义的唯一标识。
     *
     * @return 表示该泛服务定义的唯一标识的 {@link String}。
     * @see #id()
     */
    @Forward(annotation = Genericable.class, property = "id") String value() default StringUtils.EMPTY;

    /**
     * 获取泛服务定义的唯一标识。
     *
     * @return 表示该泛服务定义的唯一标识的 {@link String}。
     */
    String id() default StringUtils.EMPTY;

    /**
     * 获取泛服务定义的描述。
     *
     * @return 表示泛服务定义的描述的 {@link String}。
     */
    String description() default StringUtils.EMPTY;
}
