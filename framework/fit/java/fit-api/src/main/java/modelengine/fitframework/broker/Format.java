/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fitframework.broker.support.DefaultFormat;

/**
 * 服务提供的序列化方式。
 *
 * @author 季聿阶
 * @since 2024-01-22
 */
public interface Format {
    /**
     * 获取序列化的名字。
     *
     * @return 表示序列化名字的 {@link String}。
     */
    String name();

    /**
     * 获取序列化的编码。
     *
     * @return 表示序列化编码的 {@code int}。
     */
    int code();

    /**
     * 表示 {@link Format} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置序列化的名字。
         *
         * @param name 表示待设置的序列化名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 向当前构建器中设置序列化的编码。
         *
         * @param code 表示待设置的序列化编码的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder code(int code);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Format}。
         */
        Format build();
    }

    /**
     * 获取 {@link Format} 的构建器。
     *
     * @return 表示 {@link Format} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 获取 {@link Format} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link Format}。
     * @return 表示 {@link Format} 的构建器的 {@link Builder}。
     */
    static Builder custom(Format value) {
        return new DefaultFormat.Builder(value);
    }
}
