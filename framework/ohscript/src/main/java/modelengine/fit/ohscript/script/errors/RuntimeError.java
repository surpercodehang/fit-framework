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
