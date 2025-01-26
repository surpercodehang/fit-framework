/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Category;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fitframework.util.ObjectUtils;

/**
 * Map条目
 *
 * @since 1.0
 */
public class MapEntry extends KnownSymbolEntry {
    /**
     * 构造函数
     *
     * @param node 术语节点
     * @param scope 作用域
     */
    public MapEntry(TerminalNode node, long scope) {
        super(node, scope, Category.VARIABLE, TypeExprFactory.createMap(ObjectUtils.cast(node.parent())));
    }
}
