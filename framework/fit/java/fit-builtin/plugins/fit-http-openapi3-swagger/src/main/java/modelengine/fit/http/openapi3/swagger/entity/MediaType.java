/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultMediaType;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#media-type-object">OpenAPI
 * 3.1.0</a> 文档中的媒体格式信息。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public interface MediaType extends Serializable {
    /**
     * 获取媒体格式的名字。
     *
     * @return 表示媒体格式名字的 {@link String}。
     */
    String name();

    /**
     * 获取媒体格式的样例。
     *
     * @return 表示媒体格式样例的 {@link Schema}。
     */
    Schema schema();

    /**
     * 表示 {@link MediaType} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置媒体格式的名字。
         *
         * @param name 表示待设置的媒体格式名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 向当前构建器中设置媒体格式的样例。
         *
         * @param schema 表示待设置的媒体格式样例的 {@link Schema}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder schema(Schema schema);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link MediaType}。
         */
        MediaType build();
    }

    /**
     * 获取 {@link MediaType} 的构建器。
     *
     * @return 表示 {@link MediaType} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultMediaType.Builder();
    }
}
