/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 语句节点
 *
 * @since 1.0
 */
public class StatementsNode extends NonTerminalNode {
    /**
     * 构造函数
     * 构造一个新的语句节点
     */
    public StatementsNode() {
        super(NonTerminal.STATEMENTS);
        this.returnAble = true;
    }

    @Override
    public void optimizeBeta() {
        super.optimizeBeta();
    }

    @Override
    public void optimizeAlpha() {
        super.optimizeAlpha();
        List<SyntaxNode> nodes = new ArrayList<>();
        for (int i = 0; i < this.childCount(); i++) {
            nodes.add(this.child(i).child(0));
        }
        this.refreshChildren(nodes);
        if (this.parent() != this.ast().start()) {
            return;
        }
        buildOhNodes(this.ast().ohs());
    }

    private void buildOhNodes(List<SyntaxNode> ohs) {
        LetStatementNode letSysOh = null;
        for (SyntaxNode oh : ohs) {
            if (letSysOh == null) {
                this.addChild(oh, 0);
                letSysOh = ObjectUtils.cast(oh);
            } else {
                letSysOh.addChild(oh.child(1));
            }
        }
    }
}
