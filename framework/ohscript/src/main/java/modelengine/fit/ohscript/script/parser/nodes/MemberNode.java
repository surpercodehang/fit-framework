/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

/**
 * 成员节点
 *
 * @since 1.0
 */
public class MemberNode extends TerminalNode {
    private final TerminalNode origin;

    /**
     * 构造函数
     *
     * @param origin 原始节点
     */
    public MemberNode(TerminalNode origin) {
        super(origin.nodeType());
        this.origin = origin;
    }

    @Override
    public boolean isPrivate() {
        return this.origin.isPrivate();
    }

    @Override
    public String lexeme() {
        return "." + this.origin.lexeme();
    }
}
