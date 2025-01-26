/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.entity;

import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表示地址信息。
 *
 * @author 季聿阶
 * @since 2023-05-06
 */
public class Address {
    private String host;
    private List<Endpoint> endpoints = new ArrayList<>();

    /**
     * 获取主机地址。
     *
     * @return 表示主机地址的 {@link String}。
     */
    public String getHost() {
        return this.host;
    }

    /**
     * 设置主机地址。
     *
     * @param host 表示主机地址的 {@link String}。
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取端点地址列表。
     *
     * @return 表示端点地址列表的 {@link List}{@code <}{@link Endpoint}{@code >}。
     */
    public List<Endpoint> getEndpoints() {
        return this.endpoints;
    }

    /**
     * 设置端点地址列表。
     *
     * @param endpoints 表示端点地址列表的 {@link List}{@code <}{@link Endpoint}{@code >}。
     */
    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = getIfNull(endpoints, ArrayList::new);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || getClass() != another.getClass()) {
            return false;
        }
        Address address = (Address) another;
        return Objects.equals(host, address.host) && Objects.equals(endpoints, address.endpoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, endpoints);
    }

    @Override
    public String toString() {
        return StringUtils.format("/{\"host\": {0}, \"endpoints\": {1}/}", this.getHost(), this.getEndpoints());
    }
}
