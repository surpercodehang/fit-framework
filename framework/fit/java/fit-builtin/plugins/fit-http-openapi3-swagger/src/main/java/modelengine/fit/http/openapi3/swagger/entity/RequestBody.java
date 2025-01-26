/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultRequestBody;

import java.util.Map;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#request-body-object">OpenAPI
 * 3.1.0</a> 文档中的请求体信息。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public interface RequestBody extends Serializable {
    /**
     * 获取请求体的描述信息。
     *
     * @return 表示请求体的描述信息的 {@link String}。
     */
    String description();

    /**
     * 获取请求体的内容。
     * <p><b>【必选】</b></p>
     *
     * @return 表示请求体的内容的 {@link Map}{@code <}{@link String}{@code , }{@link MediaType}{@code >}。
     */
    Map<String, MediaType> content();

    /**
     * 获取请求体是否必选的标志。
     *
     * @return 当请求体必选时，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isRequired();

    /**
     * 表示 {@link RequestBody} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置请求体的描述信息。
         *
         * @param description 表示待设置的描述信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 向当前构建器中设置请求体的内容。
         * <p><b>【必选】</b></p>
         *
         * @param content 表示待设置的请求体的内容的 {@link Map}{@code <}{@link String}{@code , }{@link MediaType}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder content(Map<String, MediaType> content);

        /**
         * 向当前构建器中设置请求体是否必选的标志。
         *
         * @param required 表示待设置的请求体是否必选标志的 {@code boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isRequired(boolean required);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link RequestBody}。
         */
        RequestBody build();
    }

    /**
     * 获取 {@link RequestBody} 的构建器。
     *
     * @return 表示 {@link RequestBody} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultRequestBody.Builder();
    }
}
