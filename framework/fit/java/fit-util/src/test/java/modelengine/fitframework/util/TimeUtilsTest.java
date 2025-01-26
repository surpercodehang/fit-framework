/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;

/**
 * {@link TimeUtils} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-02
 */
@DisplayName("测试 TimeUtils")
class TimeUtilsTest {
    private final LocalDate localDate = LocalDate.of(2023, 2, 2);
    private final LocalTime localTime = LocalTime.of(10, 10, 10);
    private final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
    private final LocalDate localDateStart = LocalDate.of(2023, 1, 1);
    private final LocalTime localTimeStart = LocalTime.of(0, 0, 0);
    private final LocalDateTime localDateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);

    @Test
    @DisplayName("测试方法：firstTimeOfYear()")
    void testFirstTimeOfYear() {
        final TemporalAdjuster adjuster = TimeUtils.firstTimeOfYear();
        final LocalDateTime time = this.localDateTime.with(adjuster);
        assertThat(time).isEqualTo(localDateTimeStart);
    }

    @Test
    @DisplayName("测试方法：firstTimeOfMonth()")
    void testFirstTimeOfMonth() {
        final TemporalAdjuster adjuster = TimeUtils.firstTimeOfMonth();
        final LocalDateTime time = this.localDateTime.with(adjuster);
        final LocalDate localDateNext = LocalDate.of(2023, 2, 1);
        final LocalDateTime dateTimeStart = LocalDateTime.of(localDateNext, this.localTimeStart);
        assertThat(time).isEqualTo(dateTimeStart);
    }

    @Nested
    @DisplayName("测试方法：firstTimeOfDay()")
    class testFirstTimeOfDay {
        @Test
        @DisplayName("当提供日期时间对象时，返回该时间的开始时刻")
        void givenDateTimeThenReturnFirstTimeOfDay() {
            final TemporalAdjuster adjuster = TimeUtils.firstTimeOfDay();
            final LocalDateTime time = LocalDateTime.of(2023, 2, 2, 10, 10);
            final LocalDateTime adjusterDateTime = time.with(adjuster);
            final LocalDateTime dateTimeStart = LocalDateTime.of(2023, 2, 2, 0, 0);
            assertThat(adjusterDateTime).isEqualTo(dateTimeStart);
        }

        @Test
        @DisplayName("当提供日期对象时，返回该日期对象")
        void givenDateThenReturnDate() {
            final TemporalAdjuster adjuster = TimeUtils.firstTimeOfDay();
            final LocalDate date = LocalDate.of(2023, 2, 2);
            final LocalDate adjusterDate = date.with(adjuster);
            final LocalDate dateDateStart = LocalDate.of(2023, 2, 2);
            assertThat(adjusterDate).isEqualTo(dateDateStart);
        }
    }

    @Nested
    @DisplayName("测试方法：isLastMonthOfYear()")
    class testIsLastMonthOfYear {
        @Test
        @DisplayName("当提供不是一年的最后一个月时，返回 false")
        void givenNotLastMonthThenReturnFalse() {
            LocalDate date = LocalDate.of(2023, 2, 2);
            final boolean isLastMonthOfYear = TimeUtils.isLastMonthOfYear(date);
            assertThat(isLastMonthOfYear).isFalse();
        }

        @Test
        @DisplayName("当提供是一年的最后一个月时，返回 true")
        void givenLastMonthThenReturnTrue() {
            LocalDate date = LocalDate.of(2023, 12, 2);
            final boolean isLastMonthOfYear = TimeUtils.isLastMonthOfYear(date);
            assertThat(isLastMonthOfYear).isTrue();
        }
    }

    @Nested
    @DisplayName("测试方法：isLastDayOfMonth(Temporal temporal)")
    class testIsLastDayOfMonth {
        @Test
        @DisplayName("当提供不是一个月中的最后一天时，返回 false")
        void givenNotLastDayOfMonthThenReturnFalse() {
            LocalDate date = LocalDate.of(2023, 1, 2);
            final boolean isLastMonthOfYear = TimeUtils.isLastDayOfMonth(date);
            assertThat(isLastMonthOfYear).isFalse();
        }

        @DisplayName("当提供是一个月中的最后一天时，返回 true")
        @CsvSource({"2023,1,31", "2023,2,28", "2024,2,29", "2023,4,30"})
        @ParameterizedTest
        void givenLastDayOfMonthThenReturnTrue(String year, String month, String day) {
            LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
            final boolean isLastMonthOfYear = TimeUtils.isLastDayOfMonth(date);
            assertThat(isLastMonthOfYear).isTrue();
        }
    }

    @Nested
    @DisplayName("测试方法：isLastHourOfDay(Temporal temporal)")
    class testIsLastHourOfDay {
        @Test
        @DisplayName("当提供不是一天中的最后一小时，返回 false")
        void givenNotLastHourOfDayThenReturnFalse() {
            LocalTime time = LocalTime.of(0, 0, 0);
            final boolean isLastHourOfDay = TimeUtils.isLastHourOfDay(time);
            assertThat(isLastHourOfDay).isFalse();
        }

        @Test
        @DisplayName("当提供是一天中的最后一小时，返回 true")
        void givenLastHourOfDayThenReturnTrue() {
            LocalTime time = LocalTime.of(23, 0, 0);
            final boolean isLastHourOfDay = TimeUtils.isLastHourOfDay(time);
            assertThat(isLastHourOfDay).isTrue();
        }
    }

    @Nested
    @DisplayName("测试方法：isLastMinuteOfHour(Temporal temporal)")
    class testIsLastMinuteOfHour {
        @Test
        @DisplayName("当提供不是一小时中的最后一分钟时，返回 false")
        void givenNotLastMinuteOfHourThenReturnFalse() {
            LocalTime time = LocalTime.of(0, 0, 0);
            final boolean isLastMinuteOfHour = TimeUtils.isLastMinuteOfHour(time);
            assertThat(isLastMinuteOfHour).isFalse();
        }

        @Test
        @DisplayName("当提供是一小时中的最后一分钟时，返回 true")
        void givenLastMinuteOfHourThenReturnTrue() {
            LocalTime time = LocalTime.of(0, 59, 0);
            final boolean isLastMinuteOfHour = TimeUtils.isLastMinuteOfHour(time);
            assertThat(isLastMinuteOfHour).isTrue();
        }
    }

    @Nested
    @DisplayName("测试方法：isLastSecondOfMinute()")
    class testIsLastSecondOfMinute {
        @Test
        @DisplayName("当提供不是分钟的最后一秒时，返回 false")
        void givenNotLastSecondThenReturnFalse() {
            LocalTime time = LocalTime.of(0, 0, 0);
            final boolean isSecondOfMinute = TimeUtils.isLastSecondOfMinute(time);
            assertThat(isSecondOfMinute).isFalse();
        }

        @Test
        @DisplayName("当提供是分钟的最后一秒时，返回 true")
        void givenLastSecondThenReturnTrue() {
            LocalTime time = LocalTime.of(0, 0, 59);
            final boolean isSecondOfMinute = TimeUtils.isLastSecondOfMinute(time);
            assertThat(isSecondOfMinute).isTrue();
        }
    }
}
