/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 声明表达式节点
 *
 * @since 1.0
 */
public class ExprStatementNode extends NonTerminalNode {
    public ExprStatementNode() {
        super(NonTerminal.EXPRESSION_STATEMENT);
    }

    private static boolean isStandalone(SyntaxNode node) {
        return false;
    }

    @Override
    public void optimizeBeta() {
    }

    @Override
    public void optimizeGama() {
        if (this.parent() == null) {
            return;
        }
        if (this.childCount() == 1 && this.child(0).nodeType() != Terminal.SEMICOLON) {
            if (!(this.child(0) instanceof FunctionDeclareNode) || ((FunctionDeclareNode) this.child(
                    0)).isAnonymous()) {
                if (!(this.parent() instanceof ForNode)) {
                    ReturnNode returnNode = new ReturnNode();
                    returnNode.refreshChildren(this.children());
                    this.parent().replaceChild(this, returnNode);
                    return;
                }
            }
        }
        SyntaxNode node = this.child(0);
        if (isStandalone(node)) {
            this.nodeType = node.nodeType();
            this.refreshChildren(node.children());
            this.panic(SyntaxError.UN_EXPECTED);
        } else {
            this.parent().replaceChild(this, node);
        }
    }
}
