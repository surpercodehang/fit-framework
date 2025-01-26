/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * external的对象数据节点
 *
 * @since 1.0
 */
public class ExternalDataNode extends NonTerminalNode {
    private final Object data;

    /**
     * 构造函数
     *
     * @param data 需要持有的external对象
     */
    public ExternalDataNode(Object data) {
        super(NonTerminal.EXTERNAL_DATA);
        this.data = data;
    }

    /**
     * 获取数据对象
     *
     * @return 持有的external对象
     */
    public Object getData() {
        return this.data;
    }

    @Override
    protected boolean isNodeIgnored() {
        return false;
    }
}
