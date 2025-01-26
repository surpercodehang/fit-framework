/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 匹配变量的节点
 *
 * @since 1.0
 */
public class MatchVarNode extends NonTerminalNode {
    public MatchVarNode() {
        super(NonTerminal.MATCH_VAR);
    }

    @Override
    public void optimizeBeta() {
    }
}
