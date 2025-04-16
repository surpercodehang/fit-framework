/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * 复杂类型表达式
 *
 * @since 1.0
 */
public abstract class ComplexTypeExpr extends ConcreteTypeExpr {
    /**
     * 通过语法树节点构造 {@link ComplexTypeExpr} 的新实例。
     *
     * @param node 表示语法树节点的 {@link SyntaxNode}。
     */
    public ComplexTypeExpr(SyntaxNode node) {
        super(node);
    }
}
