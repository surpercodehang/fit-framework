/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.SyntaxException;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.assists.Constraints;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.AbstractTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

import java.util.stream.Collectors;

/**
 * 通用类型表达式
 *
 * @since 1.0
 */
public class GenericTypeExpr extends AbstractTypeExpr {
    /**
     * 构造函数
     *
     * @param node 语法节点
     */
    public GenericTypeExpr(SyntaxNode node) {
        super(node);
        this.node = node;
    }

    private GenericTypeExpr(SyntaxNode node, Constraints constraints) {
        super(node, constraints);
        this.node = node;
    }

    @Override
    public Type type() {
        return Type.GENERIC;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        GenericTypeExpr expr = new GenericTypeExpr(node, this.constraints());
        expr.couldBe().addAll(this.couldBe());
        return expr;
    }

    @Override
    protected TypeExpr evaluate() {
        // get exact from supposed
        TypeExpr exactBe = calculateTypeExprFromSupposedToBe();
        // get exact from should be and has to be
        exactBe = typeExprFromShouldBeAndHasToBe(exactBe);
        return exactBe;
    }

    private TypeExpr calculateTypeExprFromSupposedToBe() {
        if (this.constraints().supposedToBe().size() == 1) {
            return this.constraints().supposedToBe().stream().collect(Collectors.toList()).get(0);
        } else {
            this.couldBe().addAll(this.constraints().supposedToBe());
            return null;
        }
    }

    @Override
    public TypeExpr project(TypeExpr origin, TypeExpr projection) throws SyntaxException {
        if (origin == this) {
            return this.project(projection);
        } else {
            return this;
        }
    }
}
