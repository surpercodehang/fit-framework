/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log.log4j2;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.log.Logger;

import org.apache.logging.log4j.core.config.Configurator;

/**
 * 表示 {@link Logger} 的 Log4j2 的实现。
 *
 * @author 季聿阶
 * @since 2023-06-14
 */
public class Log4j2Logger implements Logger {
    private final org.apache.logging.log4j.Logger logger;

    Log4j2Logger(org.apache.logging.log4j.Logger logger) {
        this.logger = notNull(logger, "The apache log4j2 logger cannot be null.");
    }

    @Override
    public String name() {
        return this.logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override
    public Level getLevel() {
        return Log4j2LevelConverter.to(this.logger.getLevel());
    }

    @Override
    public void setLevel(Level level) {
        org.apache.logging.log4j.Level log4j2Level = Log4j2LevelConverter.from(level);
        Configurator.setLevel(this.logger, log4j2Level);
    }

    @Override
    public void trace(String format, Object... args) {
        this.logger.trace(format, args);
    }

    @Override
    public void trace(String message, Throwable error) {
        this.logger.trace(message, error);
    }

    @Override
    public void debug(String format, Object... args) {
        this.logger.debug(format, args);
    }

    @Override
    public void debug(String message, Throwable error) {
        this.logger.debug(message, error);
    }

    @Override
    public void info(String format, Object... args) {
        this.logger.info(format, args);
    }

    @Override
    public void info(String message, Throwable error) {
        this.logger.info(message, error);
    }

    @Override
    public void warn(String format, Object... args) {
        this.logger.warn(format, args);
    }

    @Override
    public void warn(String message, Throwable error) {
        this.logger.warn(message, error);
    }

    @Override
    public void error(String format, Object... args) {
        this.logger.error(format, args);
    }

    @Override
    public void error(String message, Throwable error) {
        this.logger.error(message, error);
    }
}
