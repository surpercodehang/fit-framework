/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.SyntaxException;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.assists.Constraints;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.AbstractTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.Projectable;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.NumberTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.StringTypeExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表达式类型
 *
 * @since 1.0
 */
public class ExprTypeExpr extends AbstractTypeExpr {
    private List<TypeExpr> types = new ArrayList<>();

    /**
     * 构造函数
     *
     * @param node 语法节点
     */
    public ExprTypeExpr(SyntaxNode node) {
        super(node);
        for (SyntaxNode child : node.children()) {
            TypeExpr type = child.typeExpr();
            if (type == null) {
                continue;
            }
            this.types.add(type.exactBe());
        }
    }

    private ExprTypeExpr(SyntaxNode node, Constraints constraints, List<TypeExpr> types) {
        super(node, constraints);
        this.types = types;
    }

    @Override
    protected TypeExpr evaluate() {
        List<TypeExpr> exactList = this.types.stream().map(t -> t.exactBe()).collect(Collectors.toList());
        for (TypeExpr type : exactList) {
            if (type instanceof StringTypeExpr) {
                this.couldBe().clear();
                return TypeExprFactory.createString(node);
            }
            if (type instanceof GenericTypeExpr) {
                this.couldBe().addAll(type.couldBe());
            }
        }
        return this.typeExprFromShouldBeAndHasToBe();
    }

    @Override
    public Type type() {
        return Type.EXPR;
    }

    @Override
    public TypeExpr project(TypeExpr origin, TypeExpr projection) throws SyntaxException {
        ExprTypeExpr expr = new ExprTypeExpr(this.node);
        expr.types.clear();
        for (TypeExpr type : this.types) {
            TypeExpr newType = type instanceof Projectable ? ((Projectable) type).project(origin, projection) : type;
            expr.types.add(newType);
        }
        return expr.exactBe();
    }

    @Override
    public TypeExpr exactBe() {
        TypeExpr exact = super.exactBe();
        List<TypeExpr> exactList = this.types.stream().map(t -> t.exactBe()).collect(Collectors.toList());
        if (exact instanceof StringTypeExpr) {
            return exact;
        }
        if (exact instanceof NumberTypeExpr) {
            for (TypeExpr type : exactList) {
                if (!(type instanceof NumberTypeExpr || type instanceof GenericTypeExpr)) {
                    type.node().panic(SyntaxError.TYPE_MISMATCH);
                }
            }
            return exact;
        }
        for (TypeExpr type : exactList) {
            if (type instanceof StringTypeExpr) {
                this.couldBe().clear();
                return TypeExprFactory.createString(node);
            }
            if (type instanceof GenericTypeExpr) {
                this.couldBe().addAll(type.couldBe());
            }
        }
        if (this.couldBe().size() == 0) {
            return TypeExprFactory.createNumber(node);
        }
        return exact == null ? this : exact;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new ExprTypeExpr(node, this.constraints(), this.types);
    }
}
