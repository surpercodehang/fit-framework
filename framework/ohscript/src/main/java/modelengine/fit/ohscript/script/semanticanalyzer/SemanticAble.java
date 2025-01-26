/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 具有语义的节点
 *
 * @since 1.0
 */
public interface SemanticAble {
    /**
     * 符号化
     *
     * @param node 节点
     */
    void symbolize(SyntaxNode node);

    /**
     * 类型推导
     *
     * @param node 节点
     * @return 推导出的类型
     */
    TypeExpr typeInfer(SyntaxNode node);
}
