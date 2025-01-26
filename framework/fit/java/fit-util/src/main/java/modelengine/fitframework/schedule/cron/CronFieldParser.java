/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron;

/**
 * 表示 CRON 表达式的字段解析器。
 *
 * @author 季聿阶
 * @since 2023-01-03
 */
public interface CronFieldParser {
    /**
     * 将 CRON 表达式的字段值解析成结构化的数据。
     *
     * @param fieldValue 表示待解析的字段值的 {@link String}。
     * @return 表示解析后的结构化数据的 {@link CronField}。
     */
    CronField parse(String fieldValue);
}
