/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.heartbeat;

/**
 * 心跳服务客户端。
 *
 * @author 季聿阶
 * @since 2022-09-15
 */
public interface HeartbeatMonitor {
    /**
     * 进行心跳保活。
     *
     * @param scenario 表示心跳场景的 {@link String}，用于区分不同心跳间隔。
     * @param period 表示指定心跳场景的心跳周期的 {@code int}，单位为秒。
     * @param initialExtraAliveTime 表示第一次心跳的额外存活时间的 {@code int}，单位为秒。
     * @param aliveTime 表示心跳的存活时间的 {@code int}，单位为秒。
     * @throws IllegalArgumentException 当 {@code scenario} 为 {@code null} 或空白字符串时。
     * @throws IllegalArgumentException 当 {@code period} 小于或等于 {@code 0} 时。
     * @throws IllegalArgumentException 当 {@code initialExtraAliveTime} 小于 {@code 0} 时。
     * @throws IllegalArgumentException 当 {@code aliveTime} 小于或等于 {@code 0} 时。
     */
    void keepAlive(String scenario, int period, int initialExtraAliveTime, int aliveTime);

    /**
     * 终止心跳。
     *
     * @param scenario 表示心跳场景的 {@link String}，用于区分不同心跳间隔。
     * @throws IllegalArgumentException 当 {@code scenario} 为 {@code null} 或空白字符串时。
     */
    void terminate(String scenario);
}
