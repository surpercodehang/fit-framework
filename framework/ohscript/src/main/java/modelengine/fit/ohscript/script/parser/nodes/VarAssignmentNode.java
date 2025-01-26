/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 变量赋值节点
 * var a = 1; ->in let/var assignment, the assignment expression like a=1 is an initial assignment
 * var a; a=1 ->in assignment, a=1 is a var assignment;
 * huizi 2023 06
 *
 * @since 1.0
 */
public class VarAssignmentNode extends InitialAssignmentNode {
    /**
     * 构造函数
     *
     * @param nodeType 节点类型
     */
    public VarAssignmentNode() {
        super();
        this.nodeType = NonTerminal.VAR_ASSIGNMENT;
    }

    @Override
    public void optimizeBeta() {
        this.addChild(this.parent().child(0), 0);
        this.parent().parent().replaceChild(this.parent(), this);
    }
}
