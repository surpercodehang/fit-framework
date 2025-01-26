/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * if节点
 *
 * @since 1.0
 */
public class IfNode extends NonTerminalNode {
    private final List<IfBranch> branches = new ArrayList<>();

    /**
     * 构造函数
     */
    public IfNode() {
        super(NonTerminal.IF_STATEMENT);
        this.returnAble = true;
    }

    /**
     * 构造一个虚假的if节点
     *
     * @param conditions if的分支条件
     * @return 构造的if节点
     */
    public static IfNode mock(Pair<SyntaxNode, SyntaxNode>... conditions) {
        int index = 0;
        IfNode root = null;
        IfNode parent = null;
        SyntaxNode node;
        while (conditions.length > index) {
            Pair<SyntaxNode, SyntaxNode> condition = conditions[index];
            if (condition.first().nodeType() == Terminal.UNIT) {
                node = condition.second();
            } else {
                node = new IfNode();
                node.addChild(new TerminalNode(Terminal.IF));
                node.addChild(new TerminalNode(Terminal.LEFT_PAREN));
                node.addChild(condition.first());
                node.addChild(new TerminalNode(Terminal.RIGHT_PAREN));
                node.addChild(condition.second());
            }
            if (parent == null) {
                root = parent = (IfNode) node;
            } else {
                GeneralNode elseNode = new GeneralNode(NonTerminal.ELSE_STATEMENT);
                elseNode.addChild(new TerminalNode(Terminal.ELSE));
                elseNode.addChild(node);
                parent.addChild(elseNode);
                if (node instanceof IfNode) {
                    parent = (IfNode) node;
                }
            }
            index++;
        }
        return root;
    }

    @Override
    public void optimizeGama() {
        if (branches.size() > 0) {
            return;
        }
        SyntaxNode next = this;
        while (true) {
            branches.add(IfBranch.mock(next.child(2), next.child(4)));
            if (next.childCount() < 6) {
                break;
            }
            next = next.child(5).child(1);
            if (next.nodeType() != NonTerminal.IF_STATEMENT) {
                branches.add(IfBranch.mock(TerminalNode.unit(), next));
                break;
            }
        }
        for (IfBranch branch : this.branches) {
            if (branch.child(1) instanceof BlockNode) {
                branch.child(1).returnAble = true;
            }
        }
        this.refreshChildren(new ArrayList<>(this.branches));
    }

    /**
     * 获取if分支
     *
     * @return 分支列表
     */
    public List<SyntaxNode> branches() {
        return new ArrayList<>(branches);
    }
}
