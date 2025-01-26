/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 数组访问节点
 *
 * @since 1.0
 */
public class ArrayAccessNode extends CallNode {
    private SyntaxNode index;

    private SyntaxNode array;

    /**
     * 构造函数
     * 构造一个数组访问节点，节点类型为NonTerminal.ARRAY_ACCESS
     */
    public ArrayAccessNode() {
        super(NonTerminal.ARRAY_ACCESS);
    }

    /**
     * 构造函数
     * 构造一个数组访问节点，节点类型为NonTerminal.ARRAY_ACCESS
     *
     * @param nodeType 节点类型
     */
    protected ArrayAccessNode(NonTerminal nodeType) {
        super(nodeType);
    }

    @Override
    public void optimizeGama() {
        if (this.index != null) {
            return;
        }
        super.optimizeGama();

        while (this.childCount() > 0) {
            SyntaxNode child = this.removeAt(this.childCount() - 1);
            if (child.nodeType() != Terminal.LEFT_BRACKET && child.nodeType() != Terminal.RIGHT_BRACKET) {
                index = child;
                continue;
            }
            if (child.nodeType() == Terminal.LEFT_BRACKET) {
                if (this.childCount() > 1) { // create nested array call node
                    array = new ArrayAccessNode();
                    array.refreshChildren(this.children());
                    this.nodes.clear();
                } else {
                    array = this.removeAt(0);
                }
                array.optimizeGama();
            }
        }
        this.addChild(array);
        this.addChild(index);
    }

    /**
     * 获取数组访问的数组对象
     *
     * @return 数组访问的数组对象
     */
    public SyntaxNode array() {
        return this.child(0);
    }

    /**
     * 获取数组访问的索引对象
     *
     * @return 数组访问的索引对象
     */
    public SyntaxNode index() {
        return this.index;
    }
}
