/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 调用Entity的节点
 *
 * @since 1.0
 */
public class EntityCallNode extends CallNode {
    private MemberNode member;

    public EntityCallNode() {
        super(NonTerminal.ENTITY_CALL);
    }

    /**
     * 创建一个模拟的实体调用节点
     *
     * @param head 实体头部节点
     * @param member 实体成员节点
     * @return 模拟的实体调用节点
     */
    public static EntityCallNode mock(SyntaxNode head, MemberNode member) {
        EntityCallNode call = new EntityCallNode();
        call.addChild(head);
        call.addChild(member);
        call.member = member;
        return call;
    }

    @Override
    public void optimizeGama() {
        if (this.childCount() == 2 && this.member != null) {
            return; // has been optimized
        }
        SyntaxNode child = this.removeAt(this.childCount() - 1); // the last child node
        SyntaxNode newParent = null;
        // for field
        if (child instanceof TerminalNode) { // if it is terminal, it must be a member
            member = new MemberNode((TerminalNode) child);
        } else { // the last node could be [i] for array call or (arg) for func call
            SyntaxNode last = this.removeAt(this.childCount() - 1); // before array/fun call, there must be a field node
            if (this.childCount()
                    == 0) { // a.b.c(); here put a.b.c at the head of ()/[] node, the result is like function
                // call:entity call() and entity call is a.b.c
                child.addChild(last, 0);
                this.parent().replaceChild(this, child);
                this.ast().optimizeGama(child);
                return;
            }
            TerminalNode terminal = ObjectUtils.cast(last);
            member = new MemberNode(terminal);
            this.parent().replaceChild(this, child);
            child.addChild(this, 0);
            newParent = child;
        }
        this.removeAt(this.childCount() - 1); // remove dot;
        // for host
        SyntaxNode entity;
        if (this.childCount() > 1) { // create nested entity call node
            entity = new EntityCallNode();
            entity.refreshChildren(this.children());
        } else {
            entity = this.removeAt(0);
        }
        this.nodes.clear();
        this.addChild(entity);
        this.addChild(member);
        entity.optimizeGama();
        if (newParent != null) {
            newParent.ast().optimizeGama(newParent);
        }
    }

    /**
     * 获取实体节点
     *
     * @return 实体节点
     */
    public SyntaxNode entity() {
        return this.child(0);
    }

    /**
     * 获取成员节点
     *
     * @return 成员节点
     */
    public TerminalNode member() {
        return this.member; // 注释：(TerminalNode) this.child(1);
    }

    @Override
    public List<SyntaxNode> childrenNeedsInfer() {
        return this.children().stream().filter(c -> c instanceof NonTerminalNode).collect(Collectors.toList());
    }
}
