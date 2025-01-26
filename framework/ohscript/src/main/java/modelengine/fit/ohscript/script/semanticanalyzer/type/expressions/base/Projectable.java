/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.SyntaxException;

/**
 * 表达式投影
 *
 * @since 1.0
 */
public interface Projectable {
    /**
     * 表达式投影
     *
     * @param origin 原始表达式
     * @param projection 投影表达式
     * @return 投影结果
     * @throws SyntaxException 语法异常
     */
    default TypeExpr project(TypeExpr origin, TypeExpr projection) throws SyntaxException {
        return projection;
    }

    /**
     * 默认表达式投影
     *
     * @param projection 投影表达式
     * @return 投影结果
     * @throws SyntaxException 语法异常
     */
    default TypeExpr project(TypeExpr projection) throws SyntaxException {
        return projection;
    }

    /**
     * 清除表达式投影
     */
    void clearProjection();
}
