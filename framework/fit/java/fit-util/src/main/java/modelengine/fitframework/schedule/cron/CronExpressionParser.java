/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron;

import modelengine.fitframework.schedule.cron.support.DefaultCronExpressionParser;

/**
 * 用于将 CRON 表达式解析为结构化数据的解析器。
 *
 * @author 季聿阶
 * @since 2023-01-02
 */
public interface CronExpressionParser {
    /**
     * 将 CRON 表达式解析为结构化的数据。
     *
     * @param expression 表示待解析的 CRON 表达式的 {@link String}。
     * @return 表示解析结果的 {@link CronExpression}。
     */
    CronExpression parse(String expression);

    /**
     * 创建一个 CRON 表达式的解析器。
     *
     * @return 表示创建出来的 CRON 表达式的解析器的 {@link CronExpressionParser}。
     */
    static CronExpressionParser create() {
        return DefaultCronExpressionParser.INSTANCE;
    }
}
