/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 忽略的节点
 *
 * @since 1.0
 */
public class IgnoredNode extends NonTerminalNode {
    private String name;

    /**
     * 构造函数
     * 构造一个忽略的节点
     */
    public IgnoredNode() {
        super(NonTerminal.IGNORED);
    }

    /**
     * 设置节点名称
     *
     * @param name 节点名称
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }
}
