/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron.support;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.schedule.cron.CronField;
import modelengine.fitframework.util.TimeUtils;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表示每周中的天数的字段。
 *
 * @author 季聿阶
 * @since 2023-01-02
 */
public class DayOfWeekCronField extends AbstractCronField {
    private static final int MIN = 1;
    private static final int MAX = 7;
    private static final Pattern PATTERN = Pattern.compile("([1-7])L|([1-7])#([1-5])");

    private final Set<String> daysOfWeek = new HashSet<>();

    @Override
    public void mergeSpecialValue(String specialValue) {
        Matcher matcher = PATTERN.matcher(specialValue);
        if (matcher.matches()) {
            this.daysOfWeek.add(specialValue);
        }
    }

    @Override
    public Optional<ZonedDateTime> findCurrentOrNextTime(@Nonnull ZonedDateTime dateTime) {
        throw new UnsupportedOperationException("Unsupported to find date time through day of week field separately.");
    }

    /**
     * 将每周的天数的信息合入位图存储中。
     *
     * @param dateTime 表示待计算的日期信息的 {@link ZonedDateTime}，该信息中保存了月份信息。
     * @return 表示合并存储后的位图存储的 {@link BitSet}。
     */
    BitSet unionDaysOfWeek(ZonedDateTime dateTime) {
        BitSet bitSet = this.createDaysBitSetOfMonth(dateTime);
        this.daysOfWeek.forEach(dayOfWeek -> this.unionDayOfWeek(bitSet, dateTime, dayOfWeek));
        return bitSet;
    }

    private BitSet createDaysBitSetOfMonth(ZonedDateTime dateTime) {
        BitSet bitSet = new BitSet(64);
        int minDayOfMonth = 1;
        int maxDayOfMonth = TimeUtils.daysOfMonth(dateTime);
        int currentDayOfWeek = 0;
        while (currentDayOfWeek <= 7) {
            currentDayOfWeek = this.getBitSet().nextSetBit(currentDayOfWeek);
            if (currentDayOfWeek < 0) {
                break;
            }
            int currentNo = 1;
            int currentMonth = dateTime.getMonthValue();
            ZonedDateTime target =
                    dateTime.with(TemporalAdjusters.dayOfWeekInMonth(currentNo, DayOfWeek.of(currentDayOfWeek)));
            int dayOfMonth = target.getDayOfMonth();
            while (dayOfMonth >= minDayOfMonth && dayOfMonth <= maxDayOfMonth
                    && target.getMonthValue() == currentMonth) {
                bitSet.set(dayOfMonth);
                currentNo++;
                target = dateTime.with(TemporalAdjusters.dayOfWeekInMonth(currentNo, DayOfWeek.of(currentDayOfWeek)));
                dayOfMonth = target.getDayOfMonth();
            }
            currentDayOfWeek++;
        }
        return bitSet;
    }

    private void unionDayOfWeek(BitSet newMap, ZonedDateTime dateTime, String dayOfWeek) {
        Matcher matcher = PATTERN.matcher(dayOfWeek);
        if (matcher.matches()) {
            ZonedDateTime target;
            if (dayOfWeek.contains("L")) {
                int dayNum = Integer.parseInt(matcher.group(1));
                target = dateTime.with(TemporalAdjusters.lastInMonth(DayOfWeek.of(dayNum)));
            } else {
                int dayNum = Integer.parseInt(matcher.group(2));
                int no = Integer.parseInt(matcher.group(3));
                target = dateTime.with(TemporalAdjusters.dayOfWeekInMonth(no, DayOfWeek.of(dayNum)));
            }
            int dayOfMonth = target.getDayOfMonth();
            newMap.set(dayOfMonth);
        }
    }

    /**
     * 表示 {@link DayOfWeekCronField} 的字段解析器。
     */
    public static class Parser extends AbstractBitCronFieldParser {
        private static final String[] DAYS = new String[] {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

        @Override
        protected CronField initialCronField() {
            return new DayOfWeekCronField();
        }

        @Override
        protected String convert(String fieldValue) {
            String result = fieldValue.toUpperCase(Locale.ROOT);
            for (int i = 0; i < DAYS.length; i++) {
                result = result.replace(DAYS[i], String.valueOf(i + 1));
            }
            return result;
        }

        @Override
        protected boolean isWildcardCharacter(String fieldValue) {
            return super.isWildcardCharacter(fieldValue) || Objects.equals(fieldValue, "?");
        }

        @Override
        protected boolean containsSpecialCharacter(String fieldValue) {
            return fieldValue.contains("L") || fieldValue.contains("#");
        }

        @Override
        protected int getMinValidValue() {
            return MIN;
        }

        @Override
        protected int getMaxValidValue() {
            return MAX;
        }
    }
}
