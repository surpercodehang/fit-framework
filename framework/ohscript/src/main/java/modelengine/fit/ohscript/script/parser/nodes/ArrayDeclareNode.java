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

import java.util.ArrayList;
import java.util.List;

/**
 * 数组声明节点
 *
 * @since 1.0
 */
public class ArrayDeclareNode extends NonTerminalNode {
    /**
     * 数组声明项列表
     * 存储数组声明项的列表
     */
    protected List<SyntaxNode> items;

    /**
     * 数组名称
     * 存储数组的名称
     */
    protected TerminalNode name;

    /**
     * 构造函数
     * 创建一个新的数组声明节点
     */
    public ArrayDeclareNode() {
        super(NonTerminal.ARRAY_DECLARE);
    }

    /**
     * 构造函数
     *
     * @param nodeType 节点类型
     */
    protected ArrayDeclareNode(NonTerminal nodeType) {
        super(nodeType);
    }

    /**
     * 创建一个模拟的数组声明节点
     *
     * @param name 数组名称
     * @return 模拟的数组声明节点
     */
    public static ArrayDeclareNode mock(TerminalNode name) {
        ArrayDeclareNode array = new ArrayDeclareNode();
        array.name = name;
        array.optimizeDelta();
        return array;
    }

    @Override
    public void optimizeBeta() {
        this.nodes.clear();
    }

    @Override
    public void optimizeDelta() {
        super.optimizeDelta();
        if (this.items != null) {
            return;
        }
        this.items = new ArrayList<>();
        for (SyntaxNode child : this.children()) {
            this.items.add(child);
        }

        if (this.name == null) {
            this.name = new TerminalNode(Terminal.ID);
            name.setToken(new Token(Terminal.ID, String.valueOf(Tool.newId()), this.location().startLine(),
                    this.location().startPosition(), this.location().startPosition() + 1));
        }
        this.addChild(name, 0);
    }

    /**
     * 获取数组声明项列表
     *
     * @return 数组声明项列表
     */
    public List<SyntaxNode> items() {
        return this.items;
    }

    @Override
    public TerminalNode declaredName() {
        return this.name;
    }

    @Override
    public boolean isMeta() {
        return this.name.nodeType() == Terminal.ARRAY_TYPE;
    }
}
