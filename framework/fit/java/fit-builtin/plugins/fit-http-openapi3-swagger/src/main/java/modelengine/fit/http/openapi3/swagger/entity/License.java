/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultLicense;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#license-object">OpenAPI 3.1.0
 * </a> 文档中的许可证信息。
 *
 * @author 季聿阶
 * @since 2023-08-16
 */
public interface License extends Serializable {
    /**
     * 获取许可证的名字。
     * <p><b>【必选】</b></p>
     *
     * @return 表示许可证名字的 {@link String}。
     */
    String name();

    /**
     * 获取许可证的 URL 地址信息。
     *
     * @return 表示许可证的 URL 地址信息的 {@link String}。
     */
    String url();

    /**
     * 表示 {@link License} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置许可证的名字。
         * <p><b>【必选】</b></p>
         *
         * @param name 表示待设置的许可证的名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 向当前构建器中设置许可证的 URL 地址信息。
         *
         * @param url 表示待设置的许可证的 URL 地址信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder url(String url);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link License}。
         */
        License build();
    }

    /**
     * 获取 {@link License} 的构建器。
     *
     * @return 表示 {@link License} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultLicense.Builder();
    }
}
