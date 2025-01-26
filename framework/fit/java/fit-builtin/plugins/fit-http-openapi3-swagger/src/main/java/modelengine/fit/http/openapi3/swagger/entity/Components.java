/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultComponents;

import java.util.Map;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#components-object">OpenAPI
 * 3.1.0</a> 文档中的所有组件信息。
 *
 * @author 季聿阶
 * @since 2023-08-25
 */
public interface Components extends Serializable {
    /**
     * 获取所有的格式样例集合。
     *
     * @return 表示所有的格式样例集合的 {@link Map}{@code <}{@link String}{@code , }{@link Schema}{@code >}。
     */
    Map<String, Schema> schemas();

    /**
     * 表示 {@link Components} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置格式样例集合。
         *
         * @param schemas 表示待设置的格式样例集合的 {@link Map}{@code <}{@link String}{@code , }{@link Schema}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder schemas(Map<String, Schema> schemas);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Components}。
         */
        Components build();
    }

    /**
     * 获取 {@link Components} 的构建器。
     *
     * @return 表示 {@link Components} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultComponents.Builder();
    }
}
