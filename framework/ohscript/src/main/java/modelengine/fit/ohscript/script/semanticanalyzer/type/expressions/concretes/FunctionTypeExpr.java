/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.parser.nodes.FunctionDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.SyntaxException;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.abstracts.GenericTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.AbstractTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.ComplexTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.Projectable;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 方法调用类型
 *
 * @since 1.0
 */
public class FunctionTypeExpr extends ComplexTypeExpr implements Projectable {
    private TypeExpr returnType;

    private TypeExpr argumentType;

    /**
     * 构造函数
     *
     * @param function 函数声明节点
     * @param argumentType 参数类型
     * @param returnType 返回类型
     */
    public FunctionTypeExpr(SyntaxNode function, TypeExpr argumentType, TypeExpr returnType) {
        super(function);
        this.argumentType = argumentType.exactBe();
        this.returnType = returnType.exactBe();
    }

    /**
     * 设置参数类型
     *
     * @param expr 参数类型表达式
     */
    public void setArgumentType(TypeExpr expr) {
        this.argumentType = expr; // 注释：.exactBe();
    }

    /**
     * 设置返回类型
     *
     * @param expr 返回类型表达式
     */
    public void setReturnType(TypeExpr expr) {
        this.returnType = expr; // 注释：.exactBe();
    }

    /**
     * 获取函数声明节点
     *
     * @return 函数声明节点
     */
    public FunctionDeclareNode function() {
        return ObjectUtils.cast(this.node());
    }

    /**
     * 参数类型
     *
     * @return 参数类型
     */
    public TypeExpr argumentType() {
        return this.argumentType;
    }

    /**
     * 返回类型
     *
     * @return 返回类型
     */
    public TypeExpr returnType() {
        return this.returnType;
    }

    @Override
    public boolean is(TypeExpr expr) {
        TypeExpr exact = expr.exactBe();
        if (exact instanceof GenericTypeExpr) {
            if (AbstractTypeExpr.isOneOf(exact, expr.couldBe())) {
                return true;
            }
        }
        if (!(exact instanceof FunctionTypeExpr)) {
            return false;
        }
        return ((FunctionTypeExpr) exact).argumentType().is(this.argumentType())
                && ((FunctionTypeExpr) exact).returnType().is(this.returnType());
    }

    @Override
    public Type type() {
        return Type.FUNCTION;
    }

    @Override
    public TypeExpr polish() {
        TypeExpr arg = this.argumentType.polish();
        TypeExpr r = this.returnType.polish();
        if (arg == this.argumentType && r == this.returnType) {
            return this;
        }
        return new FunctionTypeExpr(this.function(), arg, r);
    }

    @Override
    public TypeExpr project(TypeExpr funcArg, TypeExpr projection) throws SyntaxException {
        // project myself
        if (funcArg == this) {
            return this.project(projection);
        }
        TypeExpr argOrigin = this.argumentType().exactBe();
        if (argOrigin == funcArg && !(argOrigin instanceof Projectable)) {
            if (!projection.is(argOrigin)) {
                throw new SyntaxException(SyntaxError.TYPE_MISMATCH,
                        "input argument type is not matched in expected argument type.");
            }
        }
        // project argument
        TypeExpr argType = argOrigin;
        if (argOrigin instanceof Projectable) {
            argType = ((Projectable) argOrigin).project(funcArg, projection);
        }
        // project return
        TypeExpr projectReturnType = this.returnType().exactBe();
        if (projectReturnType instanceof Projectable) {
            projectReturnType = ((Projectable) projectReturnType).project(funcArg, projection);
        }
        return new FunctionTypeExpr(this.node, argType, projectReturnType);
    }

    @Override
    public void clearProjection() {
        if (this.returnType instanceof Projectable) {
            ((Projectable) this.returnType).clearProjection();
        }
        if (this.argumentType instanceof Projectable) {
            ((Projectable) this.argumentType).clearProjection();
        }
    }

    @Override
    public TypeExpr project(TypeExpr projection) throws SyntaxException {
        TypeExpr projectReturnType = this.returnType;
        FunctionTypeExpr funcProject = ObjectUtils.cast(projection);
        // project argument first
        FunctionTypeExpr projectArg = ObjectUtils.cast(
                funcProject.project(funcProject.argumentType, this.argumentType));
        TypeExpr arg = projectArg.argumentType;
        // project return
        TypeExpr returnProject = projectArg.returnType();
        if (projectReturnType instanceof Projectable) {
            projectReturnType = ((Projectable) projectReturnType).project(returnProject);
        } else {
            if (!projectReturnType.is(returnProject)) {
                throw new SyntaxException(SyntaxError.TYPE_MISMATCH,
                        "the return type doesn't match " + projectReturnType.type().name().toLowerCase());
            }
        }
        return new FunctionTypeExpr(this.node(), arg, projectReturnType);
    }

    @Override
    public String toString() {
        return this.argumentType().exactBe() + "->" + this.returnType().exactBe();
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new FunctionTypeExpr(node, this.argumentType, this.returnType);
    }
}
