/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.schedule.cron.CronExpression;
import modelengine.fitframework.schedule.cron.CronExpressionParser;
import modelengine.fitframework.schedule.cron.CronField;
import modelengine.fitframework.schedule.cron.CronFieldParser;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link CronExpressionParser} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-01-02
 */
public class DefaultCronExpressionParser implements CronExpressionParser {
    /** 表示 {@link CronExpressionParser} 的单例。 */
    public static final CronExpressionParser INSTANCE = new DefaultCronExpressionParser();

    private final CronFieldParser secondsParser = new SecondOfMinuteCronField.Parser();
    private final CronFieldParser minutesParser = new MinuteOfHourCronField.Parser();
    private final CronFieldParser hoursParser = new HourOfDayCronField.Parser();
    private final CronFieldParser daysParser = new DayOfMonthCronField.Parser();
    private final CronFieldParser monthsParser = new MonthOfYearCronField.Parser();
    private final CronFieldParser weeksParser = new DayOfWeekCronField.Parser();

    @Override
    public CronExpression parse(String expression) {
        notBlank(expression, "The cron expression cannot be blank.");
        List<String> fieldValues = StringUtils.split(expression, ' ', ArrayList::new, StringUtils::isNotBlank);
        Validation.equals(fieldValues.size(),
                6,
                "The cron expression must contain 6 parts. [expression={0}]",
                expression);
        CronField f1 = this.secondsParser.parse(fieldValues.get(0));
        CronField f2 = this.minutesParser.parse(fieldValues.get(1));
        CronField f3 = this.hoursParser.parse(fieldValues.get(2));
        CronField f4 = this.daysParser.parse(fieldValues.get(3));
        CronField f5 = this.monthsParser.parse(fieldValues.get(4));
        CronField f6 = this.weeksParser.parse(fieldValues.get(5));
        return new DefaultCronExpression(f1, f2, f3, f4, f5, f6);
    }
}
