/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * provides base class for concrete type expressions.
 * 基本类型表达式
 *
 * @author 张群辉
 * @since 1.0
 */
public abstract class ConcreteTypeExpr extends TypeExpr {
    /**
     * 通过语法节点初始化 {@link ConcreteTypeExpr} 的新实例。
     *
     * @param node 表示语法节点的 {@link SyntaxNode}。
     */
    public ConcreteTypeExpr(SyntaxNode node) {
        super(node);
    }

    /**
     * 通过键和语法节点初始化 {@link ConcreteTypeExpr} 的新实例。
     *
     * @param key 表示键的 {@link String}。
     * @param node 表示语法节点的 {@link SyntaxNode}。
     */
    public ConcreteTypeExpr(String key, SyntaxNode node) {
        super(key, node);
    }
}
