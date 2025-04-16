/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer;

/**
 * 符号分类枚举
 * <p>
 * 用于语义分析阶段区分不同类型的符号，与符号类型({@link modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr TypeExpr})互补，
 * 共同完成符号的类型校验和作用域管理。
 * </p>
 *
 * @since 1.0
 */
public enum Category {
    /**
     * 变量符号。
     * <p>用于表示脚本中的变量声明，包括：
     * <ul>
     *   <li>{@code var} 声明的可变变量</li>
     *   <li>{@code let} 声明的不可变变量</li>
     * </ul>
     */
    VARIABLE,
    
    /**
     * 参数符号。
     * <p>用于函数或方法的形式参数声明，包括：
     * <ul>
     *   <li>函数定义中的参数列表</li>
     *   <li>Lambda 表达式参数</li>
     * </ul>
     */
    ARGUMENT,
    
    /**
     * 函数声明符号。
     * <p>用于标识函数或方法的定义，包括：
     * <ul>
     *   <li>普通函数声明</li>
     *   <li>类成员方法声明</li>
     *   <li>系统扩展函数声明</li>
     * </ul>
     */
    FUNCTION_DECLARE
}
