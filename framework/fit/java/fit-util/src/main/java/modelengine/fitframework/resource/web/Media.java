/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.resource.web;

import modelengine.fitframework.inspection.Validation;

import java.net.URL;

/**
 * 表示媒体资源的实体。
 *
 * @author 易文渊
 * @since 2024-06-06
 */
public class Media {
    private String mime;
    private String data;

    /**
     * 使用默认值来初始化 {@link Media} 的新实例。
     * <p><b>注意：</b>此构造函数只应该在反序列化时由框架自动调用。</p>
     */
    public Media() {}

    /**
     * 使用指定的媒体类型和数据来初始化 {@link Media} 的新实例。
     *
     * @param mime 表示媒体类型的 {@link String}，通用结构为 {@code type/subtype}。
     * @param data 表示媒体数据的 {@link String}，可以是 URL 或 base64 编码。
     * @throws IllegalArgumentException 当 {@code mime} 为 {@code null} 或 {@code data} 为 {@code null}、空字符串或只有空白字符时。
     */
    public Media(String mime, String data) {
        this.mime = Validation.notNull(mime, "The mime cannot be null.");
        this.data = Validation.notBlank(data, "The data cannot be blank.");
    }

    /**
     * 使用指定的 URL 来初始化 {@link Media} 的新实例。
     *
     * @param url 表示媒体资源地址的 {@link URL}。
     * @throws IllegalArgumentException 当 {@code url} 为 {@code null} 时。
     */
    public Media(URL url) {
        Validation.notNull(url, "The url cannot be null.");
        this.data = url.toString();
    }

    /**
     * 获取媒体类型。
     *
     * @return 表示媒体类型的 {@link String}，当数据为 {@link URL#toString()} 时为null。
     */
    public String getMime() {
        return this.mime;
    }

    /**
     * 设置媒体类型，只应该在反序列化时由框架自动调用。
     *
     * @param mime 表示媒体类型的 {@link String}。
     */
    public void setMime(String mime) {
        this.mime = mime;
    }

    /**
     * 获取媒体数据。
     *
     * @return 表示媒体数据url或者base64编码的 {@link String}。
     */
    public String getData() {
        return this.data;
    }

    /**
     * 设置数据，只应该在反序列化时由框架自动调用。
     *
     * @param data 表示资源地址或者媒体数据 base64 编码的 {@link String}。
     */
    public void setData(String data) {
        this.data = data;
    }
}