/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.entity;

import modelengine.fit.http.openapi3.swagger.Serializable;
import modelengine.fit.http.openapi3.swagger.entity.support.DefaultContact;

/**
 * 表示 <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.1.0.md#contact-object">OpenAPI 3.1.0
 * </a> 文档中的联系人信息。
 *
 * @author 季聿阶
 * @since 2023-08-16
 */
public interface Contact extends Serializable {
    /**
     * 获取联系人的名字。
     * <p><b>【必选】</b></p>
     *
     * @return 表示联系人的名字的 {@link String}。
     */
    String name();

    /**
     * 获取联系人的 URL 地址信息。
     *
     * @return 表示联系人的 URL 地址信息的 {@link String}。
     */
    String url();

    /**
     * 获取联系人的邮箱地址信息。
     *
     * @return 表示联系人的邮箱地址信息的 {@link String}。
     */
    String email();

    /**
     * 表示 {@link Contact} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置联系人的名字。
         * <p><b>【必选】</b></p>
         *
         * @param name 表示待设置的联系人的名字的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 向当前构建器中设置联系人的 URL 地址信息。
         *
         * @param url 表示待设置的联系人的 URL 地址信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder url(String url);

        /**
         * 向当前构建器中设置联系人的邮箱地址信息。
         *
         * @param email 表示待设置的联系人的邮箱地址信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder email(String email);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Contact}。
         */
        Contact build();
    }

    /**
     * 获取 {@link Contact} 的构建器。
     *
     * @return 表示 {@link Contact} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultContact.Builder();
    }
}
