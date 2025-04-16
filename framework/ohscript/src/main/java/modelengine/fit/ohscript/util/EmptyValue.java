/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

/**
 * 特殊空值标记枚举。
 * <p>用于表示脚本引擎运行时的特殊状态值，与 {@code null} 不同，这些值具有明确的语义含义，主要用于控制流和异常状态传递。</p>
 *
 * @since 1.0
 */
public enum EmptyValue {
    /**
     * 表示无返回值。
     * <p>用于函数或表达式没有返回值的场景，类似其他语言的 {@code void}。</p>
     */
    UNIT,

    /**
     * 循环中断标记。
     * <p>用于 {@code break} 语句，表示需要中断当前循环。</p>
     */
    BREAK,

    /**
     * 循环继续标记。
     * <p>用于 {@code continue} 语句，表示跳过当前迭代继续下次循环。</p>
     */
    CONTINUE,

    /**
     * 运行时错误。
     * <p>表示脚本执行过程中发生了未捕获的异常。</p>
     */
    ERROR,

    /**
     * 未知类型占位。
     * <p>用于类型系统表示尚未确定的具体类型。</p>
     */
    UNKNOWN,

    /**
     * 空对象引用。
     * <p>等价于其他语言的 {@code null}，表示变量未指向任何对象。</p>
     */
    NULL,

    /**
     * 未定义值。
     * <p>表示变量尚未初始化或不存在，通常由作用域查找失败产生。</p>
     */
    UNDEFINED,

    /**
     * 忽略返回值。
     * <p>用于表示解释器可以安全忽略的中间计算结果。</p>
     */
    IGNORE
}
