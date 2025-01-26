/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.EntityDeclareNode;
import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Category;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.EntityTypeExpr;
import modelengine.fitframework.util.ObjectUtils;

/**
 * Entity对象条目
 *
 * @since 1.0
 */
public class EntityEntry extends KnownSymbolEntry<EntityTypeExpr> {
    /**
     * 实体声明节点
     * 这是一个实体声明节点，它是EntityEntry对象的一个属性，用于存储实体声明节点的信息。
     */
    protected final EntityDeclareNode entity;

    /**
     * 构造函数
     * 这是EntityEntry对象的构造函数，它接收一个TerminalNode对象，一个long类型的scope和一个EntityTypeExpr对象作为参数。
     * 这个构造函数首先调用父类的构造函数，然后将node的parent()方法的返回值转换为EntityDeclareNode对象，并赋值给entity属性。
     *
     * @param node 一个TerminalNode对象
     * @param scope 一个long类型的scope
     * @param typeExpr 一个EntityTypeExpr对象
     */
    public EntityEntry(TerminalNode node, long scope, EntityTypeExpr typeExpr) {
        super(node, scope, Category.VARIABLE, typeExpr);
        this.entity = ObjectUtils.cast(node.parent());
    }

    /**
     * 获取实体声明节点
     *
     * @return 实体声明节点
     */
    public EntityDeclareNode entity() {
        return entity;
    }

    /**
     * 设置类别
     *
     * @param category 类别
     */
    public void setCategory(Category category) {
        this.category = category;
    }
}
