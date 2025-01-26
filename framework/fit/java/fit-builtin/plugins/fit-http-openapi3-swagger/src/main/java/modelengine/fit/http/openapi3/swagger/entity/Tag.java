/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultTag;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#tag-object">OpenAPI
 * 3.1.0</a> 文档中的标签信息。
 *
 * @author 季聿阶
 * @since 2023-08-22
 */
public interface Tag extends Serializable {
    /**
     * 获取标签的名字。
     * <p><b>【必选】</b></p>
     *
     * @return 表示标签名字的 {@link String}。
     */
    String name();

    /**
     * 获取标签的描述信息。
     *
     * @return 表示标签的描述信息的 {@link String}。
     */
    String description();

    /**
     * 表示 {@link Tag} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置标签的名字。
         * <p><b>【必选】</b></p>
         *
         * @param name 表示待设置的标签名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 向当前构建器中设置标签的描述信息。
         *
         * @param description 表示待设置的描述信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Tag}。
         */
        Tag build();
    }

    /**
     * 获取 {@link Tag} 的构建器。
     *
     * @return 表示 {@link Tag} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTag.Builder();
    }
}
