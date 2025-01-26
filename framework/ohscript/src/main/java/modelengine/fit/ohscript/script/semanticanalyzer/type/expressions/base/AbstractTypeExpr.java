/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.SyntaxException;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.assists.Constraints;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.IgnoreTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.UnitTypeExpr;

import java.util.HashSet;
import java.util.Set;

/**
 * 抽象类型表达式
 *
 * @since 1.0
 */
public abstract class AbstractTypeExpr extends TypeExpr implements Projectable {
    /**
     * 精确的类型表达式
     */
    protected TypeExpr exactBe = null;

    private final Constraints constraints;

    private final Set<TypeExpr> couldBe = new HashSet<>();

    private TypeExpr projection;

    /**
     * 构造函数
     *
     * @param node 语法节点
     */
    public AbstractTypeExpr(SyntaxNode node) {
        this(node, new Constraints(node));
    }

    /**
     * 构造函数
     *
     * @param node 语法节点
     * @param constraints 约束
     */
    protected AbstractTypeExpr(SyntaxNode node, Constraints constraints) {
        super(node);
        this.constraints = constraints;
    }

    /**
     * 判断一个type expression是否属于一个type expression集合：a∈[b]
     *
     * @param a 要判断的type expression
     * @param bSet 要判断是否属于的type expression集合
     * @return 判断是否属于
     */
    public static boolean isOneOf(TypeExpr a, Set<TypeExpr> bSet) {
        if (bSet.isEmpty()) {
            return true; // 空集合理解为没有约束
        }
        for (TypeExpr b : bSet) {
            if (a.is(b)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取约束
     *
     * @return 约束
     */
    protected Constraints constraints() {
        return this.constraints;
    }

    /**
     * 添加supposedToBe约束
     *
     * @param supposed 要添加的supposedToBe约束
     */
    public void addSupposedToBe(TypeExpr supposed) {
        this.constraints().addSupposedToBe(supposed);
        this.invalidate();
    }

    /**
     * 添加shouldBe约束
     *
     * @param shouldBe 要添加的shouldBe约束
     */
    public void addShouldBe(TypeExpr shouldBe) {
        if (this.constraints.addShouldBe(shouldBe)) {
            // 约束变化，重新推导
            this.invalidate();
        }
    }

    /**
     * 添加hasToBe约束
     *
     * @param hasToBe 要添加的hasToBe约束
     */
    public void addHasToBe(TypeExpr hasToBe) {
        TypeExpr exactMe = this; // 注释：.exactBe();
        if (exactMe == hasToBe) {
            return;
        }
        if (this.constraints.addHasToBe(hasToBe)) {
            // 约束变化，重新推导
            this.invalidate();
        }
    }

    /**
     * 从shouldBe和hasToBe约束中获取类型表达式
     *
     * @return 类型表达式
     */
    protected TypeExpr typeExprFromShouldBeAndHasToBe() {
        return this.typeExprFromShouldBeAndHasToBe(null);
    }

    /**
     * 从shouldBe和hasToBe约束中获取类型表达式
     *
     * @param exactBe 精确的类型表达式
     * @return 类型表达式
     */
    protected TypeExpr typeExprFromShouldBeAndHasToBe(TypeExpr exactBe) {
        // get exact from should be
        TypeExpr newExactBe = typeExprFromConstraint(this.constraints().shouldBe(), exactBe);
        // get exact from has to be
        newExactBe = typeExprFromConstraint(this.constraints().hasToBe(), newExactBe);
        return newExactBe;
    }

    private TypeExpr typeExprFromConstraint(TypeExpr constraint, TypeExpr exact) {
        if (constraint == null) {
            return exact;
        }

        if (exact != null) {
            if (exact.is(constraint)) {
                return exact;
            }
            if (constraint.is(exact)) {
                return constraint;
            }
            this.node().panic(SyntaxError.TYPE_MISMATCH, "constraint doesn't match the exact type expression");
            return exact;
        }
        if (this.couldBe().isEmpty()) {
            return constraint;
        }

        for (TypeExpr expr : this.couldBe()) {
            if (constraint.is(expr)) {
                this.couldBe().clear();
                return constraint;
            }
            if (expr.is(constraint)) {
                this.couldBe().clear();
                return expr;
            }
        }
        this.node().panic(SyntaxError.TYPE_MISMATCH, "constraint doesn't match possible type expressions");
        return null;
    }

    @Override
    public boolean is(TypeExpr expr) {
        TypeExpr exactY = expr.exactBe();
        if (exactY instanceof UnitTypeExpr || exactY instanceof IgnoreTypeExpr) {
            return false;
        }
        TypeExpr exactX = this.exactBe();
        // 验证x <: y
        // 如果x有exact:
        if (exactX != this) {
            // y没有exact, 则验证x.exact <: one of y.couldBe
            if (exactY == expr) {
                return isOneOf(exactX, expr.couldBe());
            } else { // y有exact，则验证 x.exact <: y.exact
                return exactX.is(exactY);
            }
        }
        // 如果x没有exact
        // 也没有couldBe,那么x是最空的泛型，<:任何类型
        if (this.couldBe.isEmpty() || exactY.couldBe().isEmpty()) {
            return true;
        }
        // 如果有couldBe, y没有exact，那么验证 x.couldBe∈y.couldBe
        if (exactY == expr) {
            return isPartOf(this.couldBe, expr.couldBe());
        }
        // 如果y有exact
        // x.couldBe.size>1, 那么肯定有一部分不属于exact
        if (this.couldBe.size() > 1) {
            return false;
        }
        // 如果x.couldBe.siz==1，那么x.couldBe[0] <: y.exact
        return this.couldBe.toArray(new TypeExpr[0])[0].is(exactY);
    }

    /**
     * 判断一个type expression集合是否属于另一个type expression集合：[a]⊆[b]
     *
     * @param aSet [a]
     * @param bSet [b]
     * @return 判断是否为一部分
     */
    private boolean isPartOf(Set<TypeExpr> aSet, Set<TypeExpr> bSet) {
        for (TypeExpr a : aSet) {
            if (!isOneOf(a, bSet)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算类型表达式的精确值
     *
     * @return 类型表达式的精确值
     */
    protected abstract TypeExpr evaluate();

    @Override
    public void invalidate() {
        TypeExpr exactPrevious = this.exactBe;
        this.updateExactBe();
        // 如果exact变化了，则驱动相关的类型都重新推导一遍
        if (this.exactBe != exactPrevious) {
            this.constraints.invalidate();
        }
    }

    private void updateExactBe() {
        TypeExpr exact = this;
        this.exactBe = null;
        while (exact != this.exactBe) { // 找到最后一级非空type expression
            this.exactBe = exact;
            if (exact instanceof AbstractTypeExpr) {
                exact = ((AbstractTypeExpr) exact).evaluate();
            }
            if (exact == null) {
                break;
            }
        }
    }

    @Override
    public void clearProjection() {
        this.projection = null;
    }

    @Override
    public TypeExpr project(TypeExpr projection) throws SyntaxException {
        if (projection instanceof AbstractTypeExpr) {
            if (this.is(projection)) {
                TypeExpr exact = this.exactBe();
                if (exact instanceof AbstractTypeExpr) {
                    ((AbstractTypeExpr) projection).clearConstraints();
                    ((AbstractTypeExpr) projection).addHasToBe(this);
                } else {
                    ((AbstractTypeExpr) projection).addHasToBe(exactBe);
                }
            }
        } else {
            if (projection.is(this)) {
                this.projection = projection;
            } else {
                if (projection.node() != null) {
                    projection.node().panic(SyntaxError.TYPE_MISMATCH);
                } else {
                    throw new SyntaxException(SyntaxError.TYPE_MISMATCH,
                            "projected type doesn't match expectation, projecting fail.");
                }
            }
        }
        return this.projection == null ? this : this.projection;
    }

    /**
     * 清除约束
     */
    private void clearConstraints() {
        this.constraints.clear();
    }

    @Override
    public TypeExpr exactBe() {
        if (this.projection != null) {
            return this.projection;
        }

        this.invalidate();
        if (this.exactBe == null || this.exactBe == this) {
            return this;
        }
        TypeExpr exact = this.exactBe.exactBe();
        while (this.exactBe != exact) {
            this.exactBe = exact;
            exact = exact.exactBe();
        }
        return this.exactBe;
    }

    @Override
    public Set<TypeExpr> couldBe() {
        return this.couldBe;
    }
}
