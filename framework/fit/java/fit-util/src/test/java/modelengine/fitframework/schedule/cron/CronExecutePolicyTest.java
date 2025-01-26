/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.schedule.ExecutePolicy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 表示 {@link CronExecutePolicy} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-01-03
 */
@DisplayName("测试 CronExecutePolicy")
public class CronExecutePolicyTest {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private ExecutePolicy.Execution execution;
    private ZonedDateTime startTime;

    @BeforeEach
    void setup() {
        this.startTime =
                ZonedDateTime.of(LocalDateTime.parse("2022-12-09 05:32:45", DateTimeFormatter.ofPattern(PATTERN)),
                        ZoneId.systemDefault());
        this.execution = mock(ExecutePolicy.Execution.class);
        CronExpression.setMaxFutureYears(10);
        when(this.execution.lastExecuteTime()).thenReturn(Optional.of(ZonedDateTime.of(LocalDateTime.parse(
                "2022-12-09 06:37:06",
                DateTimeFormatter.ofPattern(PATTERN)), ZoneId.systemDefault()).toInstant()));
    }

    @Nested
    @DisplayName("状态值为 SCHEDULING 时，给定参考时间，验证不同逻辑表达式下次执行时间")
    class GivenReferenceTimeAndStatusSchedulingThenVerifyTheNextExecuteTimeInDifferentCronExpression {
        @BeforeEach
        void setup() {
            when(CronExecutePolicyTest.this.execution.status()).thenReturn(ExecutePolicy.ExecutionStatus.SCHEDULING);
        }

        @DisplayName("表达式为普通表达式场景")
        @ParameterizedTest
        @CsvSource({
                "0 * * * * *,2022-12-09 05:33:00", "30 * * * * *,2022-12-09 05:33:30",
                "0 0 * * * *,2022-12-09 06:00:00", "0 15 10 ? * *,2022-12-09 10:15:00",
                "0 0 12 * * ?,2022-12-09 12:00:00", "0 * 14 * * ?,2022-12-09 14:00:00",
                "0 15 10 15 * ?,2022-12-15 10:15:00"
        })
        void whenCronExpressionIsCommon(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }

        @DisplayName("表达式中有 ',' '#' '/' 以及 '-' 的场景")
        @ParameterizedTest
        @CsvSource({
                "'0 0 10,14,16 * * ?',2022-12-09 10:00:00", "0 0/30 9-17 * * ?,2022-12-09 09:00:00",
                "0 0-30/15 15 * * ?,2022-12-09 15:00:00", "0 0-15 14 * * ?,2022-12-09 14:00:00",
                "'0 0/5 14,18 * * ?',2022-12-09 14:00:00", "0 15 10 ? * 6#3,2022-12-17 10:15:00",
                "'0 */5 14,18 * * ?',2022-12-09 14:00:00"
        })
        void whenCronExpressionContainsPunctuationMarks(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }

        @DisplayName("表达式中有特殊符号 'L' 场景")
        @ParameterizedTest
        @CsvSource({
                "0 15 10 L * ?,2022-12-31 10:15:00", "0 15 10 ? * 6L,2022-12-31 10:15:00",
                "0 15 10 ? 3 3L,2023-03-29 10:15:00", "0 30 10 L-9 * ?,2022-12-22 10:30:00"
        })
        void whenCronExpressionContainsSpecialCharacterL(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }

        @DisplayName("表达式中有英文表示月或周的场景")
        @ParameterizedTest
        @CsvSource({
                "0 * 14 * * MON,2022-12-12 14:00:00", "'0 * 14 * * MON,SAT',2022-12-10 14:00:00",
                "0 * 14 * * MON-THU,2022-12-12 14:00:00", "0 15 10 ? MAR 3,2023-03-01 10:15:00",
                "0 15 10 ? JUN 3,2023-06-07 10:15:00"
        })
        void whenCronExpressionContainsEnglishAbbreviation(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }

        @DisplayName("表达式中包含多种逻辑场景")
        @ParameterizedTest
        @CsvSource({
                "0 15 10 ? MAR 3L,2023-03-29 10:15:00", "0 15 10 ? MAR-MAY 3L,2023-03-29 10:15:00",
                "'0 15 10 ? MAR,DEC 3L',2022-12-28 10:15:00"
        })
        void whenCronExpressionContainsManyHybridLogic(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("状态值为 EXECUTED 时，给定参考时间，验证不同逻辑表达式下次执行时间")
    class GivenReferenceTimeAndStatusExecutedThenVerifyTheNextExecuteTimeInDifferentCronExpression {
        @BeforeEach
        void setup() {
            when(CronExecutePolicyTest.this.execution.status()).thenReturn(ExecutePolicy.ExecutionStatus.EXECUTED);
        }

        @DisplayName("表达式为普通表达式场景")
        @ParameterizedTest
        @CsvSource({
                "0 * * * * *,2022-12-09 06:38:00", "30 * * * * *,2022-12-09 06:37:30",
                "0 0 * * * *,2022-12-09 07:00:00", "0 15 10 ? * *,2022-12-09 10:15:00",
                "0 0 12 * * ?,2022-12-09 12:00:00", "0 * 14 * * ?,2022-12-09 14:00:00",
                "0 15 10 15 * ?,2022-12-15 10:15:00"
        })
        void whenCronExpressionIsCommon(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }

        @DisplayName("表达式中有 ',' '#' '/' 以及 '-' 的场景")
        @ParameterizedTest
        @CsvSource({
                "'0 0 10,14,16 * * ?',2022-12-09 10:00:00", "0 0/30 9-17 * * ?,2022-12-09 09:00:00",
                "0 0-30/15 15 * * ?,2022-12-09 15:00:00", "0 0-15 14 * * ?,2022-12-09 14:00:00",
                "'0 0/5 14,18 * * ?',2022-12-09 14:00:00", "0 15 10 ? * 6#3,2022-12-17 10:15:00",
                "'0 */5 14,18 * * ?',2022-12-09 14:00:00"
        })
        void whenCronExpressionContainsPunctuationMarks(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }

        @DisplayName("表达式中有特殊符号 'L' 场景")
        @ParameterizedTest
        @CsvSource({
                "0 15 10 L * ?,2022-12-31 10:15:00", "0 15 10 ? * 6L,2022-12-31 10:15:00",
                "0 15 10 ? 3 3L,2023-03-29 10:15:00", "0 30 10 L-9 * ?,2022-12-22 10:30:00"
        })
        void whenCronExpressionContainsSpecialCharacterL(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }

        @DisplayName("表达式中有英文表示月或周的场景")
        @ParameterizedTest
        @CsvSource({
                "0 * 14 * * MON,2022-12-12 14:00:00", "'0 * 14 * * MON,SAT',2022-12-10 14:00:00",
                "0 * 14 * * MON-THU,2022-12-12 14:00:00", "0 15 10 ? MAR 3,2023-03-01 10:15:00",
                "0 15 10 ? JUN 3,2023-06-07 10:15:00"
        })
        void whenCronExpressionContainsEnglishAbbreviation(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }

        @DisplayName("表达式中包含多种逻辑场景")
        @ParameterizedTest
        @CsvSource({
                "0 15 10 ? MAR 3L,2023-03-29 10:15:00", "0 15 10 ? MAR-MAY 3L,2023-03-29 10:15:00",
                "'0 15 10 ? MAR,DEC 3L',2022-12-28 10:15:00"
        })
        void whenCronExpressionContainsManyHybridLogic(String cronExpression, String expected) {
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            Optional<Instant> optionalDateTime = executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                    CronExecutePolicyTest.this.startTime.toInstant());

            assertThat(optionalDateTime.isPresent()).isTrue();
            assertThat(CronExecutePolicyTest.this.format(optionalDateTime.get())).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("异常场景分析")
    class AnalyzeAbnormalScenarios {
        @DisplayName("给定上次执行时间比开始时间早，抛出异常")
        @Test
        void givenScheduleTimeBeforeStartTimeThenThrowException() {
            when(CronExecutePolicyTest.this.execution.lastExecuteTime()).thenReturn(Optional.of(ZonedDateTime.parse(
                    "2022-12-09T04:37:06.00Z").toInstant()));
            String cronExpression = "0 * * * * *";
            ExecutePolicy executePolicy = ExecutePolicy.cron(cronExpression);
            IllegalArgumentException illegalArgumentException =
                    catchThrowableOfType(() -> executePolicy.nextExecuteTime(CronExecutePolicyTest.this.execution,
                            CronExecutePolicyTest.this.startTime.toInstant()), IllegalArgumentException.class);
            assertThat(illegalArgumentException).isNotNull();
        }

        @DisplayName("给定表达式元素数量不为6个，抛出异常")
        @Test
        void givenCronExpressionNotContainsSixPartsThenThrowException() {
            String cronExpression = "0 * * * *";
            IllegalArgumentException illegalArgumentException =
                    catchThrowableOfType(() -> ExecutePolicy.cron(cronExpression), IllegalArgumentException.class);
            assertThat(illegalArgumentException).isNotNull();
        }
    }

    private String format(Instant instant) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        return zonedDateTime.format(DateTimeFormatter.ofPattern(PATTERN));
    }
}
