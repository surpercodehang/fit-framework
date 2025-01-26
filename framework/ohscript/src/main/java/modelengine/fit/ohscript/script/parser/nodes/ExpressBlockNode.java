/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 表达式块节点
 *
 * @since 1.0
 */
public class ExpressBlockNode extends NonTerminalNode {
    public ExpressBlockNode() {
        super(NonTerminal.EXPRESS_BLOCK_STATEMENT);
    }

    @Override
    public void optimizeAlpha() {
        BlockNode node = new BlockNode();
        this.parent().replaceChild(this, node);
        node.addChild(new TerminalNode(Terminal.LEFT_BRACE));
        this.children().forEach(child -> node.addChild(child));
        node.addChild(new TerminalNode(Terminal.RIGHT_BRACE));
        node.parent().removeChild(node.parent().child(0));
        node.parent().removeChild(node.parent().child(1));
    }
}
