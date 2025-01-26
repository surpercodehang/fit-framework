/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.inspection.Nonnull;

/**
 * 表示通信协议类型。
 *
 * @author 王成
 * @since 2023-11-22
 */
public enum Protocol {
    /** 表示 HTTP 协议。 */
    HTTP("http", 80),

    /** 表示 HTTPS 协议。 */
    HTTPS("https", 443),

    /** 表示 WebSocket 协议。 */
    WEB_SOCKET("ws", 80),

    /** 表示 Secure WebSocket 协议。 */
    SECURE_WEB_SOCKET("wss", 443);

    /** 表示协议名。 */
    private final String protocol;

    /** 表示端口号。 */
    private final int port;

    /**
     * 通过协议和端口号来实例化 {@link Protocol}。
     *
     * @param protocol 表示协议名的 {@link String}。
     * @param port 表示端口号的 {@code int}。
     * @throws IllegalArgumentException 当 {@code protocol} 为 {@code null} 或空白字符串时，或当 {@code port} 为负数时。
     */
    Protocol(String protocol, int port) {
        this.protocol = notBlank(protocol, "The name of protocol cannot be blank. [protocol={0}]", protocol);
        this.port = greaterThanOrEquals(port, 0, "The port number of protocol cannot be negative. [port={0}]", port);
    }

    /**
     * 获取协议名。
     *
     * @return 表示协议名的 {@link String}。
     */
    public String protocol() {
        return this.protocol;
    }

    /**
     * 获取端口号。
     *
     * @return 表示端口号的 {@code int}。
     */
    public int port() {
        return this.port;
    }

    @Override
    public String toString() {
        return this.protocol + "-" + this.port;
    }

    /**
     * 获取指定名字的协议枚举。
     * <p>当无法识别协议时，默认使用 {@link #HTTP}。</p>
     *
     * @param protocol 表示指定协议的 {@link String}。
     * @return 表示获取到的协议的 {@link Protocol}。如无匹配的版本，则返回 {@code null}。
     */
    @Nonnull
    public static Protocol from(String protocol) {
        for (Protocol one : Protocol.values()) {
            if (one.protocol().equalsIgnoreCase(protocol)) {
                return one;
            }
        }
        return HTTP;
    }
}
