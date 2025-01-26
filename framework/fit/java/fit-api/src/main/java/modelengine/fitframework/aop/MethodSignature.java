/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop;

import java.lang.reflect.Method;

/**
 * 方法签名接口。
 *
 * @author 白鹏坤
 * @since 2023-04-12
 */
public interface MethodSignature extends CodeSignature {
    /**
     * 获取方法的返回类型。
     *
     * @return 方法的返回类型的 {@link Class}。
     */
    Class<?> getReturnType();

    /**
     * 获取方法定义。
     *
     * @return 表示方法定义的 {@link Method}。
     */
    Method getMethod();
}
