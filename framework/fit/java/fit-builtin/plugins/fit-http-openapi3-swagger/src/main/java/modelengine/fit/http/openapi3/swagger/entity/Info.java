/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultInfo;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#info-object">OpenAPI 3.1.0
 * </a> 文档中的元数据信息。
 *
 * @author 季聿阶
 * @since 2023-08-15
 */
public interface Info extends Serializable {
    /**
     * 获取 API 文档的标题。
     * <p><b>【必选】</b></p>
     *
     * @return 表示 API 文档的标题的 {@link String}。
     */
    String title();

    /**
     * 获取 API 文档的简短摘要。
     *
     * @return 表示 API 文档的简短摘要的 {@link String}。
     */
    String summary();

    /**
     * 获取 API 文档的描述信息。
     *
     * @return 表示 API 文档的描述信息的 {@link String}。
     */
    String description();

    /**
     * 获取 API 文档的联系人信息。
     *
     * @return 表示 API 文档的联系人信息的 {@link Contact}。
     */
    Contact contact();

    /**
     * 获取 API 文档的许可证信息。
     *
     * @return 表示 API 文档的许可证信息的 {@link License}。
     */
    License license();

    /**
     * 获取 API 文档的版本号。
     * <p><b>【必选】</b></p>
     *
     * @return 表示 API 文档的版本号的 {@link String}。
     */
    String version();

    /**
     * 表示 {@link Info} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置 API 文档的标题。
         * <p><b>【必选】</b></p>
         *
         * @param title 表示待设置的 API 文档的标题的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder title(String title);

        /**
         * 向当前构建器中设置 API 文档的简短摘要。
         *
         * @param summary 表示待设置的 API 文档的简短摘要的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder summary(String summary);

        /**
         * 向当前构建器中设置 API 文档的描述信息。
         *
         * @param description 表示待设置的 API 文档的描述信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 向当前构建器中设置 API 文档的联系人信息。
         *
         * @param contact 表示待设置的 API 文档的联系人信息的 {@link Contact}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder contact(Contact contact);

        /**
         * 向当前构建器中设置 API 文档的许可证信息。
         *
         * @param license 表示待设置的 API 文档的许可证信息的 {@link License}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder license(License license);

        /**
         * 向当前构建器中设置 API 文档的版本号。
         * <p><b>【必选】</b></p>
         *
         * @param version 表示待设置的 API 文档的版本号的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder version(String version);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Info}。
         */
        Info build();
    }

    /**
     * 获取 {@link Info} 的构建器。
     *
     * @return 表示 {@link Info} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultInfo.Builder();
    }
}
