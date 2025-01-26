/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.inspection.Nullable;

import java.util.Objects;

/**
 * 表示 Http 协议的版本。
 *
 * @author 季聿阶
 * @since 2022-07-09
 */
public enum HttpVersion {
    /** 表示 HTTP/1.0 的版本。 */
    HTTP_1_0("HTTP", 1, 0),

    /** 表示 HTTP/1.1 的版本。 */
    HTTP_1_1("HTTP", 1, 1);

    /** 表示协议。 */
    private final String protocol;

    /** 表示主版本号。 */
    private final int major;

    /** 表示次版本号。 */
    private final int minor;

    /**
     * 通过协议、主版本号和次版本号来实例化 {@link HttpVersion}。
     *
     * @param protocol 表示协议的 {@link String}。
     * @param major 表示主版本号的 {@code int}。
     * @param minor 表示次版本号的 {@code int}。
     * @throws IllegalArgumentException 当 {@code protocol} 为 {@code null} 或空白字符串时。
     * @throws IllegalArgumentException 当 {@code major} 或 {@code minor} 为负数时。
     */
    HttpVersion(String protocol, int major, int minor) {
        this.protocol =
                notBlank(protocol, "The protocol name of http version cannot be blank. [protocol={0}]", protocol);
        this.major = greaterThanOrEquals(major,
                0,
                "The major version of http version cannot be negative. [major={0}]",
                major);
        this.minor = greaterThanOrEquals(minor,
                0,
                "The minor version of http version cannot be negative. [major={0}]",
                major);
    }

    /**
     * 获取协议。
     *
     * @return 表示协议的 {@link String}。
     */
    public String protocol() {
        return this.protocol;
    }

    /**
     * 获取主版本号。
     *
     * @return 表示主版本号的 {@code int}。
     */
    public int major() {
        return this.major;
    }

    /**
     * 获取次版本号。
     *
     * @return 主版次号的 {@code int}。
     */
    public int minor() {
        return this.minor;
    }

    @Override
    public String toString() {
        return this.protocol + "/" + this.major + "." + this.minor;
    }

    /**
     * 获取指定版本的版本枚举。
     *
     * @param version 表示指定版本的 {@link String}。
     * @return 表示获取到的版本的 {@link HttpVersion}。如无匹配的版本，则返回 {@code null}。
     */
    @Nullable
    public static HttpVersion from(String version) {
        for (HttpVersion httpVersion : HttpVersion.values()) {
            if (Objects.equals(httpVersion.toString(), version)) {
                return httpVersion;
            }
        }
        return null;
    }
}
