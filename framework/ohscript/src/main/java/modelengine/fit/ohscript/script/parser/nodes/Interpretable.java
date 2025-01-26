/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;

/**
 * 可解释执行的
 *
 * @since 1.0
 */
public interface Interpretable {
    /**
     * 解释执行
     *
     * @param env 抽象语法树环境
     * @param context 激活上下文
     * @return 返回值
     * @throws OhPanic 抛出OhPanic异常
     */
    ReturnValue interpret(ASTEnv env, ActivationContext context) throws OhPanic;
}
