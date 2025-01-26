/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron;

import static modelengine.fitframework.inspection.Validation.isFalse;
import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.schedule.support.AbstractExecutePolicy;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Optional;
import java.util.TimeZone;

/**
 * 表示 Cron 表达式的执行策略。
 *
 * @author 季聿阶
 * @see <a href="https://en.wikipedia.org/wiki/Cron">CRON</a>
 * @since 2022-11-15
 */
public class CronExecutePolicy extends AbstractExecutePolicy {
    private final CronExpression expression;
    private final ZoneId zoneId;

    public CronExecutePolicy(String expression) {
        this(expression, ZoneId.systemDefault());
    }

    public CronExecutePolicy(String expression, TimeZone timeZone) {
        this(expression, notNull(timeZone, "The time zone cannot be null.").toZoneId());
    }

    /**
     * 构造一个 Cron 表达式的执行策略。
     *
     * @param expression 表示 Cron 表达式的 {@link String}。
     * @param zoneId 表示时区唯一标识的 {@link ZoneId}。
     */
    public CronExecutePolicy(String expression, ZoneId zoneId) {
        CronExpressionParser parser = CronExpressionParser.create();
        this.expression = parser.parse(expression);
        this.zoneId = notNull(zoneId, "The zone id cannot be null.");
    }

    @Override
    public Optional<Instant> nextExecuteTime(@Nonnull Execution execution, @Nonnull Instant startTime) {
        this.validateExecutionStatus(execution.status());
        ZonedDateTime init;
        if (execution.status() == ExecutionStatus.SCHEDULING) {
            init = ZonedDateTime.ofInstant(startTime, this.zoneId);
        } else {
            Optional<Instant> lastExecuteTime = execution.lastExecuteTime();
            isTrue(lastExecuteTime.isPresent(), "The last execute time must be present.");
            isFalse(lastExecuteTime.get().isBefore(startTime),
                    "The last execute time cannot before the start time. [lastExecuteTime={0}, startTime={1}]",
                    lastExecuteTime.get(),
                    startTime);
            init = ZonedDateTime.ofInstant(lastExecuteTime.get(), this.zoneId);
        }
        return this.expression.findNextDateTime(init).map(ChronoZonedDateTime::toInstant);
    }
}
