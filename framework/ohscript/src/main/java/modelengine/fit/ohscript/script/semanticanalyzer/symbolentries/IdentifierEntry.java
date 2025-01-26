/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Category;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UnknownTypeExpr;

/**
 * 标识符条目
 *
 * @since 1.0
 */
public class IdentifierEntry extends KnownSymbolEntry {
    private final boolean mutable;

    private final SyntaxNode host;

    /**
     * 构造函数
     *
     * @param node 标识符的语法节点
     * @param scope 标识符的作用域
     * @param mutable 标识符是否可变
     * @param host 标识符所在的语法节点
     */
    public IdentifierEntry(TerminalNode node, long scope, boolean mutable, SyntaxNode host) {
        super(node, scope, Category.VARIABLE, TypeExprFactory.createUnknown());
        this.mutable = mutable;
        this.host = host;
    }

    /**
     * 获取标识符是否可变
     *
     * @return 标识符是否可变
     */
    public boolean mutable() {
        return this.mutable;
    }

    /**
     * 设置标识符的类型表达式
     *
     * @param expr 类型表达式
     */
    public void setTypeExpr(TypeExpr expr) {
        if (!(this.typeExpr instanceof UnknownTypeExpr)) {
            return;
        }
        this.typeExpr = expr;
        if (host == null) {
            return;
        }
        host.declaredName().symbolEntry().typeExpr().myMembers().put(this.node().lexeme(), expr);
    }
}
