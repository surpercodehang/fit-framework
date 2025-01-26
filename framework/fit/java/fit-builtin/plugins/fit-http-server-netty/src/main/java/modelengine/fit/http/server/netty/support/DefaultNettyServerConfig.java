/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty.support;

import modelengine.fit.http.server.netty.NettyHttpServerConfig;
import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;

/**
 * {@link NettyHttpServerConfig} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-08-11
 */
@Component
@AcceptConfigValues("plugin.http-server.netty")
public class DefaultNettyServerConfig implements NettyHttpServerConfig {
    /**
     * 配置项：{@code 'core-thread-num'}.
     */
    private int coreThreadNum;

    /**
     * 配置项：{@code 'max-thread-num'}。
     */
    private int maxThreadNum;

    /**
     * 配置项：{@code 'queue-capacity'}.
     */
    private int queueCapacity;

    /**
     * 配置项：{@code 'display-error'}。
     */
    private boolean displayError;

    @Override
    public int getCoreThreadNum() {
        return this.coreThreadNum;
    }

    /**
     * 设置核心线程数量。
     *
     * @param coreThreadNum 表示核心线程数量的 {@code int}。
     */
    public void setCoreThreadNum(int coreThreadNum) {
        this.coreThreadNum = coreThreadNum;
    }

    @Override
    public int getMaxThreadNum() {
        return this.maxThreadNum;
    }

    /**
     * 设置最大线程数量。
     *
     * @param maxThreadNum 表示最大线程数量的 {@code int}。
     */
    public void setMaxThreadNum(int maxThreadNum) {
        this.maxThreadNum = maxThreadNum;
    }

    @Override
    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    /**
     * 设置等待队列大小。
     *
     * @param queueCapacity 表示等待队列大小的 {@code int}。
     */
    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    @Override
    public boolean isDisplayError() {
        return this.displayError;
    }

    /**
     * 设置是否显示错误信息的标志。
     *
     * @param displayError 表示是否显示错误信息标志的 {@code boolean}。
     */
    public void setDisplayError(boolean displayError) {
        this.displayError = displayError;
    }
}
