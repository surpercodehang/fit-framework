/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.parser.nodes.TupleDeclareNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

import java.util.Map;

/**
 * 元组类型表达式
 *
 * @since 1.0
 */
public class TupleTypeExpr extends EntityTypeExpr {
    /**
     * 构造函数
     *
     * @param entity 元组声明节点
     * @param members 元组成员类型表达式映射
     * 构造元组类型表达式实例，包含元组声明节点和元组成员类型表达式映射
     */
    public TupleTypeExpr(TupleDeclareNode entity, Map<String, TypeExpr> members) {
        super(entity, members);
    }
}
