/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.util.ObjectUtils;

/**
 * Entity声明的node，非终结符node
 *
 * @since 1.0
 */
public class EntityDeclareNode extends NonTerminalNode {
    /**
     * 实体声明的名字，是一个终结符node
     *
     * @since 1.0
     */
    protected TerminalNode name;

    /**
     * 构造一个entity声明的node
     */
    public EntityDeclareNode() {
        this(NonTerminal.ENTITY_DECLARE);
    }

    /**
     * 构造一个entity声明的node，并设置其父节点
     *
     * @param nonTerminal 非终结符
     * @since 1.0
     */
    protected EntityDeclareNode(NonTerminal nonTerminal) {
        super(nonTerminal);
    }

    /**
     * 构造一个虚拟的entity声明的node，通常用于承载外部的external对象
     *
     * @return 虚拟的节点
     */
    public static EntityDeclareNode mock() {
        EntityDeclareNode entity = new EntityDeclareNode();
        TerminalNode tag = new TerminalNode(Terminal.ENTITY);
        entity.addChild(tag);
        entity.addChild(EntityBodyNode.mock());
        return entity;
    }

    @Override
    public void optimizeBeta() {
        if (this.child(0).nodeType() != Terminal.ENTITY) {
            TerminalNode tag = new TerminalNode(Terminal.ENTITY);
            this.addChild(tag, 0);
        }
        super.optimizeBeta();
    }

    @Override
    public void optimizeGama() {
        if (this.name != null) {
            return;
        }
        this.name = new TerminalNode(Terminal.ID);
        name.setToken(new Token(Terminal.ID, String.valueOf(Tool.newId()), 0, 0, 0));
        this.addChild(name, 1);
        for (InitialAssignmentNode member : this.members) {
            TerminalNode node = ObjectUtils.cast(member.child(0));
            MemberNode newName = new MemberNode(node);
            member.replaceChild(node, newName);
        }
        super.optimizeGama();
    }

    @Override
    public TerminalNode declaredName() {
        return this.name;
    }
}
