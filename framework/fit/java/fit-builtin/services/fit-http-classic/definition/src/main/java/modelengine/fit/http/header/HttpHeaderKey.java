/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.header;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.HttpClassicRequestAttribute;

/**
 * 表示 Fit HTTP 通信头部的键。
 *
 * @author 王成
 * @since 2023-11-20
 */
public enum HttpHeaderKey {
    FIT_METADATA("FIT-Metadata"),
    FIT_GENERICABLE_VERSION("FIT-Genericable-Version"),
    FIT_DATA_FORMAT("FIT-Data-Format"),
    FIT_CODE("FIT-Code"),
    FIT_MESSAGE("FIT-Message"),
    FIT_TLV("FIT-TLV"),
    FIT_ACCESS_TOKEN("FIT-Access-Token");

    private final String value;

    /**
     * 通过 Http 头部键的特殊值来实例化 {@link HttpClassicRequestAttribute}。
     *
     * @param value 表示特殊属性的名字的 {@link String}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 或空白字符串时。
     */
    HttpHeaderKey(String value) {
        this.value = notBlank(value, "The attribute value cannot be blank.");
    }

    /**
     * 获取属性的名字。
     *
     * @return 表示属性名字的 {@link String}。
     */
    public String value() {
        return this.value;
    }
}
