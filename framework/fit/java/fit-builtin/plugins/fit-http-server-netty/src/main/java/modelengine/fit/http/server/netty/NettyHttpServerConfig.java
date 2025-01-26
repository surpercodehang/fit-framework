/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.netty;

/**
 * 表示 {@link NettyHttpClassicServer} 的启动配置。
 *
 * @author 季聿阶
 * @since 2022-07-28
 */
public interface NettyHttpServerConfig {
    /**
     * 获取业务线程池的核心线程数。
     *
     * @return 表示业务线程池的核心线程数的 {@code int}。
     */
    int getCoreThreadNum();

    /**
     * 获取业务线程池的最大线程数。
     *
     * @return 表示业务线程池的最大线程数的 {@code int}。
     */
    int getMaxThreadNum();

    /**
     * 获取业务线程池的等待队列数。
     *
     * @return 表示业务线程池的等待队列数的 {@code int}。
     */
    int getQueueCapacity();

    /**
     * 获取 Http 响应发生错误时，是否显示错误详细信息。
     *
     * @return 表示 Http 响应发生错误时，是否显示错误详细信息的 {@code boolean}。
     */
    boolean isDisplayError();
}
