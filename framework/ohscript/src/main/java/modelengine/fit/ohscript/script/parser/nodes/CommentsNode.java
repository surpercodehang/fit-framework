/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * comments in ohscript is significant for AI function
 * comments will give LLM information to determine what code to run next
 * huizi 2023
 *
 * @since 1.0
 */
public class CommentsNode extends NonTerminalNode {
    public CommentsNode() {
        super(NonTerminal.COMMENT_STATEMENT);
    }

    @Override
    public void optimizeBeta() {
    }
}
