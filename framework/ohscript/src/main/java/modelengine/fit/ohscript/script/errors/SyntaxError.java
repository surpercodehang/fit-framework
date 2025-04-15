/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.errors;

/**
 * 表达式异常枚举
 *
 * @since 1.0
 */
public enum SyntaxError {
    /**
     * 参数已经定义过
     */
    ARGUMENT_ALREADY_DEFINED,

    /**
     * 类型不匹配错误
     * <p>操作数类型不符合预期时抛出，例如：字符串与数值进行算术运算</p>
     */
    TYPE_MISMATCH,

    /**
     * 变量未定义错误
     * <p>引用未声明变量时抛出，含作用域链查找失败的情况</p>
     */
    VARIABLE_NOT_DEFINED,

    /**
     * 不可达代码错误
     * <p>在return/break等控制语句后的代码逻辑无法执行时抛出</p>
     */
    UN_REACHABLE,

    /**
     * 意外符号错误
     * <p>解析器遇到不符合语法规则的token时抛出</p>
     */
    UN_EXPECTED,

    /**
     * 常量未初始化错误
     * <p>声明为const/let的常量未赋初始值时抛出</p>
     */
    CONST_NOT_INITIALIZED,

    /**
     * 函数重复定义错误
     * <p>同一作用域内存在同名函数声明时抛出</p>
     */
    FUNCTION_ALREADY_DEFINED,

    /**
     * 函数未定义错误
     * <p>调用未声明函数且无系统方法匹配时抛出</p>
     */
    FUNCTION_NOT_DEFINED,

    /**
     * 常量修改错误
     * <p>尝试修改const声明变量时抛出</p>
     */
    CONST_MODIFIED,

    /**
     * 声明歧义错误
     * <p>同一作用域存在同名变量/函数/实体时抛出</p>
     */
    AMBIGUOUS_DECLARE,

    /**
     * 返回类型歧义错误
     * <p>函数不同分支返回不同类型且无法自动推导时抛出</p>
     */
    AMBIGUOUS_RETURN,

    /**
     * 实体重复定义错误
     * <p>同一作用域存在同名类/结构体声明时抛出</p>
     */
    ENTITY_ALREADY_DEFINED,

    /**
     * 实体成员未定义错误
     * <p>访问类/结构体未声明的属性或方法时抛出</p>
     */
    ENTITY_MEMBER_NOT_DEFINED,

    /**
     * 实体成员访问权限错误
     * <p>尝试访问private修饰的成员时抛出</p>
     */
    ENTITY_MEMBER_ACCESS_DENIED,

    /**
     * Lambda表达式命名错误
     * <p>匿名函数被显式命名时抛出</p>
     */
    LAMBDA_MUST_BE_ANONYMOUS,

    /**
     * 实体未找到错误
     * <p>引用的类/结构体未在当前作用域链中定义时抛出</p>
     */
    ENTITY_NOT_FOUND,

    /**
     * 数组类型不一致错误
     * <p>数组元素类型与声明类型不兼容时抛出</p>
     */
    ARRAY_TYPE_DIFFERENT,

    /**
     * 导入源错误
     * <p>import语句引用的模块文件不存在时抛出</p>
     */
    IMPORT_ERROR_SOURCE,

    /**
     * 导入标识符错误
     * <p>导入模块中不存在指定符号时抛出</p>
     */
    IMPORT_ERROR_ID,

    /**
     * 参数不存在错误
     * <p>函数调用参数数量不匹配时抛出</p>
     */
    ARGUMENT_NOT_EXIST,

    /**
     * 系统成员未找到错误
     * <p>访问数组.length等系统属性但类型不匹配时抛出</p>
     */
    SYSTEM_MEMBER_NOT_FOUND,

    /**
     * 语法树冲突错误
     * <p>语法树节点类型与预期不符时抛出</p>
     */
    AST_CONFLICT,

    /**
     * 扩展意外错误
     * <p>类继承/接口实现存在循环依赖时抛出</p>
     */
    EXTENSION_UPEXPECTED_ERROR,

    /**
     * 循环控制语句位置错误
     * <p>break/continue出现在非循环结构中时抛出</p>
     */
    LOOP_CONTROL_OUT_OF_LOOP
}
