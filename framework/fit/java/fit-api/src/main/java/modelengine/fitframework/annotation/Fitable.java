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
 * 用于 FIT {@link Genericable 泛服务} 的实现。
 * <p>对于泛服务的实现获取对应的泛服务的唯一标识的优先级如下：（获取的唯一标识不为空白字符串）
 * <ol>
 *     <li>通过 {@link #genericable()} 方法获取一个唯一标识；</li>
 *     <li>通过方法所实现的接口方法上的 {@link Genericable#id()} 获取一个唯一标识；</li>
 * </ol>
 * </p>
 *
 * @author 张群辉
 * @author 季聿阶
 * @see Genericable
 * @since 2020-01-18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Fitable {
    /**
     * 获取 FIT 泛服务实现对应的泛服务的唯一标识。
     *
     * @return 表示 FIT 泛服务实现对应的泛服务的唯一标识的 {@link String}。
     */
    String genericable() default StringUtils.EMPTY;

    /**
     * 获取 FIT 泛服务实现的唯一标识。
     *
     * @return 表示泛服务实现的唯一标识的 {@link String}。
     * @see #id()
     */
    @Forward(annotation = Fitable.class, property = "id") String value() default StringUtils.EMPTY;

    /**
     * 获取 FIT 泛服务实现的唯一标识。
     *
     * @return 表示泛服务实现的唯一标识的 {@link String}。
     */
    String id() default StringUtils.EMPTY;
}
