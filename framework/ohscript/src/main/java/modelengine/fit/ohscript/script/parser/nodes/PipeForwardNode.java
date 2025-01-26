/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 管道节点
 *
 * @since 1.0
 */
public class PipeForwardNode extends NonTerminalNode {
    public PipeForwardNode() {
        super(NonTerminal.PIPE_FORWARD);
    }

    @Override
    public void optimizeDelta() {
        SyntaxNode funcCall = null;
        SyntaxNode arg = this.removeAt(0);
        while (this.childCount() > 0) {
            this.removeAt(0);
            SyntaxNode func = this.removeAt(0);
            funcCall = new FunctionCallNode();
            funcCall.addChild(func);
            func.optimizeDelta();
            funcCall.addChild(new TerminalNode(Terminal.LEFT_PAREN));
            funcCall.addChild(arg);
            funcCall.addChild(new TerminalNode(Terminal.RIGHT_PAREN));
            funcCall.optimizeGama();
            arg = funcCall;
        }
        this.parent().replaceChild(this, funcCall);
    }
}
