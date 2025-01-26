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
 * 未知类型表达式，通常用于处理错误
 *
 * @since 1.0
 */
public class UnknownTypeExpr extends SimpleTypeExpr implements VainTypeExpr {
    public UnknownTypeExpr() {
        super(null);
    }

    private UnknownTypeExpr(TerminalNode node) {
        super(node);
    }

    @Override
    public Type type() {
        return Type.UNKNOWN;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new UnknownTypeExpr(node);
    }

    @Override
    public boolean is(TypeExpr expr) {
        return true;
    }
}
