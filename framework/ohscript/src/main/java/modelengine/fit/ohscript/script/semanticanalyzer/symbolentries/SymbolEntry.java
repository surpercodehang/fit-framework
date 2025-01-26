/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Category;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

import java.io.Serializable;

/**
 * 符号表中的基本条目，代表了一个符号（如变量、函数、类等）的相关信息
 *
 * @since 1.0
 */
public abstract class SymbolEntry implements Serializable {
    private static final long serialVersionUID = -3525520752129457417L;

    /**
     * 符号的类别
     * 例如，变量、函数、类等
     */
    protected Category category;

    private final TerminalNode node;

    /**
     * 构造函数
     *
     * @param node 符号节点
     * @param category 符号类别
     */
    public SymbolEntry(TerminalNode node, Category category) {
        this.node = node;
        this.category = category;
    }

    /**
     * 获取entry的id
     *
     * @return 符号的id
     */
    public abstract long id();

    /**
     * 获取entry对应的节点
     *
     * @return 符号节点
     */
    public TerminalNode node() {
        return this.node;
    }

    /**
     * 获取entry的作用域
     *
     * @return 符号的作用域
     */
    public long scope() {
        return this.node.parentScope();
    }

    /**
     * 获取entry的类型表达式
     *
     * @return 符号的类型表达式
     */
    public abstract TypeExpr typeExpr();
}
