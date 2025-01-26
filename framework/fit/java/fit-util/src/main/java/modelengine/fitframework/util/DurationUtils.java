/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.time.Duration;

/**
 * 为时间间隔提供工具类。
 *
 * @author 季聿阶
 * @since 2023-05-11
 */
public class DurationUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private DurationUtils() {}

    /**
     * 判断指定时间间隔，是否包含毫秒数。
     * <p>包含毫秒数指的是这段时间间隔不是 1000 毫秒的整数倍。</p>
     *
     * @param duration 表示指定时间间隔的 {@link Duration}。
     * @return 表示判断结果的 {@code boolean}。
     */
    public static boolean hasMillis(Duration duration) {
        notNull(duration, "The duration cannot be null.");
        return duration.toMillis() % 1000L != 0L;
    }
}
