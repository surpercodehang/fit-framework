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
    /**
     * 通过终结节点、作用域和类型表达式来初始化 {@link ExtensionEntry} 的新实例。
     *
     * @param node 表示终结节点的 {@link TerminalNode}。
     * @param scope 表示作用域的 {@code long}。
     * @param typeExpr 表示类型表达式的 {@link ExtensionTypeExpr}。
     */
    public ExtensionEntry(TerminalNode node, long scope, ExtensionTypeExpr typeExpr) {
        super(node, scope, typeExpr);
    }
}
