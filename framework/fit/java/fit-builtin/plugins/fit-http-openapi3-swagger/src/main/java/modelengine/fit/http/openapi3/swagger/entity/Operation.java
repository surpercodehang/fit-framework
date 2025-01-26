/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultOperation;
import modelengine.fit.http.protocol.HttpRequestMethod;

import java.util.List;
import java.util.Set;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#operation-object">OpenAPI
 * 3.1.0</a> 文档中的操作信息。
 *
 * @author 季聿阶
 * @since 2023-08-21
 */
public interface Operation extends Serializable {
    /**
     * 获取操作所属的 Http 方法。
     *
     * @return 表示操作所属的 Http 方法的 {@link HttpRequestMethod}。
     */
    HttpRequestMethod method();

    /**
     * 获取操作所属的路径。
     *
     * @return 表示操作所属的路径的 {@link String}。
     */
    String path();

    /**
     * 获取操作的标签集合。
     *
     * @return 表示操作的标签集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> tags();

    /**
     * 获取操作的简短摘要。
     *
     * @return 表示操作的简短摘要的 {@link String}。
     */
    String summary();

    /**
     * 获取操作的描述信息。
     *
     * @return 表示操作的描述信息的 {@link String}。
     */
    String description();

    /**
     * 获取操作的唯一标识。
     *
     * @return 表示操作的唯一标识的 {@link String}。
     */
    String operationId();

    /**
     * 获取操作的所有参数列表。
     *
     * @return 表示操作的所有参数列表的 {@link List}{@code <}{@link Parameter}{@code >}。
     */
    List<Parameter> parameters();

    /**
     * 获取操作的请求体。
     *
     * @return 表示操作的请求体的 {@link RequestBody}。
     */
    RequestBody requestBody();

    /**
     * 获取操作的所有响应信息。
     *
     * @return 表示操作的所有响应信息的 {@link Responses}。
     */
    Responses responses();

    /**
     * 表示 {@link Operation} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置操作所属的 Http 方法。
         *
         * @param method 表示待设置的 Http 方法的 {@link HttpRequestMethod}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder method(HttpRequestMethod method);

        /**
         * 向当前构建器中设置操作所属的路径。
         *
         * @param path 表示待设置的路径的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder path(String path);

        /**
         * 向当前构建器中设置操作的标签集合。
         *
         * @param tags 表示待设置的标签集合的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tags(Set<String> tags);

        /**
         * 向当前构建器中设置操作的简短摘要。
         *
         * @param summary 表示待设置的简短摘要的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder summary(String summary);

        /**
         * 向当前构建器中设置操作的描述信息。
         *
         * @param description 表示待设置的描述信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 向当前构建器中设置操作的唯一标识。
         *
         * @param operationId 表示待设置的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder operationId(String operationId);

        /**
         * 向当前构建器中设置操作的参数列表。
         *
         * @param parameters 表示待设置的参数列表的 {@link List}{@code <}{@link Parameter}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder parameters(List<Parameter> parameters);

        /**
         * 向当前构建器中设置操作的请求体。
         *
         * @param requestBody 表示待设置的请求体的 {@link RequestBody}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder requestBody(RequestBody requestBody);

        /**
         * 向当前构建器中设置操作的所有响应信息。
         *
         * @param responses 表示待设置的所有响应信息的 {@link Responses}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder responses(Responses responses);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Operation}。
         */
        Operation build();
    }

    /**
     * 获取 {@link Operation} 的构建器。
     *
     * @return 表示 {@link Operation} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultOperation.Builder();
    }
}
