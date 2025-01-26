/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.assists;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.AbstractTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 工具类，提供一系列方法，提供对于抽象类型表达式的限制
 * means the real type expression of the attached type expression is constrained by the constraints
 * should be: is constraints of left value
 * has to be: is constraints of right value
 * huizi 2023
 *
 * @since 1.0
 */
public class Constraints implements Serializable {
    private static final long serialVersionUID = 931997351260008366L;

    private final SyntaxNode node;

    private final List<TypeExpr> shouldBe = new ArrayList<>();

    private final List<TypeExpr> hasTodBe = new ArrayList<>();

    private final Set<TypeExpr> supposedToBe = new HashSet<>();

    /**
     * 构造函数
     *
     * @param node 语法节点
     */
    public Constraints(SyntaxNode node) {
        this.node = node;
    }

    /**
     * 获取should be的约束
     *
     * @return 返回should be的约束
     */
    public TypeExpr shouldBe() {
        return shouldBe.size() == 0 ? null : shouldBe.get(0);
    }

    /**
     * 获取has to be的约束
     *
     * @return 返回has to be的约束
     */
    public TypeExpr hasToBe() {
        return hasTodBe.size() == 0 ? null : hasTodBe.get(0);
    }

    /**
     * 获取假设的类型表达式
     *
     * @return 返回假设的类型表达式
     */
    public Set<TypeExpr> supposedToBe() {
        return this.supposedToBe;
    }

    /**
     * 添加假设的类型表达式
     *
     * @param supposed 需要添加的类型表达式
     */
    public void addSupposedToBe(TypeExpr supposed) {
        for (TypeExpr typeExpr : this.supposedToBe) {
            if (supposed.is(typeExpr)) {
                return;
            }
        }
        this.supposedToBe.add(supposed);
    }

    /**
     * 添加should be的约束
     *
     * @param should 需要添加的约束
     * @return 如果添加成功，返回true，否则返回false
     */
    public boolean addShouldBe(TypeExpr should) {
        return this.addConstraint(should, this.shouldBe);
    }

    /**
     * 添加has to be的约束
     *
     * @param hasTo 需要添加的约束
     * @return 如果添加成功，返回true，否则返回false
     */
    public boolean addHasToBe(TypeExpr hasTo) {
        return this.addConstraint(hasTo, this.hasTodBe);
    }

    /**
     * 添加约束
     *
     * @param needed 需要添加的约束
     * @param list 约束列表
     * @return 如果添加成功，返回true，否则返回false
     */
    private boolean addConstraint(TypeExpr needed, List<TypeExpr> list) {
        if (list.size() == 0) {
            list.add(needed);
            return true;
        }
        TypeExpr first = list.get(0);
        if (first.is(needed)) {
            if (!list.contains(needed)) {
                list.add(needed);
            }
            return true;
        }
        if (needed.is(first)) {
            if (!list.contains(needed)) {
                list.add(0, needed);
            }
            return true;
        }
        this.node.panic(SyntaxError.TYPE_MISMATCH, "constraints list meets type mismatch conflict ");
        return false;
    }

    /**
     * 使所有的约束失效
     */
    public void invalidate() {
        this.invalidateSet(this.shouldBe);
        this.invalidateSet(this.hasTodBe);
    }

    private void invalidateSet(List<TypeExpr> set) {
        for (TypeExpr typ : set) {
            if (typ instanceof AbstractTypeExpr) {
                typ.invalidate();
            }
        }
    }

    /**
     * 清除所有的约束
     * means to clear all the constraints
     */
    public void clear() {
        this.hasTodBe.clear();
        this.shouldBe.clear();
        this.supposedToBe.clear();
    }
}
