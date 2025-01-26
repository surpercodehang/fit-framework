/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.util.Pair;
import modelengine.fit.ohscript.util.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Match表达式节点
 *
 * @since 1.0
 */
public class MatchStatementNode extends NonTerminalNode {
    private TerminalNode matcher = null;

    /**
     * 构造函数
     */
    public MatchStatementNode() {
        super(NonTerminal.MATCH_STATEMENT);
        this.returnAble = true;
    }

    @Override
    public void optimizeGama() {
        if (this.matcher != null) {
            return;
        }
        List<SyntaxNode> newChildren = new ArrayList<>();
        newChildren.add(createMatcherAssignment());
        newChildren.add(createIf4Branches());
        this.refreshChildren(newChildren);
    }

    private SyntaxNode createIf4Branches() {
        List<SyntaxNode> matchBranches = this.children()
                .stream()
                .filter(c -> c.nodeType() == NonTerminal.MATCH_BRANCH || c.nodeType() == NonTerminal.MATCH_ELSE_BRANCH)
                .collect(Collectors.toList());
        Pair<SyntaxNode, SyntaxNode>[] ifBranches = new Pair[matchBranches.size()];
        for (int i = 0; i < matchBranches.size(); i++) {
            SyntaxNode matchBranch = matchBranches.get(i);
            SyntaxNode condition = TerminalNode.unit();
            if (matchBranch.nodeType() == NonTerminal.MATCH_BRANCH) {
                condition = new GeneralNode(NonTerminal.RELATIONAL_CONDITION);
                condition.addChild(matchBranch.child(1));
                SyntaxNode when = matchBranch.child(2);
                if (when.nodeType() == NonTerminal.MATCH_WHEN) {
                    condition.addChild(new TerminalNode(Terminal.AND_AND));
                    condition.addChild(when.child(2));
                }
            }
            SyntaxNode block = matchBranch.child(matchBranch.childCount() - 1);
            ifBranches[i] = new Pair<>(condition, block);
        }
        return IfNode.mock(ifBranches);
    }

    private SyntaxNode createMatcherAssignment() {
        SyntaxNode oldMatcher = this.child(1);
        // create a new let statement to assign match variable: match a();=> let 123 = a(); then refer 123 in next
        // section
        LetStatementNode letMatcher = new LetStatementNode();
        letMatcher.addChild(new TerminalNode(Terminal.LET));
        this.matcher = new TerminalNode(Terminal.ID);
        this.matcher.setToken(new Token(Terminal.ID, String.valueOf(Tool.newId()), 0, 0, 0));
        InitialAssignmentNode initial = InitialAssignmentNode.mock(matcher, oldMatcher);
        letMatcher.addChild(initial);
        return letMatcher;
    }

    /**
     * 获取匹配器
     *
     * @return 匹配器
     */
    public TerminalNode matcher() {
        return this.matcher;
    }
}
