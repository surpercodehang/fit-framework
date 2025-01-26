/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.broker.Endpoint;
import modelengine.fitframework.util.StringUtils;

import java.util.Objects;

/**
 * 表示 {@link Endpoint} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-06
 */
public class DefaultEndpoint implements Endpoint {
    private final String protocol;
    private final int protocolCode;
    private final int port;

    private DefaultEndpoint(String protocol, int protocolCode, int port) {
        this.protocol = StringUtils.toLowerCase(nullIf(protocol, StringUtils.EMPTY));
        this.protocolCode = protocolCode;
        this.port = port;
    }

    @Override
    public String protocol() {
        return this.protocol;
    }

    @Override
    public int protocolCode() {
        return this.protocolCode;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        DefaultEndpoint that = (DefaultEndpoint) another;
        return this.protocolCode == that.protocolCode && this.port == that.port && Objects.equals(this.protocol,
                that.protocol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.protocol, this.protocolCode, this.port);
    }

    @Override
    public String toString() {
        return StringUtils.format("/{\"protocol\": {0}, \"protocolCode\": {1}, \"port\": {2}/}",
                this.protocol,
                this.protocolCode,
                this.port);
    }

    /**
     * 表示 {@link Endpoint.Builder} 的默认实现。
     */
    public static class Builder implements Endpoint.Builder {
        private String protocol;
        private int protocolCode;
        private int port;

        /**
         * 使用已知的服务端点初始化 {@link Builder} 类的新实例。
         *
         * @param value 表示已知的服务端点的 {@link Endpoint}。
         */
        public Builder(Endpoint value) {
            if (value != null) {
                this.protocol = value.protocol();
                this.protocolCode = value.protocolCode();
                this.port = value.port();
            }
        }

        @Override
        public Endpoint.Builder protocol(String protocol, int code) {
            this.protocol = protocol;
            this.protocolCode = code;
            return this;
        }

        @Override
        public Endpoint.Builder port(int port) {
            this.port = port;
            return this;
        }

        @Override
        public Endpoint build() {
            return new DefaultEndpoint(this.protocol, this.protocolCode, this.port);
        }
    }
}
