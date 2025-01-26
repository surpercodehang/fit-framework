/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http;

import static modelengine.fitframework.inspection.Validation.notBlank;

/**
 * 表示 Http 请求携带的特殊属性的名字。
 *
 * @author 季聿阶
 * @since 2022-08-30
 */
public enum HttpClassicRequestAttribute {
    /** 表示 Http 请求所匹配的原始路径样式。 */
    PATH_PATTERN("fit-path-pattern"),

    /** 表示 Http 请求所匹配的处理器。 */
    HTTP_HANDLER("fit-http-handler");

    private final String key;

    /**
     * 通过 Http 请求携带的特殊属性名字来实例化 {@link HttpClassicRequestAttribute}。
     *
     * @param key 表示特殊属性的名字的 {@link String}。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 或空白字符串时。
     */
    HttpClassicRequestAttribute(String key) {
        this.key = notBlank(key, "The attribute name cannot be blank.");
    }

    /**
     * 获取属性的名字。
     *
     * @return 表示属性名字的 {@link String}。
     */
    public String key() {
        return this.key;
    }
}
