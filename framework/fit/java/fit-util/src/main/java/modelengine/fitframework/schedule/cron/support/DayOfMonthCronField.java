/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.schedule.cron.CronField;
import modelengine.fitframework.util.TimeUtils;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表示每月中的天数的字段。
 *
 * @author 季聿阶
 * @since 2023-01-02
 */
public class DayOfMonthCronField extends AbstractCronField {
    private static final int MIN = 1;
    private static final int MAX = 31;
    private static final Pattern PATTERN = Pattern.compile("L-(\\d+)|L");

    private final Set<String> lastDays = new HashSet<>();

    @Override
    public void mergeSpecialValue(String specialValue) {
        Matcher matcher = PATTERN.matcher(specialValue);
        if (matcher.matches()) {
            this.lastDays.add(specialValue);
        }
    }

    @Override
    public Optional<ZonedDateTime> findCurrentOrNextTime(@Nonnull ZonedDateTime dateTime) {
        throw new UnsupportedOperationException("Unsupported to find date time through day of month field separately.");
    }

    /**
     * 将一个月中的最后天数的信息合入位图存储中。
     *
     * @param dateTime 表示待计算的日期信息的 {@link ZonedDateTime}，该信息中保存了月份信息。
     * @return 表示合并存储后的位图存储的 {@link BitSet}。
     */
    BitSet unionLastDays(ZonedDateTime dateTime) {
        BitSet bitSet = this.createDaysBitSetOfMonth(dateTime);
        ZonedDateTime lastDayOfMonth =
                dateTime.with(TemporalAdjusters.lastDayOfMonth()).with(TimeUtils.firstTimeOfDay());
        this.lastDays.forEach(lastDay -> this.unionLastDay(bitSet, lastDayOfMonth, lastDay));
        return bitSet;
    }

    private BitSet createDaysBitSetOfMonth(ZonedDateTime dateTime) {
        BitSet bitSet = cast(this.getBitSet().clone());
        int maxDaysOfMonth = TimeUtils.daysOfMonth(dateTime);
        for (int i = maxDaysOfMonth + 1; i < 31; i++) {
            bitSet.set(i, false);
        }
        return bitSet;
    }

    private void unionLastDay(BitSet bitMap, ZonedDateTime lastDayOfMonth, String lastDay) {
        ZonedDateTime lastNthDayOfMonth = lastDayOfMonth;
        Matcher matcher = PATTERN.matcher(lastDay);
        if (matcher.matches()) {
            String group = matcher.group(1);
            if (group != null) {
                int lastDayNum = Integer.parseInt(group);
                lastNthDayOfMonth = lastDayOfMonth.minusDays(lastDayNum);
            }
            int dayOfMonth = lastNthDayOfMonth.getDayOfMonth();
            bitMap.set(dayOfMonth);
        }
    }

    /**
     * 表示 {@link DayOfMonthCronField} 的字段解析器。
     */
    public static class Parser extends AbstractBitCronFieldParser {
        @Override
        protected CronField initialCronField() {
            return new DayOfMonthCronField();
        }

        @Override
        protected boolean isWildcardCharacter(String fieldValue) {
            return super.isWildcardCharacter(fieldValue) || Objects.equals(fieldValue, "?");
        }

        @Override
        protected boolean containsSpecialCharacter(String fieldValue) {
            return fieldValue.contains("L");
        }

        @Override
        protected int getMinValidValue() {
            return MIN;
        }

        @Override
        protected int getMaxValidValue() {
            // 可能超过当月的最大天数，需要在搜索过程中进行判断。
            return MAX;
        }
    }
}
