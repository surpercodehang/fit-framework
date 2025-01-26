/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

/**
 * 表示时间的工具类。
 *
 * @author 季聿阶
 * @since 2023-01-03
 */
public class TimeUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private TimeUtils() {}

    /**
     * 获取寻找一个年份中的最早的时间点的时间转换器。
     *
     * @return 表示寻找一个年份中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfYear() {
        return temporal -> temporal.with(TemporalAdjusters.firstDayOfYear()).with(firstTimeOfDay());
    }

    /**
     * 获取寻找下一个年份中的最早的时间点的时间转换器。
     *
     * @return 表示寻找下一个年份中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfNextYear() {
        return temporal -> temporal.with(TemporalAdjusters.firstDayOfNextYear()).with(firstTimeOfDay());
    }

    /**
     * 获取寻找一个月份中的最早的时间点的时间转换器。
     *
     * @return 表示寻找一个月份中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfMonth() {
        return temporal -> temporal.with(TemporalAdjusters.firstDayOfMonth()).with(firstTimeOfDay());
    }

    /**
     * 获取寻找下一个月份中的最早的时间点的时间转换器。
     *
     * @return 表示寻找下一个月份中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfNextMonth() {
        return temporal -> temporal.with(TemporalAdjusters.firstDayOfNextMonth()).with(firstTimeOfDay());
    }

    /**
     * 获取寻找一天中的最早的时间点的时间转换器。
     *
     * @return 表示寻找一天中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfDay() {
        return temporal -> {
            if (temporal.isSupported(ChronoField.HOUR_OF_DAY)) {
                return temporal.with(ChronoField.HOUR_OF_DAY, 0).with(firstTimeOfHour());
            } else {
                return temporal.with(firstTimeOfHour());
            }
        };
    }

    /**
     * 获取寻找下一天中的最早的时间点的时间转换器。
     *
     * @return 表示寻找下一天中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfNextDay() {
        return temporal -> temporal.with(firstTimeOfDay()).plus(1, ChronoUnit.DAYS);
    }

    /**
     * 获取寻找一小时中的最早的时间点的时间转换器。
     *
     * @return 表示寻找一小时中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfHour() {
        return temporal -> {
            if (temporal.isSupported(ChronoField.MINUTE_OF_HOUR)) {
                return temporal.with(ChronoField.MINUTE_OF_HOUR, 0).with(firstTimeOfMinute());
            } else {
                return temporal.with(firstTimeOfMinute());
            }
        };
    }

    /**
     * 获取寻找下一小时中的最早的时间点的时间转换器。
     *
     * @return 表示寻找下一小时中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfNextHour() {
        return temporal -> temporal.with(firstTimeOfHour()).plus(1, ChronoUnit.HOURS);
    }

    /**
     * 获取寻找一分钟中的最早的时间点的时间转换器。
     *
     * @return 表示寻找一分钟中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfMinute() {
        return temporal -> {
            if (temporal.isSupported(ChronoField.SECOND_OF_MINUTE)) {
                return temporal.with(ChronoField.SECOND_OF_MINUTE, 0).with(firstTimeOfSecond());
            } else {
                return temporal.with(firstTimeOfSecond());
            }
        };
    }

    /**
     * 获取寻找下一分钟中的最早的时间点的时间转换器。
     *
     * @return 表示寻找下一分钟中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfNextMinute() {
        return temporal -> temporal.with(firstTimeOfMinute()).plus(1, ChronoUnit.MINUTES);
    }

    /**
     * 获取寻找一秒中的最早的时间点的时间转换器。
     *
     * @return 表示寻找一秒中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfSecond() {
        return temporal -> {
            if (temporal.isSupported(ChronoField.NANO_OF_SECOND)) {
                return temporal.with(ChronoField.NANO_OF_SECOND, 0);
            } else {
                return temporal;
            }
        };
    }

    /**
     * 获取寻找下一秒中的最早的时间点的时间转换器。
     *
     * @return 表示寻找下一秒中的最早的时间点的时间转换器的 {@link TemporalAdjuster}。
     */
    public static TemporalAdjuster firstTimeOfNextSecond() {
        return temporal -> temporal.with(firstTimeOfSecond()).plus(1, ChronoUnit.SECONDS);
    }

    /**
     * 获取指定携带月份信息的时间中的一个月的天数。
     *
     * @param temporal 表示携带指定月份的信息的时间的 {@link Temporal}。
     * @return 表示一个月中的天数的 {@code int}。
     */
    public static int daysOfMonth(Temporal temporal) {
        return temporal.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.DAY_OF_MONTH);
    }

    /**
     * 判断指定的月份是否为一年中的最后一个月。
     *
     * @param temporal 表示指定的月份的 {@link Temporal}。
     * @return 如果指定月份为一年中的最后一个月，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isLastMonthOfYear(Temporal temporal) {
        return temporal.get(ChronoField.MONTH_OF_YEAR) == 12;
    }

    /**
     * 判断指定的天是否为一月中的最后一天。
     *
     * @param temporal 表示指定的天的 {@link Temporal}。
     * @return 如果指定天为一月中的最后一个天，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isLastDayOfMonth(Temporal temporal) {
        return temporal.get(ChronoField.DAY_OF_MONTH) == temporal.with(TemporalAdjusters.lastDayOfMonth())
                .get(ChronoField.DAY_OF_MONTH);
    }

    /**
     * 判断指定的小时是否为一天中的最后一小时。
     *
     * @param temporal 表示指定的小时的 {@link Temporal}。
     * @return 如果指定小时是一天中的最后一小时，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isLastHourOfDay(Temporal temporal) {
        return temporal.get(ChronoField.HOUR_OF_DAY) == 23;
    }

    /**
     * 判断指定分钟是否为一小时中的最后一分钟。
     *
     * @param temporal 表示指定分钟的 {@link Temporal}。
     * @return 如果指定分钟是一小时中的最后一分钟，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isLastMinuteOfHour(Temporal temporal) {
        return temporal.get(ChronoField.MINUTE_OF_HOUR) == 59;
    }

    /**
     * 判断指定秒数是否为一分钟内的最后一秒。
     *
     * @param temporal 表示指定秒数的 {@link Temporal}。
     * @return 如果指定秒数是一分钟内的最后一秒，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isLastSecondOfMinute(Temporal temporal) {
        return temporal.get(ChronoField.SECOND_OF_MINUTE) == 59;
    }
}
