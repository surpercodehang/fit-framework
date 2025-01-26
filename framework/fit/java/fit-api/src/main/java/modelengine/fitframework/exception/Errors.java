/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.exception;

import modelengine.fitframework.broker.client.FitableNotFoundException;
import modelengine.fitframework.broker.client.TargetNotFoundException;
import modelengine.fitframework.broker.client.TooManyFitablesException;
import modelengine.fitframework.serialization.SerializationException;

import java.util.Optional;

/**
 * 表示 FIT 编程框架系统内的异常集合。
 *
 * @author 季聿阶
 * @since 2023-06-16
 */
public enum Errors {
    /** 表示 FIT 的默认异常。 */
    DEFAULT(FitException.CODE, FitException.class),
    /** 表示可降级的异常。 */
    DEGRADABLE(DegradableException.CODE, DegradableException.class),
    /** 表示可重试的异常。 */
    RETRYABLE(RetryableException.CODE, RetryableException.class),
    /** 表示服务实现过多的异常。 */
    ROUTING_TOO_MANY_FITABLES(TooManyFitablesException.CODE, TooManyFitablesException.class),
    /** 表示没有服务实现的异常。 */
    ROUTING_FITABLE_NOT_FOUND(FitableNotFoundException.CODE, FitableNotFoundException.class),
    /** 表示没有服务地址的异常。 */
    LOADBALANCE_TARGET_NOT_FOUND(TargetNotFoundException.CODE, TargetNotFoundException.class),
    /** 表示序列化过程的异常。 */
    SERIALIZATION(0x7F040000, SerializationException.class);

    private final int code;
    private final Class<? extends FitException> clazz;

    Errors(int code, Class<? extends FitException> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    /**
     * 获取错误的异常码。
     *
     * @return 表示错误的异常码的 {@code int}。
     */
    public final int code() {
        return this.code;
    }

    /**
     * 获取错误的异常类型。
     *
     * @return 表示异常类型的 {@link Class}{@code <? extends }{@link FitException}{@code >}。
     */
    public final Class<? extends FitException> exceptionClass() {
        return this.clazz;
    }

    /**
     * 根据异常码获取异常的类型。
     *
     * @param code 表示异常码的 {@code int}。
     * @return 表示异常码对应的异常类型的 {@link Optional}{@code <}{@link Class}{@code <? extends }{@link
     * FitException}{@code >>}。
     */
    public static Optional<Class<? extends FitException>> exceptionClass(int code) {
        for (Errors error : Errors.values()) {
            if (error.code() == code) {
                return Optional.of(error.exceptionClass());
            }
        }
        return Optional.empty();
    }
}
