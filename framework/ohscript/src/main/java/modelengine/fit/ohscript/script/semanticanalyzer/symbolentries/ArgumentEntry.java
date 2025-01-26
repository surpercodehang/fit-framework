/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Category;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 参数条目
 *
 * @since 1.0
 */
public class ArgumentEntry extends KnownSymbolEntry {
    private static final ArgumentEntry UNIT = new ArgumentEntry(TerminalNode.unit(), 1);

    public ArgumentEntry(TerminalNode node, long scope) {
        super(node, scope, Category.ARGUMENT, TypeExprFactory.createGeneric(node));
    }

    /**
     * 获取UNIT符号
     *
     * @return 符号
     */
    public static SymbolEntry unit() {
        return UNIT;
    }

    @Override
    public long scope() {
        return this.node().scope();
    }

    /**
     * 设置类型表达式
     *
     * @param typeExpr 类型表达式
     */
    public void setTypeExpr(TypeExpr typeExpr) {
        this.typeExpr = typeExpr;
    }
}
