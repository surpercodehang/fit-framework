/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import modelengine.fitframework.inspection.Validation;

import java.util.Objects;

/**
 * Http 媒体文件类型。
 *
 * @author 季聿阶
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6838">RFC 6838</a>
 * @since 2022-07-14
 */
public enum MimeType {
    /**
     * {@code "application/json"}
     */
    APPLICATION_JSON("application/json"),

    /**
     * {@code "application/x-www-form-urlencoded"}
     */
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),

    /**
     * {@code "application/octet-stream"}
     */
    APPLICATION_OCTET_STREAM("application/octet-stream"),

    /**
     * {@code "application/xhtml+xml"}
     */
    APPLICATION_XHTML("application/xhtml+xml"),

    /**
     * {@code "application/xml"}
     */
    APPLICATION_XML("application/xml"),

    /**
     * {@code "application/zstd"}
     */
    APPLICATION_ZSTD("application/zstd"),

    /**
     * {@code "multipart/form-data"}
     */
    MULTIPART_FORM_DATA("multipart/form-data"),

    /**
     * {@code "multipart/mixed"}
     */
    MULTIPART_MIXED("multipart/mixed"),

    /**
     * {@code "text/css"}
     */
    TEXT_CSS("text/css"),

    /**
     * {@code "text/html"}
     */
    TEXT_HTML("text/html"),

    /**
     * {@code "text/event-stream"}
     */
    TEXT_EVENT_STREAM("text/event-stream"),

    /**
     * {@code "text/plain"}
     */
    TEXT_PLAIN("text/plain"),

    /**
     * {@code "application/javascript"}
     */
    APPLICATION_JAVASCRIPT("application/javascript");

    private final String type;

    /**
     * 通过媒体文件类型来实例化 {@link MimeType}。
     *
     * @param type 表示媒体文件类型的 {@link String}。
     * @throws IllegalArgumentException 当 {@code type} 为 {@code null} 或空白字符串时。
     */
    MimeType(String type) {
        this.type = Validation.notBlank(type, "The MIME type cannot be blank.");
    }

    /**
     * 将指定类型转换成 {@link MimeType}。
     *
     * @param contentType 表示指定类型的 {@link String}。
     * @return 表示转换后的 {@link MimeType}。
     */
    public static MimeType from(String contentType) {
        for (MimeType type : MimeType.values()) {
            if (Objects.equals(contentType, type.value())) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取媒体文件类型。
     *
     * @return 表示媒体文件类型的 {@link String}。
     */
    public String value() {
        return this.type;
    }
}
