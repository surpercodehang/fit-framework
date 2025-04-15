/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.errors;

/**
 * 运行时的异常类型枚举
 *
 * @since 1.0
 */
public enum RuntimeError {
    /**
     * 类型不匹配错误
     * 当操作或赋值涉及的数据类型不兼容时抛出此错误
     */
    TYPE_MISMATCH {
        @Override
        protected Integer code() {
            return 101;
        }

        @Override
        String message() {
            return "type is not matched";
        }
    },
    /**
     * 变量未初始化错误
     * 当尝试访问一个未经初始化的变量时抛出此错误
     */
    NOT_INITIALIZED {
        @Override
        protected Integer code() {
            return 102;
        }

        @Override
        String message() {
            return "variable is not initialized";
        }
    },
    /**
     * 变量不可赋值错误
     * 当尝试修改一个只读变量的值时抛出此错误
     */
    NOT_ASSIGNABLE {
        @Override
        protected Integer code() {
            return 103;
        }

        @Override
        String message() {
            return "readonly variable can not be assigned";
        }
    },
    /**
     * 字段未找到错误
     * 当访问实体对象中不存在的字段时抛出此错误
     */
    FIELD_NOT_FOUND {
        @Override
        protected Integer code() {
            return 104;
        }

        @Override
        String message() {
            return "entity field is not found";
        }
    },
    /**
     * 非映射或数组类型错误
     * 当对非映射或数组类型的变量进行映射或数组操作时抛出此错误
     */
    NOT_MAP_OR_ARRAY {
        @Override
        protected Integer code() {
            return 105;
        }

        @Override
        String message() {
            return "variable is not map or array";
        }
    },
    /**
     * 变量未找到错误
     * 当访问未定义的变量时抛出此错误
     */
    VAR_NOT_FOUND {
        @Override
        protected Integer code() {
            return 106;
        }

        @Override
        String message() {
            return "variable is not found";
        }
    };

    /**
     * 抛出一个OhPanic异常，异常信息和异常代码由当前枚举值决定
     *
     * @throws OhPanic 当前枚举值对应的异常
     */
    public void raise() throws OhPanic {
        throw new OhPanic(this.message(), this.code());
    }

    /**
     * 获取当前枚举值对应的错误代码
     *
     * @return 错误代码
     */
    protected abstract Integer code();

    /**
     * 获取当前枚举值对应的错误信息
     *
     * @return 错误信息
     */
    abstract String message();
}
