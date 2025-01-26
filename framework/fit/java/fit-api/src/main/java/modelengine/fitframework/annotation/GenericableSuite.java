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
 * 用于 FIT {@link Genericable 泛服务} 的集合。
 *
 * @author 季聿阶
 * @since 2023-02-22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GenericableSuite {
    /**
     * 获取泛服务集合的唯一标识。
     *
     * @return 表示该泛服务集合的唯一标识的 {@link String}。
     * @see #id()
     */
    @Forward(annotation = GenericableSuite.class, property = "id") String value() default "";

    /**
     * 获取泛服务集合的唯一标识。
     *
     * @return 表示该泛服务集合的唯一标识的 {@link String}。
     */
    String id() default "";
}
