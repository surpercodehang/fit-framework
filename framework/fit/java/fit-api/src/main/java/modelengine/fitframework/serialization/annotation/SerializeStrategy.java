/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示序列化器策略。
 *
 * @author 易文渊
 * @since 2024-8-17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SerializeStrategy {
    /**
     * 获取序列化器的策略。
     *
     * @return 表示序列化器策略的 {@link Include}。
     */
    Include include() default Include.DEFAULT;

    /**
     * 表示序列化器包含字段的策略枚举，用于指示 Java 序列化时需要包含哪些属性。
     */
    enum Include {
        /**
         * {@code null} 值不会被序列化。
         */
        NON_NULL,

        /**
         * {@code null}、集合数组等没有内容、空字符串等，都不会被序列化。
         */
        NON_EMPTY,

        /**
         * 所有字段都会被序列化。
         */
        DEFAULT
    }
}
