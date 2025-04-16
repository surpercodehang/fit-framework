/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

import java.util.stream.Collectors;

/**
 * var语句节点
 * var语句定义的是变量
 *
 * @since 1.0
 */
public class VarStatementNode extends NonTerminalNode {
    /**
     * 构造函数，创建一个变量声明语句节点
     */
    public VarStatementNode() {
        super(NonTerminal.VAR_STATEMENT);
    }

    /**
     * 判断变量是否可变
     * 
     * @return 返回true表示变量可变
     */
    public boolean mutable() {
        return true;
    }

    @Override
    public void optimizeBeta() {
        this.removeChild(this.child(0)); // remove var/let keyword
        if (this.child(0) instanceof GeneralNode) {
            this.refreshChildren(this.child(0)
                    .children()
                    .stream()
                    .filter(c -> c instanceof NonTerminalNode)
                    .collect(Collectors.toList()));
        }
    }
}
