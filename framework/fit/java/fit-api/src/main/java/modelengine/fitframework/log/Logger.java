/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

/**
 * 为应用程序提供日志记录程序。
 *
 * @author 梁济时
 * @since 2022-11-14
 */
public interface Logger {
    /**
     * 获取日志记录器的名字。
     *
     * @return 表示日志记录器的名字的 {@link String}。
     */
    String name();

    /**
     * 判断日志级别是否为跟踪（Trace）级别。
     *
     * @return 如果日志级别为跟踪级别，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isTraceEnabled();

    /**
     * 判断日志级别是否为调试（Debug）级别。
     *
     * @return 如果日志级别为调试级别，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isDebugEnabled();

    /**
     * 判断日志级别是否为信息（Info）级别。
     *
     * @return 如果日志级别为信息级别，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isInfoEnabled();

    /**
     * 判断日志级别是否为警告（Warn）级别。
     *
     * @return 如果日志级别为警告级别，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isWarnEnabled();

    /**
     * 判断日志级别是否为错误（Error）级别。
     *
     * @return 如果日志级别为错误级别，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isErrorEnabled();

    /**
     * 获取当前日志记录器的日志级别。
     *
     * @return 表示日志级别的 {@link Level}。
     */
    Level getLevel();

    /**
     * 设置当前日志记录器的日志级别。
     *
     * @param level 表示待设置的日志级别的 {@link Level}。
     */
    void setLevel(Level level);

    /**
     * 输出跟踪日志。
     *
     * @param format 表示日志信息的格式化字符串的 {@link String}。
     * @param args 表示格式化参数的 {@link Object}{@code []}。
     */
    void trace(String format, Object... args);

    /**
     * 输入跟踪日志。
     *
     * @param message 表示日志信息的 {@link String}。
     * @param error 表示异常信息的 {@link Throwable}。
     */
    void trace(String message, Throwable error);

    /**
     * 输出调试日志。
     *
     * @param format 表示日志信息的格式化字符串的 {@link String}。
     * @param args 表示格式化参数的 {@link Object}{@code []}。
     */
    void debug(String format, Object... args);

    /**
     * 输入调试日志。
     *
     * @param message 表示日志信息的 {@link String}。
     * @param error 表示异常信息的 {@link Throwable}。
     */
    void debug(String message, Throwable error);

    /**
     * 输出信息日志。
     *
     * @param format 表示日志信息的格式化字符串的 {@link String}。
     * @param args 表示格式化参数的 {@link Object}{@code []}。
     */
    void info(String format, Object... args);

    /**
     * 输入信息日志。
     *
     * @param message 表示日志信息的 {@link String}。
     * @param error 表示异常信息的 {@link Throwable}。
     */
    void info(String message, Throwable error);

    /**
     * 输出告警日志。
     *
     * @param format 表示日志信息的格式化字符串的 {@link String}。
     * @param args 表示格式化参数的 {@link Object}{@code []}。
     */
    void warn(String format, Object... args);

    /**
     * 输入告警日志。
     *
     * @param message 表示日志信息的 {@link String}。
     * @param error 表示异常信息的 {@link Throwable}。
     */
    void warn(String message, Throwable error);

    /**
     * 输出错误日志。
     *
     * @param format 表示日志信息的格式化字符串的 {@link String}。
     * @param args 表示格式化参数的 {@link Object}{@code []}。
     */
    void error(String format, Object... args);

    /**
     * 输入错误日志。
     *
     * @param message 表示日志信息的 {@link String}。
     * @param error 表示异常信息的 {@link Throwable}。
     */
    void error(String message, Throwable error);

    /**
     * 获取用以记录指定类型相关日志的记录器。
     *
     * @param clazz 表示待记录日志的类型的 {@link Class}{@code <?>}。
     * @return 表示用以记录该类型相关日志的记录器的 {@link Logger}。
     */
    static Logger get(Class<?> clazz) {
        return Loggers.getFactory().getLogger(clazz);
    }

    /**
     * 获取用以记录指定名字相关日志的记录器。
     *
     * @param name 表示待记录日志的名字的 {@link String}。
     * @return 表示用以记录该名字相关日志的记录器的 {@link Logger}。
     */
    static Logger get(String name) {
        return Loggers.getFactory().getLogger(name);
    }

    /**
     * 表示日志级别。
     */
    enum Level {
        /** 表示日志级别：跟踪。 */
        TRACE(0),
        /** 表示日志级别：调试。 */
        DEBUG(1),
        /** 表示日志级别：信息。 */
        INFO(2),
        /** 表示日志级别：告警。 */
        WARN(3),
        /** 表示日志级别：错误。 */
        ERROR(4),
        /** 表示日志级别：无。 */
        NONE(Integer.MAX_VALUE);

        private final int priority;

        Level(int priority) {
            this.priority = priority;
        }

        /**
         * 获取日志级别的优先级。
         *
         * @return 表示日志级别优先级的 {@code int}。
         */
        public int priority() {
            return this.priority;
        }

        /**
         * 根据指定名字获取日志级别。
         *
         * @param name 表示指定名字的 {@link String}。
         * @return 表示对应的日志级别的 {@link Level}，如果无法对应，则获取 {@link Level#NONE}。
         */
        @Nonnull
        public static Level from(String name) {
            for (Level level : values()) {
                if (StringUtils.equalsIgnoreCase(level.name(), name)) {
                    return level;
                }
            }
            return NONE;
        }
    }
}
