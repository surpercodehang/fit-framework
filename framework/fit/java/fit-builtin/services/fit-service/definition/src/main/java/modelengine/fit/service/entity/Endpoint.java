/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.entity;

import modelengine.fitframework.util.StringUtils;

import java.util.Objects;

/**
 * 表示端点信息。
 *
 * @author 季聿阶
 * @since 2023-05-06
 */
public class Endpoint {
    private Integer port;
    private Integer protocol;

    /**
     * 获取端口号。
     *
     * @return 表示端口号的 {@link Integer}。
     */
    public Integer getPort() {
        return this.port;
    }

    /**
     * 设置端口号。
     *
     * @param port 表示端口号的 {@link Integer}。
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 获取协议。
     *
     * @return 表示协议的 {@link Integer}。
     */
    public Integer getProtocol() {
        return this.protocol;
    }

    /**
     * 设置协议。
     *
     * @param protocol 表示协议的 {@link Integer}。
     */
    public void setProtocol(Integer protocol) {
        this.protocol = protocol;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || getClass() != another.getClass()) {
            return false;
        }
        Endpoint endpoint = (Endpoint) another;
        return Objects.equals(port, endpoint.port) && Objects.equals(protocol, endpoint.protocol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, protocol);
    }

    @Override
    public String toString() {
        return StringUtils.format("/{\"port\": {0}, \"protocol\": {1}/}", this.getPort(), this.getProtocol());
    }
}
