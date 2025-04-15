/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.semanticanalyzer;

/**
 * 类型枚举
 *
 * @since 1.0
 */
public enum Type {
    /**
     * 未知类型
     */
    UNKNOWN {
        @Override
        public Integer id() {
            return -1;
        }
    },
    /**
     * 字符串类型
     */
    STRING {
        @Override
        public Integer id() {
            return -2;
        }
    },
    /**
     * 数字类型
     */
    NUMBER {
        @Override
        public Integer id() {
            return -3;
        }
    },
    /**
     * 单元类型
     */
    UNIT {
        @Override
        public Integer id() {
            return -4;
        }
    },
    /**
     * 布尔类型
     */
    BOOLEAN {
        @Override
        public Integer id() {
            return -5;
        }
    },
    /**
     * 忽略类型
     */
    IGNORE {
        @Override
        public Integer id() {
            return -6;
        }
    },
    /**
     * 外部类型
     */
    EXTERNAL {
        @Override
        public Integer id() {
            return -7;
        }
    },
    /**
     * 错误类型
     */
    ERROR {
        @Override
        public Integer id() {
            return -8;
        }
    },
    /**
     * 空类型
     */
    NULL {
        @Override
        public Integer id() {
            return -9;
        }
    },
    /**
     * 泛型类型
     */
    GENERIC {
        @Override
        public Integer id() {
            return -10;
        }
    },
    /**
     * 函数类型
     */
    FUNCTION {
        @Override
        public Integer id() {
            return -11;
        }
    },
    /**
     * 实体类型
     */
    ENTITY {
        @Override
        public Integer id() {
            return -12;
        }
    },
    /**
     * 数组类型
     */
    ARRAY {
        @Override
        public Integer id() {
            return -13;
        }
    },
    /**
     * 映射类型
     */
    MAP {
        @Override
        public Integer id() {
            return -14;
        }
    },
    /**
     * 表达式类型
     */
    EXPR {
        @Override
        public Integer id() {
            return -15;
        }
    },
    /**
     * 扩展类型
     */
    EXTENSION {
        @Override
        public Integer id() {
            return -16;
        }
    },
    /**
     * 未定义类型
     */
    UNDEFINED {
        @Override
        public Integer id() {
            return -16;
        }
    };

    /**
     * 获取类型ID
     *
     * @return 类型ID
     */
    public abstract Integer id();
}
