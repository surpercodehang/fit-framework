/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

/**
 * safe块节点
 *
 * @since 1.0
 */
public class SafeBlockNode extends NonTerminalNode {
    private BlockNode block;

    public SafeBlockNode() {
        super(NonTerminal.SAFE_BLOCK);
    }

    @Override
    public void optimizeGama() {
        super.optimizeGama();
        this.block = ObjectUtils.cast(this.child(1));
    }

    /**
     * 获取safe块节点
     *
     * @return 返回块节点
     */
    public BlockNode block() {
        return this.block;
    }
}
