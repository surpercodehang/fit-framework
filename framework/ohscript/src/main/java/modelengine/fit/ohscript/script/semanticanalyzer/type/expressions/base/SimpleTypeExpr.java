/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * 简单类型的表达式：string,number,unit,unknown
 *
 * @since 1.0
 */
public abstract class SimpleTypeExpr extends ConcreteTypeExpr {
    public SimpleTypeExpr(SyntaxNode node) {
        super("simple", node);
    }

    /**
     * 判断当前类型表达式是否是给定的类型表达式或者可能的类型表达式之一
     *
     * @param expr 给定的类型表达式
     * @return 如果当前类型表达式是给定的类型表达式或者可能的类型表达式之一，返回true，否则返回false
     */
    public boolean is(TypeExpr expr) {
        if (super.is(expr)) {
            return true;
        }
        for (TypeExpr typeExpr : this.couldBe()) {
            if (typeExpr.key().equals(expr.key())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String key() {
        return this.type().id().toString();
    }
}
