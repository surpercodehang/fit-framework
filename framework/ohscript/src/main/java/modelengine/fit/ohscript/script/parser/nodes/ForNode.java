/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

/**
 * for节点
 *
 * @since 1.0
 */
public class ForNode extends NonTerminalNode {
    private TerminalNode index;

    private SyntaxNode initial;

    private SyntaxNode condition;

    private SyntaxNode expression;

    /**
     * 构造函数
     *
     * @param node 非终端节点
     */
    public ForNode() {
        super(NonTerminal.FOR_STATEMENT);
        this.returnAble = true;
    }

    @Override
    public void optimizeGama() {
        if (this.index != null) {
            return;
        }
        final int off = 2;
        this.initial = this.child(off);
        this.index = ObjectUtils.cast(this.initial.child(0).child(0));
        this.condition = this.child(off + 1);
        this.expression = this.child(off + 3);
    }

    /**
     * 获取for循环的索引
     *
     * @return for循环的索引
     */
    public TerminalNode index() {
        return this.index;
    }

    /**
     * 获取for循环体
     *
     * @return 循环体
     */
    public BlockNode body() {
        return ObjectUtils.cast(this.child(7));
    }

    /**
     * 获取for循环条件
     *
     * @return 循环条件
     */
    public SyntaxNode condition() {
        return this.condition;
    }

    /**
     * 获取for循环表达式
     *
     * @return 循环表达式
     */
    public SyntaxNode expression() {
        return this.expression;
    }

    /**
     * 获取for循环的初始化表达式
     *
     * @return 初始化表达式
     */
    public SyntaxNode initial() {
        return this.initial;
    }
}
