/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.entity;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表示进程信息。
 *
 * @author 季聿阶
 * @since 2023-05-06
 */
public class Worker {
    private String id;
    private String environment;
    private List<Address> addresses = new ArrayList<>();
    private Map<String, String> extensions = new HashMap<>();

    /**
     * 获取进程的唯一标识。
     *
     * @return 表示进程的唯一标识的 {@link String}。
     */
    public String getId() {
        return this.id;
    }

    /**
     * 设置进程的唯一标识。
     *
     * @param id 表示进程的唯一标识的 {@link String}。
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取进程的环境信息。
     *
     * @return 表示进程的环境信息的 {@link String}。
     */
    public String getEnvironment() {
        return this.environment;
    }

    /**
     * 设置进程的环境信息。
     *
     * @param environment 表示进程的环境信息的 {@link String}。
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * 获取进程的所有地址列表。
     *
     * @return 表示进程的所有服务地址列表的 {@link List}{@code <}{@link Address}{@code >}。
     */
    public List<Address> getAddresses() {
        return this.addresses;
    }

    /**
     * 设置进程的所有地址列表。
     *
     * @param addresses 表示进程的所有服务地址列表的 {@link List}{@code <}{@link Address}{@code >}。
     */
    public void setAddresses(List<Address> addresses) {
        this.addresses = getIfNull(addresses, ArrayList::new);
    }

    /**
     * 获取进程的扩展信息集合。
     *
     * @return 表示进程的扩展信息集合的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public Map<String, String> getExtensions() {
        return this.extensions;
    }

    /**
     * 设置进程的扩展信息集合。
     *
     * @param extensions 表示进程的扩展信息集合的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public void setExtensions(Map<String, String> extensions) {
        this.extensions = getIfNull(extensions, Collections::emptyMap);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        Worker worker = cast(another);
        return Objects.equals(this.id, worker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return StringUtils.format("/{\"id\": {0}, \"environment\": {1}, \"addresses\": {2}, \"extensions\": {3}/}",
                this.getId(),
                this.getEnvironment(),
                this.getAddresses(),
                this.getExtensions());
    }
}
