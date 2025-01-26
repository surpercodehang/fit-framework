/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.utils;

import modelengine.fitframework.log.Logger;

import java.util.function.Supplier;

/**
 * SleepUtil 用于睡眠或延迟操作的工具类
 *
 * @author 晏钰坤
 * @since 1.0
 */
public class SleepUtil {
    private static final Logger LOG = Logger.get(SleepUtil.class);

    /**
     * 公用的时间延迟方法
     *
     * @param millis 毫秒
     */
    public static void sleep(long millis) {
        if (millis <= 0) {
            LOG.error("Sleep time is invalid.");
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.error("Sleep get InterruptedException.");
            LOG.debug("Cause by : {}", e);
        }
    }

    /**
     * 等待stop的执行结束，如果超过了most给出的最大时间，则直接结束
     *
     * @param stop 结束条件
     * @param most 最大等待时长，ms
     */
    public static void waitUntil(Supplier<Boolean> stop, int most) {
        int time = 0;
        int step = 5;
        while (!stop.get() && time < most) {
            SleepUtil.sleep(step);
            time += step;
        }
    }
}
