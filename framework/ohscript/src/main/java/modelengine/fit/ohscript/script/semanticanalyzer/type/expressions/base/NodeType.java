/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * 词法分析中的节点类型
 *
 * @since 1.0
 */
public interface NodeType {
    /**
     * 解析节点
     *
     * @return 解析后的节点
     */
    SyntaxNode parse();

    /**
     * 获取节点类型名称
     *
     * @return 节点类型名称
     */
    String name();
}
