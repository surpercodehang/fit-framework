/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.ComplexTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * Map类型表达式
 *
 * @since 1.0
 */
public class MapTypeExpr extends ComplexTypeExpr {
    public MapTypeExpr(SyntaxNode node) {
        super(node);
    }

    @Override
    public Type type() {
        return Type.MAP;
    }

    @Override
    public boolean is(TypeExpr expr) {
        if (super.is(expr)) {
            return true;
        }
        return expr instanceof MapTypeExpr; // maybe not that simple
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new MapTypeExpr(node);
    }
}
