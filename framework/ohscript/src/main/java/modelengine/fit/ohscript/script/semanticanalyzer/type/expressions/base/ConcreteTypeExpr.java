/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * provides base class for concrete type expressions.
 * 基本类型表达式
 *
 * @author 张群辉
 * @since 1.0
 */
public abstract class ConcreteTypeExpr extends TypeExpr {
    public ConcreteTypeExpr(SyntaxNode node) {
        super(node);
    }

    public ConcreteTypeExpr(String key, SyntaxNode node) {
        super(key, node);
    }
}
