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

import java.util.HashMap;
import java.util.Map;

/**
 * Entity类型表达式
 *
 * @since 1.0
 */
public class EntityTypeExpr extends ComplexTypeExpr {
    /**
     * 构造函数
     *
     * @param entity 语法节点
     * @param members 成员变量
     */
    public EntityTypeExpr(SyntaxNode entity, Map<String, TypeExpr> members) {
        super(entity);
        if (members != null) {
            this.members().putAll(members);
        }
    }

    @Override
    public boolean is(TypeExpr expr) {
        if (super.is(expr)) {
            return true;
        }
        if (!(expr.exactBe() instanceof EntityTypeExpr)) {
            return false;
        }
        EntityTypeExpr exact = (EntityTypeExpr) expr.exactBe();
        if (this.key().equals(exact.key())) {
            return true;
        }
        for (String key : exact.members().keySet()) {
            TypeExpr value = this.members().get(key);
            if (value == null) {
                return false;
            }
            if (!value.is(exact.members().get(key))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TypeExpr polish() {
        EntityTypeExpr expr = new EntityTypeExpr(this.node(), new HashMap<>());
        return polish(expr);
    }

    /**
     * 对Entity类型表达式进行求值
     *
     * @param expr Entity类型表达式
     * @return 返回求值后的Entity类型表达式
     */
    protected EntityTypeExpr polish(EntityTypeExpr expr) {
        for (String entry : this.myMembers().keySet()) {
            expr.myMembers().put(entry, this.members().get(entry).polish());
        }
        expr.base = this.base;
        if (expr.base != null) {
            expr.base = expr.base.polish();
        }
        return expr;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new EntityTypeExpr(node, new HashMap<>());
    }

    @Override
    public Type type() {
        return Type.ENTITY;
    }
}
