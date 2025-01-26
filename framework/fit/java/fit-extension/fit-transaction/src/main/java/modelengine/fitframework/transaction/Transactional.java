/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction;

import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示指定的方法需要在一个事务中执行。
 *
 * @author 梁济时
 * @since 2022-08-29
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    /**
     * 指示事务的名称。
     *
     * @return 表示事务名称的 {@link String}。
     */
    String name() default StringUtils.EMPTY;

    /**
     * 指示事务的传播策略。
     *
     * @return 表示事务传播策略的 {@link TransactionPropagationPolicy}。
     */
    TransactionPropagationPolicy propagation() default TransactionPropagationPolicy.REQUIRED;

    /**
     * 指示事务的隔离级别。
     *
     * @return 表示事务隔离级别的 {@link TransactionIsolationLevel}。
     */
    TransactionIsolationLevel isolation() default TransactionIsolationLevel.READ_COMMITTED;

    /**
     * 指示事务的以秒为单位的超时时间。
     *
     * @return 表示超时时间的秒数的32位整数。
     */
    int timeout() default 0;

    /**
     * 指示事务是否是只读的。
     *
     * @return 若为 {@code true}，则事务是只读的，否则不是只读的。
     */
    boolean readonly() default false;
}
