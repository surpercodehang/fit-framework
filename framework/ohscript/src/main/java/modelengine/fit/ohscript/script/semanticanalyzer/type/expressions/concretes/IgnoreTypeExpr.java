/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.SimpleTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 没有类型的表达式会被定义为Ignore类型表达式
 * most of the terminals except id, number,string, especially keyword doesn't have type expression which will return
 * ignored type expression
 *
 * @since 1.0
 */
public class IgnoreTypeExpr extends SimpleTypeExpr implements VainTypeExpr {
    public IgnoreTypeExpr() {
        super(null);
    }

    private IgnoreTypeExpr(TerminalNode node) {
        super(node);
    }

    @Override
    public boolean is(TypeExpr expr) {
        return true;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new IgnoreTypeExpr(node);
    }

    @Override
    public Type type() {
        return Type.IGNORE;
    }
}
