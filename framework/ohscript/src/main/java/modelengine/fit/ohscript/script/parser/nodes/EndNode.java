/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;

/**
 * end关键字节点
 *
 * @since 1.0
 */
public class EndNode extends TerminalNode {
    /**
     * 构造函数
     * 构造一个EndNode对象，该对象代表一个end关键字
     */
    public EndNode() {
        super(Terminal.END);
    }
}
