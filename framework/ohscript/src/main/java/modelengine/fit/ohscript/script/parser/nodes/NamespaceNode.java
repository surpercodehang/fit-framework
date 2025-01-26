/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 命名空间节点
 *
 * @since 1.0
 */
public class NamespaceNode extends NonTerminalNode {
    public NamespaceNode() {
        super(NonTerminal.NAMESPACE);
    }
}
