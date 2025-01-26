/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log.log4j2;

import modelengine.fitframework.log.Logger;

import org.apache.logging.log4j.Level;

/**
 * 表示 {@link Level} 和 {@link Logger.Level} 的转换方法。
 *
 * @author 季聿阶
 * @since 2023-06-21
 */
public class Log4j2LevelConverter {
    /**
     * 将 {@link Logger.Level} 转换为 {@link Level}。
     *
     * @param level 表示待转换的日志级别的 {@link Logger.Level}。
     * @return 表示转换后的日志级别的 {@link Level}。
     */
    public static Level from(Logger.Level level) {
        if (level == null || level == Logger.Level.NONE) {
            return Level.OFF;
        }
        return Level.valueOf(level.name());
    }

    /**
     * 将 {@link Level} 转换为 {@link Logger.Level}。
     *
     * @param level 表示待转换的日志级别的 {@link Level}。
     * @return 表示转换后的日志级别的 {@link Logger.Level}。
     */
    public static Logger.Level to(Level level) {
        if (level == null) {
            return Logger.Level.NONE;
        }
        return Logger.Level.from(level.name());
    }
}
