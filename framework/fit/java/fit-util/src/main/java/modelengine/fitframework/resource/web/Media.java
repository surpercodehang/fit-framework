/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

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
     * 默认构造函数，只应该在反序列化时由框架自动调用。
     */
    public Media() {}

    /**
     * 使用媒体数据 base64 编码的字符串创建 {@link Media} 的实例。
     *
     * @param mime 表示媒体类型，通用结构为 {@code type/subtype} 的 {@link String}。
     * @param data 表示媒体数据url或者base64编码的 {@link String}。
     * @throws IllegalArgumentException
     * <ul>
     *     <li>当 {@code mime} 为 {@code null} 时。</li>
     *     <li>当 {@code data} 为 {@code null}、空字符串或者只有空白字符的字符串时。</li>
     * <ul/>
     */
    public Media(String mime, String data) {
        this.mime = Validation.notNull(mime, "The mime cannot be null.");
        this.data = Validation.notBlank(data, "The data cannot be blank.");
    }

    /**
     * 使用 {@link URL#toString()} 创建 {@link Media} 的实例。
     *
     * @param url 表示媒体资源地址的 {@link URL}。
     * @throws IllegalArgumentException 当 {@code url} 为 {@code null}时。
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