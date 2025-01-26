/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 返回值节点
 *
 * @since 1.0
 */
public class ReturnNode extends NonTerminalNode {
    /**
     * 构造函数
     * 创建一个新的返回值节点
     */
    public ReturnNode() {
        super(NonTerminal.RETURN_STATEMENT);
        this.returnAble = true;
    }

    @Override
    public void semanticCheck() {
        int index = this.parent().children().indexOf(this);
        if (index < this.parent().childCount() - 1) {
            this.parent().child(index + 1).panic(SyntaxError.UN_REACHABLE);
        }
        super.semanticCheck();
    }

    @Override
    public void optimizeAlpha() {
        if (this.child(0) instanceof IgnoredNode) {
            this.refreshChildren(this.child(0).children());
        }
    }

    @Override
    public void optimizeBeta() {
    }

    @Override
    public TypeExpr typeExpr() {
        if (this.child(0) instanceof TerminalNode && ((TerminalNode) this.child(0)).nodeType() == Terminal.RETURN) {
            return this.child(1).typeExpr();
        } else {
            return this.child(0).typeExpr();
        }
    }
}
