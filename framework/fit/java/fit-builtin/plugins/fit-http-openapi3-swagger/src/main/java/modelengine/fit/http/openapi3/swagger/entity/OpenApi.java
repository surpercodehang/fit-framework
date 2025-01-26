/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultOpenApi;

import java.util.List;

/**
 * 表示支持 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#openapi-object">OpenAPI
 * 3.1.0</a> 规范的文档类型。
 *
 * @author 季聿阶
 * @since 2023-08-15
 */
public interface OpenApi extends Serializable {
    /**
     * 获取 OpenAPI 规范的版本号。
     * <p><b>【必选】</b></p>
     *
     * @return 表示 OpenAPI 规范的版本号的 {@link String}。
     */
    String openapi();

    /**
     * 获取 API 文档的元数据信息。
     * <p><b>【必选】</b></p>
     *
     * @return 表示 API 文档的元数据信息的 {@link Info}。
     */
    Info info();

    /**
     * 获取所有 API 的路径信息。
     *
     * @return 表示所有 API 的路径信息的 {@link Paths}。
     */
    Paths paths();

    /**
     * 获取文档中所有的模型变量的引用。
     *
     * @return 表示文档中所有的模型变量的引用的 {@link Components}。
     */
    Components components();

    /**
     * 获取文档的标签列表。
     *
     * @return 表示文档的标签列表的 {@link List}{@code <}{@link Tag}{@code >}。
     */
    List<Tag> tags();

    /**
     * 表示 {@link OpenApi} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置 OpenAPI 规范的版本号。
         * <p><b>【必选】</b></p>
         *
         * @param openApi 表示待设置的 OpenAPI 规范的版本号的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder openapi(String openApi);

        /**
         * 向当前构建器中设置 API 文档的元数据信息。
         * <p><b>【必选】</b></p>
         *
         * @param info 表示待设置的 API 文档的元数据信息的 {@link Info}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder info(Info info);

        /**
         * 向当前构建器中设置所有 API 的路径信息。
         *
         * @param paths 表示待设置的所有 API 的路径信息的 {@link Paths}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder paths(Paths paths);

        /**
         * 向当前构建器中设置文档中所有的模型变量的引用。
         *
         * @param components 表示待设置的文档中所有的模型变量的引用的 {@link Components}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder components(Components components);

        /**
         * 向当前构建器中设置文档的标签列表。
         *
         * @param tags 表示待设置的文档的标签列表的 {@link List}{@code <}{@link Tag}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tags(List<Tag> tags);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link OpenApi}。
         */
        OpenApi build();
    }

    /**
     * 获取 {@link OpenApi} 的构建器。
     *
     * @return 表示 {@link OpenApi} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultOpenApi.Builder();
    }
}
