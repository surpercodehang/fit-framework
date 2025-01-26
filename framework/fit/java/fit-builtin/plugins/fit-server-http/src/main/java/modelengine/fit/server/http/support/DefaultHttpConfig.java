/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http.support;

import modelengine.fit.server.http.HttpConfig;
import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.conf.runtime.support.DefaultSecure;

import java.util.Optional;

/**
 * 表示配置项 {@code 'server.http'} 下的配置。
 *
 * @author 季聿阶
 * @since 2023-09-10
 */
@Component
@AcceptConfigValues("server.http")
public class DefaultHttpConfig implements HttpConfig {
    /**
     * 配置项：{@code 'enabled'}。
     */
    private Boolean isEnabled;

    /**
     * 配置项：{@code 'port'}。
     */
    private Integer port;

    /**
     * 配置项：{@code 'to-register-port'}。
     */
    private Integer toRegisterPort;

    /**
     * 配置项：{@code 'secure'}。
     */
    private DefaultSecure secure;

    /**
     * 配置项：{@code 'large-body-size'}，默认值为 8192。
     */
    private long largeBodySize = 8192L;

    /**
     * 设置端口是否打开的标志。
     *
     * @param enabled 表示端口是否打开的标志的 {@link Boolean}。
     */
    public void setEnabled(Boolean enabled) {
        this.isEnabled = enabled;
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
     * 设置去注册的端口号。
     *
     * @param toRegisterPort 表示去注册的端口号的 {@link Integer}。
     */
    public void setToRegisterPort(Integer toRegisterPort) {
        this.toRegisterPort = toRegisterPort;
    }

    /**
     * 设置安全相关的信息。
     *
     * @param secure 表示安全相关的信息的 {@link DefaultSecure}。
     */
    public void setSecure(DefaultSecure secure) {
        this.secure = secure;
    }

    /**
     * 设置巨大消息体的阈值。
     *
     * @param largeBodySize 表示巨大消息体阈值的 {@code long}。
     */
    public void setLargeBodySize(long largeBodySize) {
        this.largeBodySize = largeBodySize;
    }

    @Override
    public boolean isProtocolEnabled() {
        return this.isEnabled != null ? this.isEnabled : this.port != null;
    }

    @Override
    public Optional<Integer> port() {
        return Optional.ofNullable(this.port);
    }

    @Override
    public Optional<Integer> toRegisterPort() {
        return Optional.ofNullable(this.toRegisterPort);
    }

    @Override
    public Optional<Secure> secure() {
        return Optional.ofNullable(this.secure);
    }

    @Override
    public long largeBodySize() {
        return this.largeBodySize;
    }
}
