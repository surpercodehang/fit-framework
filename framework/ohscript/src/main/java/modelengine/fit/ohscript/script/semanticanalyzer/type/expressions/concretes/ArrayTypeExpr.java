/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.SyntaxException;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.AbstractTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.ComplexTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.Projectable;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 数组类型表达式
 *
 * @since 1.0
 */
public class ArrayTypeExpr extends ComplexTypeExpr implements Projectable {
    private TypeExpr itemTypeExpr;

    /**
     * 构造函数
     *
     * @param node 语法节点。
     */
    public ArrayTypeExpr(SyntaxNode node) {
        super(node);
        this.itemTypeExpr = TypeExprFactory.createGeneric(node);
    }

    /**
     * 构造函数
     *
     * @param node 语法节点。
     * @param itemExpr 数组项类型表达式。
     */
    private ArrayTypeExpr(SyntaxNode node, TypeExpr itemExpr) {
        super(node);
        this.itemTypeExpr = itemExpr;
    }

    /**
     * 判断是否为指定类型表达式
     *
     * @param expr 类型表达式。
     * @return 如果是指定类型表达式返回true，否则返回false。
     */
    public boolean is(TypeExpr expr) {
        TypeExpr newExpr = expr.exactBe();
        if (super.is(newExpr)) {
            return true;
        }
        if (!(newExpr instanceof ArrayTypeExpr)) {
            return false;
        }
        return this.itemTypeExpr().is(((ArrayTypeExpr) newExpr).itemTypeExpr());
    }

    @Override
    public Type type() {
        return Type.ARRAY;
    }

    @Override
    public TypeExpr exactBe() {
        if (this.itemTypeExpr instanceof AbstractTypeExpr) {
            TypeExpr itemExpr = this.itemTypeExpr();
            if (itemExpr != this.itemTypeExpr) {
                return new ArrayTypeExpr(this.node(), this.itemTypeExpr());
            }
        }
        return this;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        ArrayTypeExpr expr = new ArrayTypeExpr(node);
        expr.itemTypeExpr = this.itemTypeExpr;
        return expr;
    }

    /**
     * 获取数组项的类型表达式
     *
     * @return 数组项的类型表达式。
     */
    public TypeExpr itemTypeExpr() {
        return this.itemTypeExpr.exactBe();
    }

    /**
     * get the confirmed item type from push or initialization
     *
     * @param expr 类型表达式。
     */
    public void setItemTypeExpr(TypeExpr expr) {
        if (expr instanceof UnknownTypeExpr) {
            return;
        }
        if (expr.is(this.itemTypeExpr())) {
            for (String key : this.members().keySet()) {
                if (!(this.members().get(key) instanceof Projectable)) {
                    continue;
                }
                try {
                    TypeExpr projected = ((Projectable) this.members().get(key)).project(this.itemTypeExpr, expr);
                    ((Projectable) this.members().get(key)).clearProjection();
                    this.members().put(key, projected);
                } catch (SyntaxException e) {
                    throw new IllegalStateException(e);
                }
            }
            this.itemTypeExpr = expr;
            return;
        }
        if (!expr.is(this.itemTypeExpr)) {
            expr.node()
                    .panic(SyntaxError.TYPE_MISMATCH,
                            "the expected array item type is " + this.itemTypeExpr() + " actual type is " + expr);
        }
    }

    /**
     * sync item type from _array_ base type
     *
     * @param expr 类型表达式。
     */
    public void syncItemTypeExpr(TypeExpr expr) {
        this.itemTypeExpr = expr;
    }

    @Override
    public TypeExpr project(TypeExpr origin, TypeExpr projection) throws SyntaxException {
        if (origin == this) {
            return this.project(projection);
        }
        if (origin == this.itemTypeExpr()) {
            TypeExpr itemExpr = (ObjectUtils.<Projectable>cast(this.itemTypeExpr())).project(projection);
            return new ArrayTypeExpr(this.node(), itemExpr);
        }
        return this;
    }

    @Override
    public TypeExpr project(TypeExpr projection) throws SyntaxException {
        if (projection.is(this) && this.itemTypeExpr() instanceof Projectable) {
            TypeExpr itemExpr = (ObjectUtils.<Projectable>cast(this.itemTypeExpr())).project(
                    (ObjectUtils.<ArrayTypeExpr>cast(projection)).itemTypeExpr());
            return new ArrayTypeExpr(this.node(), itemExpr);
        } else {
            return this;
        }
    }

    @Override
    public void clearProjection() {
        if (this.itemTypeExpr instanceof Projectable) {
            ((Projectable) this.itemTypeExpr).clearProjection();
        }
    }
}
