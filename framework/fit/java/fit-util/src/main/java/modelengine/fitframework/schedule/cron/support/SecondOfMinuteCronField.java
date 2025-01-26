/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron.support;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.schedule.cron.CronField;
import modelengine.fitframework.util.TimeUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;

/**
 * 表示每分钟中的秒数的字段。
 *
 * @author 季聿阶
 * @since 2023-01-03
 */
public class SecondOfMinuteCronField extends AbstractCronField {
    private static final int MAX = 59;

    @Override
    public Optional<ZonedDateTime> findCurrentOrNextTime(@Nonnull ZonedDateTime dateTime) {
        int secondValue = this.getBitSet().nextSetBit(dateTime.getSecond());
        if (secondValue < 0) {
            return Optional.empty();
        }
        if (secondValue == dateTime.getSecond()) {
            return Optional.of(dateTime);
        }
        ZonedDateTime target =
                dateTime.with(ChronoField.SECOND_OF_MINUTE, secondValue).with(TimeUtils.firstTimeOfSecond());
        return Optional.of(target);
    }

    /**
     * 表示 {@link SecondOfMinuteCronField} 的字段解析器。
     */
    public static class Parser extends AbstractBitCronFieldParser {
        @Override
        protected CronField initialCronField() {
            return new SecondOfMinuteCronField();
        }

        @Override
        protected int getMaxValidValue() {
            return MAX;
        }
    }
}
