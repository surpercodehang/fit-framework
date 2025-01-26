/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Category;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 已知的符号表中的条目
 *
 * @since 1.0
 */
public class KnownSymbolEntry<T extends TypeExpr> extends SymbolEntry {
    /**
     * 类型表达式
     * 已知符号的类型表达式
     */
    protected T typeExpr;

    private final long scope;

    /**
     * 构造函数
     *
     * @param node 节点
     * @param scope 作用域
     * @param category 类别
     * @param typeExpr 类型表达式
     */
    public KnownSymbolEntry(TerminalNode node, long scope, Category category, T typeExpr) {
        super(node, category);
        this.scope = scope;
        this.typeExpr = typeExpr;
    }

    @Override
    public long id() {
        return this.node().id();
    }

    @Override
    public long scope() {
        return this.scope;
    }

    @Override
    public TypeExpr typeExpr() {
        return this.typeExpr;
    }
}
