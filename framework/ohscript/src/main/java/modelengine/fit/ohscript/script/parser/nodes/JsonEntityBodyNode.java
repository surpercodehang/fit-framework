/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

/**
 * json的Entity内容节点
 *
 * @since 1.0
 */
public class JsonEntityBodyNode extends NonTerminalNode {
    public JsonEntityBodyNode() {
        super(NonTerminal.JSON_ENTITY_BODY);
    }

    @Override
    public void optimizeBeta() {
        int index = this.parent().children().indexOf(this);
        if (this.parent instanceof EntityExtensionNode) {
            EntityDeclareNode entity = ObjectUtils.cast(this.parent());
            EntityBodyNode body = EntityBodyNode.mock();
            entity.removeChild(this.parent().children().get(index + 1));
            entity.removeChild(this.parent().children().get(index - 1));
            entity.replaceChild(this, body);
            this.children().stream().filter(c -> c.nodeType() == NonTerminal.JSON_ITEM).forEach(item -> {
                this.mockMember(body, ObjectUtils.cast(item));
            });
            body.parent().optimizeBeta();
            body.optimizeBeta();
        } else {
            // new entity
            EntityDeclareNode entity = EntityDeclareNode.mock();
            EntityBodyNode body = ObjectUtils.cast(entity.child(1));
            this.parent().parent().replaceChild(this.parent(), entity);
            this.children().stream().filter(c -> c.nodeType() == NonTerminal.JSON_ITEM).forEach(item -> {
                this.mockMember(body, ObjectUtils.cast(item));
            });
            entity.optimizeBeta();
            body.optimizeBeta();
        }
    }

    private void mockMember(EntityBodyNode body, JsonItemNode item) {
        body.addChild(new TerminalNode(Terminal.DOT), body.childCount() - 1);
        TerminalNode left = item.field();
        InitialAssignmentNode initial = InitialAssignmentNode.mock(left, item.expression());
        body.addChild(initial, body.childCount() - 1);
        body.addChild(new TerminalNode(Terminal.SEMICOLON), body.childCount() - 1);
    }
}