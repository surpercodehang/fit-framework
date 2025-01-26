/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import modelengine.fit.ohscript.util.Constants;
import modelengine.fit.ohscript.util.Tool;
import modelengine.fitframework.util.ObjectUtils;

/**
 * Entity的内容节点
 *
 * @since 1.0
 */
public class EntityBodyNode extends NonTerminalNode {
    private SyntaxNode host = null;

    private TerminalNode name;

    public EntityBodyNode() {
        super(NonTerminal.ENTITY_BODY);
    }

    /**
     * 虚拟一个空的Entity体，通常用于管理external的对象
     *
     * @return 虚拟的节点
     */
    public static EntityBodyNode mock() {
        EntityBodyNode body = new EntityBodyNode();
        body.addChild(new TerminalNode(Terminal.LEFT_BRACE));
        body.addChild(new TerminalNode(Terminal.RIGHT_BRACE));
        return body;
    }

    @Override
    public void optimizeBeta() {
        for (int i = 0; i < this.childCount(); i++) {
            if (this.child(i).nodeType() == Terminal.DOT || this.child(i).nodeType() == Terminal.ENTITY_BODY_BEGIN) {
                InitialAssignmentNode member = ObjectUtils.cast(this.child(i + 1));
                this.members().add(member);
            }
        }
        SyntaxNode parent = this.parent();
        if (parent instanceof EntityExtensionNode) {
            // add .base to extended object
            TerminalNode left = new TerminalNode(Terminal.ID);
            left.setToken(new Token(Terminal.ID, Constants.BASE, this.location().startLine(), 0, 0));
            InitialAssignmentNode base = InitialAssignmentNode.mock(left, new TerminalNode(Terminal.QUESTION));
            this.addChild(base);
            this.members().add(base);
        }
        parent.members().addAll(this.members());
    }

    @Override
    public void optimizeGama() {
        if (this.parent() instanceof EntityDeclareNode || this.parent() instanceof SystemExtensionNode) {
            return;
        }
        for (InitialAssignmentNode member : this.members) {
            TerminalNode node = ObjectUtils.cast(member.child(0));
            MemberNode newName = new MemberNode(node);
            member.replaceChild(node, newName);
        }
        // extension should have its own scope
        this.scope = Tool.newId();
        this.setAst(this.ast());

        this.host = this.child(0);

        if (this.host instanceof TerminalNode && this.host.nodeType() != Terminal.ID) {
            this.name = ObjectUtils.cast(this.host);
        } else {
            this.name = new TerminalNode(Terminal.ID);
            name.setToken(new Token(Terminal.ID, String.valueOf(Tool.newId()), 0, 0, 0));
            this.addChild(name, 1);
        }
    }

    private SyntaxNode scopeParent() {
        return this.parent();
    }

    /**
     * 获取Entity的宿主对象
     *
     * @return 宿主对象
     */
    public SyntaxNode host() {
        return this.host;
    }

    /**
     * 获取Entity的声明名称
     *
     * @return 声明名称
     */
    public TerminalNode declaredName() {
        return this.name;
    }

    @Override
    public TypeExpr typeExpr() {
        return super.typeExpr();
    }
}
