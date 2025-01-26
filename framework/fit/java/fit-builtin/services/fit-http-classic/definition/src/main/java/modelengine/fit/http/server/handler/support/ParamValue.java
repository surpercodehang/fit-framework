/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.handler.Source;
import modelengine.fitframework.pattern.builder.BuilderFactory;

/**
 * 表示 {@link RequestParam} 注解内容的数据类。
 *
 * @author 曹嘉美
 * @since 2025-01-07
 */
public interface ParamValue {
    /**
     * 获取 {@link ParamValue} 的构建器。
     *
     * @return 表示 {@link ParamValue} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 获取 {@link ParamValue} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link ParamValue}。
     * @return 表示 {@link ParamValue} 的构建器的 {@link Builder}。
     */
    static Builder custom(ParamValue value) {
        return BuilderFactory.get(ParamValue.class, Builder.class).create(value);
    }

    /**
     * 获取参数名称。
     *
     * @return 表示参数名称的 {@link String}。
     */
    String name();

    /**
     * 获取参数是否必须。
     *
     * @return 表示参数是否必须的 {@link boolean}。
     */
    boolean required();

    /**
     * 获取参数的默认值。
     *
     * @return 表示参数的默认值的 {@link String}。
     */
    String defaultValue();

    /**
     * 获取参数的来源位置。
     *
     * @return 表示参数的位置的 {@link Source}。
     */
    Source in();

    /**
     * {@link ParamValue} 的构建器。
     */
    interface Builder {
        /**
         * 设置参数名称。
         *
         * @param name 表示参数名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置参数是否必须。
         *
         * @param required 表示参数是否必须的 {@link boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder required(boolean required);

        /**
         * 设置参数的默认值。
         *
         * @param defaultValue 表示参数的默认值的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder defaultValue(String defaultValue);

        /**
         * 设置参数的来源位置。
         *
         * @param in 表示参数的位置的 {@link Source}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder in(Source in);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link ParamValue}。
         */
        ParamValue build();
    }
}
