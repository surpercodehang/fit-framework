/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.NonTerminal;

import java.util.List;

/**
 * 元组声明节点
 *
 * @since 1.0
 */
public class TupleDeclareNode extends EntityDeclareNode {
    public TupleDeclareNode() {
        super(NonTerminal.TUPLE_DECLARE);
    }

    @Override
    public void optimizeBeta() {
        // it is expression
        if (this.childCount() == 3 && this.child(1).nodeType() == NonTerminal.EXPRESSION) {
            this.parent().replaceChild(this, this.child(1));
            return;
        }

        // it is tuple unpacker
        if ((this.parent() instanceof InitialAssignmentNode && this.parent().child(0) == this)
                || this.parent() instanceof TupleUnPackerNode) {
            TupleUnPackerNode unpacker = new TupleUnPackerNode();
            unpacker.refreshChildren(this.children());
            this.parent().replaceChild(this, unpacker);
            unpacker.optimizeAlpha();
            unpacker.optimizeBeta();
            return;
        }
        // it is tuple declare
        List<SyntaxNode> children = this.children();
        int index = 0;
        for (SyntaxNode child : children) {
            if (child.nodeType() == Terminal.LEFT_PAREN || child.nodeType() == Terminal.RIGHT_PAREN
                    || child.nodeType() == Terminal.COMMA) {
                continue;
            }
            TerminalNode left = new TerminalNode(Terminal.NUMBER);
            left.setToken(new Token(Terminal.NUMBER, String.valueOf(index++), child.location().startLine(),
                    child.location().startPosition(), child.location().startPosition()));
            InitialAssignmentNode assign = new InitialAssignmentNode();
            this.replaceChild(child, assign);
            assign.addChild(left);
            assign.addChild(new TerminalNode(Terminal.EQUAL));
            assign.addChild(child);
            this.members.add(assign);
        }
    }

    @Override
    public void optimizeGama() {
        if (this.child(0).nodeType() == Terminal.TUPLE) {
            return;
        }
        this.addChild(new TerminalNode(Terminal.TUPLE), 0);
        super.optimizeGama();
    }
}
