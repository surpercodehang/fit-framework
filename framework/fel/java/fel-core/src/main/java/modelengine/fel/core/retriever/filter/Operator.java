/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.core.retriever.filter;

/**
 * 表示过滤表达式的枚举。
 *
 * @author 易文渊
 * @since 2024-08-10
 */
public enum Operator {
    EQ,
    NE,
    LT,
    GT,
    LE,
    GE,
    IN,
    NIN,
    LIKE,
    OR,
    AND,
}