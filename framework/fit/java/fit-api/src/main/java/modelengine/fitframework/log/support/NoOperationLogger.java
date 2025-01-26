/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log.support;

import modelengine.fitframework.log.Logger;

/**
 * 表示 {@link Logger} 的忽略日志实现。
 *
 * @author 季聿阶
 * @since 2023-06-15
 */
public class NoOperationLogger implements Logger {
    /** 表示 {@link NoOperationLogger} 的单例。 */
    public static final Logger INSTANCE = new NoOperationLogger();

    private NoOperationLogger() {}

    @Override
    public String name() {
        return NoOperationLogger.class.getSimpleName();
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public Level getLevel() {
        return Level.NONE;
    }

    @Override
    public void setLevel(Level level) {}

    @Override
    public void trace(String format, Object... args) {}

    @Override
    public void trace(String message, Throwable error) {}

    @Override
    public void debug(String format, Object... args) {}

    @Override
    public void debug(String message, Throwable error) {}

    @Override
    public void info(String format, Object... args) {}

    @Override
    public void info(String message, Throwable error) {}

    @Override
    public void warn(String format, Object... args) {}

    @Override
    public void warn(String message, Throwable error) {}

    @Override
    public void error(String format, Object... args) {}

    @Override
    public void error(String message, Throwable error) {}
}
