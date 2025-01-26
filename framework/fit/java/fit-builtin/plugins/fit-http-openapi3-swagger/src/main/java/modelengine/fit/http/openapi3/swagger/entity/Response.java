/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultResponse;

import java.util.Map;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#response-object">OpenAPI
 * 3.1.0</a> 文档中的响应信息。
 *
 * @author 季聿阶
 * @since 2023-08-27
 */
public interface Response extends Serializable {
    /**
     * 获取响应中的描述信息。
     * <p><b>【必选】</b></p>
     *
     * @return 表示响应中的描述信息的 {@link String}。
     */
    String description();

    /**
     * 获取响应中的内容。
     *
     * @return 表示响应中的内容的 {@link Map}{@code <}{@link String}{@code , }{@link MediaType}{@code >}。
     */
    Map<String, MediaType> content();

    /**
     * 表示 {@link Response} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置响应的描述信息。
         * <p><b>【必选】</b></p>
         *
         * @param description 表示待设置的描述信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 向当前构建器中设置响应的内容。
         *
         * @param content 表示待设置的响应内容的 {@link Map}{@code <}{@link String}{@code , }{@link MediaType}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder content(Map<String, MediaType> content);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Response}。
         */
        Response build();
    }

    /**
     * 获取 {@link Response} 的构建器。
     *
     * @return 表示 {@link Response} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultResponse.Builder();
    }
}
