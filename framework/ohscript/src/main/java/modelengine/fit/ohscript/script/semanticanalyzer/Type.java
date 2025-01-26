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
    UNKNOWN {
        @Override
        public Integer id() {
            return -1;
        }
    },
    STRING {
        @Override
        public Integer id() {
            return -2;
        }
    },
    NUMBER {
        @Override
        public Integer id() {
            return -3;
        }
    },
    UNIT {
        @Override
        public Integer id() {
            return -4;
        }
    },
    BOOLEAN {
        @Override
        public Integer id() {
            return -5;
        }
    },
    IGNORE {
        @Override
        public Integer id() {
            return -6;
        }
    },
    EXTERNAL {
        @Override
        public Integer id() {
            return -7;
        }
    },
    ERROR {
        @Override
        public Integer id() {
            return -8;
        }
    },
    NULL {
        @Override
        public Integer id() {
            return -9;
        }
    },
    GENERIC {
        @Override
        public Integer id() {
            return -10;
        }
    },
    FUNCTION {
        @Override
        public Integer id() {
            return -11;
        }
    },
    ENTITY {
        @Override
        public Integer id() {
            return -12;
        }
    },
    ARRAY {
        @Override
        public Integer id() {
            return -13;
        }
    },
    MAP {
        @Override
        public Integer id() {
            return -14;
        }
    },
    EXPR {
        @Override
        public Integer id() {
            return -15;
        }
    },
    EXTENSION {
        @Override
        public Integer id() {
            return -16;
        }
    },
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
