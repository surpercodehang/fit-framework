/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron.support;

import modelengine.fitframework.schedule.cron.CronField;
import modelengine.fitframework.schedule.cron.CronFieldParser;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表示 {@link CronFieldParser} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2023-01-03
 */
public abstract class AbstractBitCronFieldParser implements CronFieldParser {
    @Override
    public CronField parse(String fieldValue) {
        CronField cronField = this.initialCronField();
        List<String> subFieldsValues = StringUtils.split(fieldValue, ',', ArrayList::new, StringUtils::isNotBlank);
        subFieldsValues.forEach(subFieldValue -> this.mergeSingleFieldValue(cronField, subFieldValue));
        return cronField;
    }

    private void mergeSingleFieldValue(CronField cronField, String fieldValue) {
        String actualFieldValue = this.convert(fieldValue);
        if (this.isWildcardCharacter(actualFieldValue)) {
            cronField.getBitSet().set(this.getMinValidValue(), this.getMaxValidValue() + 1);
            return;
        }
        if (this.containsSpecialCharacter(actualFieldValue)) {
            cronField.mergeSpecialValue(actualFieldValue);
            return;
        }
        int slashIndex = actualFieldValue.indexOf('/');
        if (slashIndex < 0) {
            this.mergeRange(cronField, actualFieldValue);
        } else {
            String rangeValue = actualFieldValue.substring(0, slashIndex);
            int interval = Integer.parseInt(actualFieldValue.substring(slashIndex + 1));
            this.mergeRangeWithInterval(cronField, rangeValue, interval);
        }
    }

    private void mergeRange(CronField cronField, String rangeValue) {
        int hyphenIndex = rangeValue.indexOf('-');
        if (hyphenIndex < 0) {
            int singleValue = Integer.parseInt(rangeValue);
            cronField.getBitSet().set(singleValue);
        } else {
            int min = Integer.parseInt(rangeValue.substring(0, hyphenIndex));
            int max = Integer.parseInt(rangeValue.substring(hyphenIndex + 1));
            cronField.getBitSet().set(min, max + 1);
        }
    }

    private void mergeRangeWithInterval(CronField cronField, String rangeValue, int interval) {
        int min;
        int max;
        int hyphenIndex = rangeValue.indexOf('-');
        if (hyphenIndex < 0) {
            if (Objects.equals(rangeValue, "*")) {
                min = 0;
            } else {
                min = Integer.parseInt(rangeValue);
            }
            max = this.getMaxValidValue();
        } else {
            min = Integer.parseInt(rangeValue.substring(0, hyphenIndex));
            max = Integer.parseInt(rangeValue.substring(hyphenIndex + 1));
        }
        for (int i = min; i <= max; i += interval) {
            cronField.getBitSet().set(i);
        }
    }

    /**
     * 初始化一个 CRON 表达式的字段。
     *
     * @return 表示初始化的 CRON 表达式的字段的 {@link CronField}。
     */
    protected abstract CronField initialCronField();

    /**
     * 将当前字段值进行转换。
     *
     * @param fieldValue 表示当前字段值的 {@link String}。
     * @return 表示转换后的字段值的 {@link String}。
     */
    protected String convert(String fieldValue) {
        return fieldValue;
    }

    /**
     * 判断当前字段值是否为一个通配符。
     *
     * @param fieldValue 表示当前字段值的 {@link String}。
     * @return 如果当前字段值是一个通配符，则返回 {@code true}，否则，返回 {@code false}。
     */
    protected boolean isWildcardCharacter(String fieldValue) {
        return Objects.equals(fieldValue, "*");
    }

    /**
     * 判断当前字段值中是否包含了特殊字段。
     *
     * @param fieldValue 表示当前字段值的 {@link String}。
     * @return 如果当前字段值内包含了特殊字段，则返回 {@code true}，否则，返回 {@code false}。
     */
    protected boolean containsSpecialCharacter(String fieldValue) {
        return false;
    }

    /**
     * 获取当前字段的最小合法值。
     *
     * @return 表示当前字段的最小合法值的 {@code int}。
     */
    protected int getMinValidValue() {
        return 0;
    }

    /**
     * 获取当前字段的最大合法值。
     *
     * @return 表示当前字段的最大合法值的 {@code int}。
     */
    protected abstract int getMaxValidValue();
}
