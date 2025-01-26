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
    TYPE_MISMATCH,
    VARIABLE_NOT_DEFINED,
    UN_REACHABLE,
    UN_EXPECTED,
    CONST_NOT_INITIALIZED,
    FUNCTION_ALREADY_DEFINED,
    FUNCTION_NOT_DEFINED,
    CONST_MODIFIED,
    AMBIGUOUS_DECLARE,
    AMBIGUOUS_RETURN,
    ENTITY_ALREADY_DEFINED,
    ENTITY_MEMBER_NOT_DEFINED,
    ENTITY_MEMBER_ACCESS_DENIED,
    LAMBDA_MUST_BE_ANONYMOUS,
    ENTITY_NOT_FOUND,
    ARRAY_TYPE_DIFFERENT,
    IMPORT_ERROR_SOURCE,
    IMPORT_ERROR_ID,
    ARGUMENT_NOT_EXIST,
    SYSTEM_MEMBER_NOT_FOUND,
    AST_CONFLICT,
    EXTENSION_UPEXPECTED_ERROR,
    LOOP_CONTROL_OUT_OF_LOOP
}
