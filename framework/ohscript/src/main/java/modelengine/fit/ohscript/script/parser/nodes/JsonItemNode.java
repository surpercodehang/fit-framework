/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.lexer.Token;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

/**
 * json条目的节点
 *
 * @since 1.0
 */
public class JsonItemNode extends NonTerminalNode {
    /**
     * 构造函数
     * 构造一个新的JsonItemNode对象
     */
    public JsonItemNode() {
        super(NonTerminal.JSON_ITEM);
    }

    /**
     * 获取表达式
     * 获取表达式语法节点
     *
     * @return 表达式语法节点
     */
    public SyntaxNode expression() {
        return this.child(1);
    }

    /**
     * 获取字段
     * 获取字段语法节点
     *
     * @return 字段语法节点
     */
    public TerminalNode field() {
        String name = ObjectUtils.<TerminalNode>cast(this.child(0)).token().lexeme();
        name = name.replace(":", "");
        TerminalNode field = new TerminalNode(Terminal.ID);
        field.setToken(new Token(Terminal.ID, name, 0, 0, 0));
        return field;
    }
}
