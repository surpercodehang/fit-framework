/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * if分支节点
 *
 * @since 1.0
 */
public class IfBranch extends NonTerminalNode {
    /**
     * 构造函数
     */
    protected IfBranch() {
        super(NonTerminal.IF_BRANCH);
    }

    /**
     * 创建一个模拟的if分支节点
     *
     * @param condition 条件语句节点
     * @param block 代码块语句节点
     * @return 创建的模拟if分支节点
     */
    public static IfBranch mock(SyntaxNode condition, SyntaxNode block) {
        IfBranch branch = new IfBranch();
        branch.addChild(condition);
        branch.addChild(block);
        return branch;
    }
}
