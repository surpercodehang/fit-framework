/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.parser.nodes.DoubleFunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.FunctionCallNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 双重方法调用类型
 *
 * @since 1.0
 */
public abstract class DoubleFunctionTypeExpr extends FunctionTypeExpr {
    /**
     * 构造函数
     *
     * @param function 双重方法声明节点
     * @param argumentType 参数类型
     * @param returnType 返回类型
     */
    public DoubleFunctionTypeExpr(DoubleFunctionDeclareNode function, TypeExpr argumentType, TypeExpr returnType) {
        super(function, argumentType, returnType);
    }

    /**
     * 投影函数
     *
     * @param function 函数调用节点
     * @return 返回函数类型
     */
    public abstract FunctionTypeExpr project(FunctionCallNode function);
}
