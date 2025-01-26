/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 调用节点，包括方法调用、对象调用以及数组访问
 *
 * @since 1.0
 */
public abstract class CallNode extends NonTerminalNode {
    protected CallNode(NonTerminal nodeType) {
        super(nodeType);
    }

    @Override
    public void optimizeGama() {
        SyntaxNode last = this.child(this.childCount() - 1);
        if (last instanceof NonTerminalNode) { // indicate there has next array access or function call
            this.putMeIntoLast(last);
        }
    }

    /**
     * 将当前节点放入最后一个节点中，并进行优化
     *
     * @param last 最后一个节点
     */
    protected void putMeIntoLast(SyntaxNode last) {
        this.removeChild(last);
        this.parent().replaceChild(this, last);
        last.addChild(this, 0);
        last.optimizeGama();
    }
}
