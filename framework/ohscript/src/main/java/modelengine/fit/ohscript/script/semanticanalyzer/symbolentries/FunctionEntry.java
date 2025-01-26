/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Category;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 函数条目
 *
 * @since 1.0
 */
public class FunctionEntry extends KnownSymbolEntry {
    private final FunctionDeclareNode function;

    /**
     * 构造函数
     *
     * @param node 节点
     * @param scope 作用域
     * @param typeExpr 类型表达式
     */
    public FunctionEntry(TerminalNode node, long scope, TypeExpr typeExpr) {
        super(node, scope, Category.FUNCTION_DECLARE, typeExpr);
        this.function = ObjectUtils.cast(node.parent());
    }

    /**
     * 获取函数声明节点
     *
     * @return 函数声明节点
     */
    public FunctionDeclareNode function() {
        return function;
    }
}
