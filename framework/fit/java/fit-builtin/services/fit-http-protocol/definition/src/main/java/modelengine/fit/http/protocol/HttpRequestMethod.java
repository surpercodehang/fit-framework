/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import modelengine.fitframework.inspection.Nullable;

/**
 * 表示 Http 请求的方法。
 *
 * @author 季聿阶
 * @since 2022-07-07
 */
public enum HttpRequestMethod {
    /**
     * 方法名为 {@code OPTIONS}。
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-9.2">RFC 2616</a>
     */
    OPTIONS,

    /**
     * 方法名为 {@code GET}。
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-9.3">RFC 2616</a>
     */
    GET,

    /**
     * 方法名为 {@code HEAD}。
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-9.4">RFC 2616</a>
     */
    HEAD,

    /**
     * 方法名为 {@code POST}。
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-9.5">RFC 2616</a>
     */
    POST,

    /**
     * 方法名为 {@code PUT}。
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-9.6">RFC 2616</a>
     */
    PUT,

    /**
     * 方法名为 {@code DELETE}。
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-9.7">RFC 2616</a>
     */
    DELETE,

    /**
     * 方法名为 {@code TRACE}。
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-9.8">RFC 2616</a>
     */
    TRACE,

    /**
     * 方法名为 {@code CONNECT}。
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-9.9">RFC 2616</a>
     */
    CONNECT,

    /**
     * 方法名为 {@code PATCH}。
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc5789#section-2">RFC 5789</a>
     */
    PATCH;

    /**
     * 获取指定方法名的方法枚举。
     * <p>获取方法时<b>忽略大小写</b>。</p>
     *
     * @param methodName 表示指定方法名的 {@link String}。
     * @return 表示获取的方法的 {@link HttpRequestMethod}。如果无匹配的方法，则返回 {@code null}。
     */
    @Nullable
    public static HttpRequestMethod from(String methodName) {
        for (HttpRequestMethod method : HttpRequestMethod.values()) {
            if (method.name().equalsIgnoreCase(methodName)) {
                return method;
            }
        }
        return null;
    }
}
