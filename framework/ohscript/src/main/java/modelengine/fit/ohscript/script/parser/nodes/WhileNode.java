/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * while节点
 *
 * @since 1.0
 */
public class WhileNode extends NonTerminalNode {
    /**
     * 构造函数
     */
    public WhileNode() {
        super(NonTerminal.WHILE_STATEMENT);
        this.returnAble = true;
    }

    /**
     * 获取条件语句
     *
     * @return 条件语句
     */
    public SyntaxNode condition() {
        return this.child(2);
    }

    /**
     * 获取循环体
     *
     * @return 循环体
     */
    public SyntaxNode body() {
        return this.child(4);
    }
}
