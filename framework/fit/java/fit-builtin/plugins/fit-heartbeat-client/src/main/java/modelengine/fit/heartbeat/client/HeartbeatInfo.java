/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.heartbeat.client;

import modelengine.fit.heartbeat.client.support.DefaultHeartbeatInfo;

/**
 * 表示心跳信息。
 *
 * @author 季聿阶
 * @since 2022-09-15
 */
public interface HeartbeatInfo {
    /**
     * 获取心跳的场景。
     *
     * @return 表示心跳场景的 {@link String}。
     */
    String scenario();

    /**
     * 获取心跳周期。
     *
     * @return 表示心跳周期的 {@code int}，单位为秒。
     */
    int period();

    /**
     * 获取心跳初始存活时间。
     *
     * @return 表示心跳初始存活时间的 {@code int}，单位为秒。
     */
    int initialExtraAliveTime();

    /**
     * 获取心跳存活时间。
     *
     * @return 表示心跳存活时间的 {@code int}，单位为秒。
     */
    int aliveTime();

    /**
     * 获取上一次同步心跳服务器的时间戳。
     *
     * @return 表示上一次同步心跳服务器的时间戳的 {@code long}。
     */
    long lastSyncTime();

    /**
     * 设置上一次同步心跳服务器的时间戳。
     *
     * @param lastSyncTime 表示上一次同步心跳服务器的时间戳的 {@code long}。
     */
    void lastSyncTime(long lastSyncTime);

    /**
     * 创建新的心跳信息。
     *
     * @param scenario 表示心跳场景的 {@link String}。
     * @param period 表示心跳周期的 {@code int}，单位为秒。
     * @param initialExtraAliveTime 表示心跳初始存活时间的 {@code int}，单位为秒。
     * @param aliveTime 表示心跳存活时间的 {@code int}，单位为秒。
     * @return 表示创建的新的心跳信息的 {@link HeartbeatInfo}。
     * @throws IllegalArgumentException 当 {@code scenario} 为 {@code null} 或空白字符串时。
     * @throws IllegalArgumentException 当 {@code period} 小于或等于 {@code 0} 时。
     * @throws IllegalArgumentException 当 {@code initialExtraAliveTime} 小于 {@code 0} 时。
     * @throws IllegalArgumentException 当 {@code aliveTime} 小于或等于 {@code 0} 时。
     */
    static HeartbeatInfo create(String scenario, int period, int initialExtraAliveTime, int aliveTime) {
        return new DefaultHeartbeatInfo(scenario, period, initialExtraAliveTime, aliveTime);
    }
}
