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
    /**
     * for循环的索引节点，表示循环变量
     */
    private TerminalNode index;

    /**
     * for循环的初始化语句节点，用于初始化循环变量
     */
    private SyntaxNode initial;

    /**
     * for循环的条件判断节点，用于判断是否继续循环
     */
    private SyntaxNode condition;

    /**
     * for循环的迭代表达式节点，用于更新循环变量
     */
    private SyntaxNode expression;

    /**
     * 初始化 {@link ForNode} 的新实例。
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
