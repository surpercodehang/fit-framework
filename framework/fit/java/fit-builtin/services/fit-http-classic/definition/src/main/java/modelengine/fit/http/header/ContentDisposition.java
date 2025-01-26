/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import java.util.Optional;

/**
 * 表示消息头中关于显示位置的信息。
 *
 * @author 季聿阶
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-19.5.1">RFC 2616</a>
 * @since 2022-09-04
 */
public interface ContentDisposition extends HeaderValue {
    /**
     * 获取显示位置。
     *
     * @return 表示显示位置的 {@link String}。
     */
    default String dispositionType() {
        return this.value();
    }

    /**
     * 获取变量名。
     *
     * @return 表示变量名的 {@link Optional}{@code <}{@link String}{@code >}。
     */
    Optional<String> name();

    /**
     * 获取文件名。
     *
     * @return 表示文件名的 {@link Optional}{@code <}{@link String}{@code >}。
     */
    Optional<String> fileName();

    /**
     * 获取经过特定编码格式编码的文件名。
     *
     * @return 表示经过特定编码格式编码的文件名的 {@link Optional}{@code <}{@link String}{@code >}。
     */
    Optional<String> fileNameStar();
}
