/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

/**
 * 表示 Http 报文中的来源位置。
 *
 * @author 季聿阶
 * @since 2023-08-28
 */
public enum Source {
    /** 表示查询参数位置。 */
    QUERY {
        @Override
        public boolean isInBody() {
            return false;
        }
    },

    /** 表示请求头位置。 */
    HEADER {
        @Override
        public boolean isInBody() {
            return false;
        }
    },

    /** 表示 Cookie 位置。 */
    COOKIE {
        @Override
        public boolean isInBody() {
            return false;
        }
    },

    /** 表示路径变量位置。 */
    PATH {
        @Override
        public boolean isInBody() {
            return false;
        }
    },

    /** 表示消息体位置。 */
    BODY {
        @Override
        public boolean isInBody() {
            return true;
        }
    },

    /** 表示表单位置。 */
    FORM {
        @Override
        public boolean isInBody() {
            return true;
        }
    };

    /**
     * 判断当前位置是否属于消息体。
     *
     * @return 如果属于，返回 {@code true}，否则，返回 {@code false}。
     */
    public abstract boolean isInBody();
}
