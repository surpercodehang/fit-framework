/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;

import java.util.List;

/**
 * oh的方法调用的函数式接口
 *
 * @param <V> 待执行方法的对象
 * @param <R> 返回值类型
 * @since 1.0
 */
@FunctionalInterface
public interface OhFunction<V, R> {
    /**
     * 执行方法
     *
     * @param host 待执行方法的对象
     * @param args 方法参数
     * @param env 执行环境
     * @param current 激活上下文
     * @return 方法执行结果
     * @throws RuntimeException 运行时异常
     * @throws OhPanic Oh语言抛出的异常
     */
    R apply(V host, List<ReturnValue> args, ASTEnv env, ActivationContext current) throws RuntimeException, OhPanic;
}
