/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * java静态调用节点
 *
 * @since 1.0
 */
public class JavaStaticCallNode extends NonTerminalNode {
    public JavaStaticCallNode() {
        super(NonTerminal.JAVA_STATIC_CALL);
    }

    @Override
    public void optimizeBeta() {
        super.optimizeBeta();
        this.child(1).addChild(this.child(0), 0);
        this.parent().replaceChild(this, this.child(1));
    }
}
