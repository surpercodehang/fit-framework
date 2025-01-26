/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow;

import modelengine.fit.waterflow.domain.utils.SleepUtil;

import java.util.List;
import java.util.function.Supplier;

/**
 * ClassName
 * 简述
 *
 * @author 高诗意
 * @since 1.0
 */
public final class FlowsTestUtil {
    /**
     * 等待40ms后，给出等待列表的结果
     *
     * @param supplier 等待的list
     * @param <T> 数据类型
     * @return 返回列表本身
     */
    public static <T> List<T> waitFortyMillis(Supplier<List<T>> supplier) {
        return waitMillis(supplier, 40);
    }

    /**
     * 等待给定的时间后，给出等待列表的结果
     *
     * @param supplier 等待的list
     * @param millis 等待的时长ms
     * @param <T> 数据类型
     * @return 返回列表本身
     */
    public static <T> List<T> waitMillis(Supplier<List<T>> supplier, long millis) {
        SleepUtil.sleep(millis);
        return supplier.get();
    }

    /**
     * 等待列表中的元素为空
     *
     * @param supplier 等待的list
     * @param <T> 数据类型
     * @return 返回列表本身
     */
    public static <T> List<T> waitEmpty(Supplier<List<T>> supplier) {
        return waitSize(supplier, 0);
    }

    /**
     * 等待列表中有了一个元素
     *
     * @param supplier 等待的list
     * @param <T> 数据类型
     * @return 返回列表本身
     */
    public static <T> List<T> waitSingle(Supplier<List<T>> supplier) {
        return waitSize(supplier, 1);
    }

    /**
     * 等待列表中有了给定数量的元素
     *
     * @param supplier 等待的list
     * @param size 元素的数量
     * @param <T> 数据类型
     * @return 返回列表本身
     */
    public static <T> List<T> waitSize(Supplier<List<T>> supplier, int size) {
        while (true) {
            List<T> ts = supplier.get();
            if (ts.size() == size) {
                return ts;
            }
            SleepUtil.sleep(5);
        }
    }

    /**
     * 等待stop条件满足，最大等待most ms
     *
     * @param stop 条件
     * @param most 最大等待时间 ms
     */
    public static void waitUntil(Supplier<Boolean> stop, int most) {
        int time = 0;
        int step = 5;
        while (!stop.get() && time < most) {
            SleepUtil.sleep(step);
            time += step;
        }
    }

    /**
     * 等待stop条件满足，最大等待500ms
     *
     * @param stop 条件
     */
    public static void waitUntil(Supplier<Boolean> stop) {
        waitUntil(stop, 500);
    }
}