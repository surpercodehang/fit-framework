/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ExtensionTypeExpr;

/**
 * 扩展的Entity条目
 *
 * @since 1.0
 */
public class ExtensionEntry extends EntityEntry {
    public ExtensionEntry(TerminalNode node, long scope, ExtensionTypeExpr typeExpr) {
        super(node, scope, typeExpr);
    }
}
