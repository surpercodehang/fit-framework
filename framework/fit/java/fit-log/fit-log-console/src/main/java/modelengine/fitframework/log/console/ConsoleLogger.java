/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log.console;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ArrayUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 表示 {@link Logger} 的控制台实现。
 * <p>该实现的日志打印级别为 {@code DEBUG}。</p>
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-11-14
 */
public class ConsoleLogger implements Logger {
    private final String name;
    private Level level;

    ConsoleLogger(String name, Level level) {
        this.name = notBlank(name, "The logger name cannot be blank.");
        this.level = nullIf(level, Level.NONE);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.isLevelEnabled(Level.TRACE);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.isLevelEnabled(Level.DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.isLevelEnabled(Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.isLevelEnabled(Level.WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.isLevelEnabled(Level.ERROR);
    }

    private boolean isLevelEnabled(Level level) {
        return this.level.priority() <= level.priority();
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(Level level) {
        this.level = nullIf(level, Level.NONE);
    }

    @Override
    public void trace(String format, Object... args) {
        if (this.isTraceEnabled()) {
            this.trace(StringUtils.format(canonicalizeFormat(format), getActualArgs(args)), getActualThrowable(args));
        }
    }

    @Override
    public void trace(String message, Throwable error) {
        if (this.isTraceEnabled()) {
            write(ConsoleColor.PURPLE, Level.TRACE.name(), this.name, message, error);
        }
    }

    @Override
    public void debug(String format, Object... args) {
        if (this.isDebugEnabled()) {
            this.debug(StringUtils.format(canonicalizeFormat(format), getActualArgs(args)), getActualThrowable(args));
        }
    }

    @Override
    public void debug(String message, Throwable error) {
        if (this.isDebugEnabled()) {
            write(ConsoleColor.AZURE, Level.DEBUG.name(), this.name, message, error);
        }
    }

    @Override
    public void info(String format, Object... args) {
        if (this.isInfoEnabled()) {
            this.info(StringUtils.format(canonicalizeFormat(format), getActualArgs(args)), getActualThrowable(args));
        }
    }

    @Override
    public void info(String message, Throwable error) {
        if (this.isInfoEnabled()) {
            write(ConsoleColor.WHITE, Level.INFO.name() + " ", this.name, message, error);
        }
    }

    @Override
    public void warn(String format, Object... args) {
        if (this.isWarnEnabled()) {
            this.warn(StringUtils.format(canonicalizeFormat(format), getActualArgs(args)), getActualThrowable(args));
        }
    }

    @Override
    public void warn(String message, Throwable error) {
        if (this.isWarnEnabled()) {
            write(ConsoleColor.YELLOW, Level.WARN.name() + " ", this.name, message, error);
        }
    }

    @Override
    public void error(String format, Object... args) {
        if (this.isErrorEnabled()) {
            this.error(StringUtils.format(canonicalizeFormat(format), getActualArgs(args)), getActualThrowable(args));
        }
    }

    @Override
    public void error(String message, Throwable error) {
        if (this.isErrorEnabled()) {
            write(ConsoleColor.RED, Level.ERROR.name(), this.name, message, error);
        }
    }

    private static Object[] getActualArgs(Object... args) {
        if (ArrayUtils.isEmpty(args)) {
            return new Object[0];
        }
        if (args[args.length - 1] instanceof Throwable) {
            Object[] actual = new Object[args.length - 1];
            System.arraycopy(args, 0, actual, 0, args.length - 1);
            return actual;
        } else {
            return args;
        }
    }

    private static Throwable getActualThrowable(Object... args) {
        if (ArrayUtils.isEmpty(args)) {
            return null;
        }
        if (args[args.length - 1] instanceof Throwable) {
            return ObjectUtils.cast(args[args.length - 1]);
        } else {
            return null;
        }
    }

    private static String canonicalizeFormat(String format) {
        StringBuilder builder = new StringBuilder(format.length() << 1);
        int index = 0;
        for (int i = 0; i < format.length(); i++) {
            char ch = format.charAt(i);
            builder.append(ch);
            if (ch == '{' && i < format.length() - 1 && format.charAt(i + 1) == '}') {
                builder.append(index++).append('}');
                i++;
            }
        }
        return builder.toString();
    }

    private static void write(ConsoleColor color, String level, String scope, String message, Throwable error) {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        String log = StringUtils.format("[{0}] [{1}] [{2}] [{3}] {4}",
                now,
                level,
                Thread.currentThread().getName(),
                scope,
                message);
        // 去除日志中的 '\r', '\b', '\n', '\f', '\t' 特殊字符，预防日志审计的抵赖的安全风险
        log = log.replaceAll("[\r\b\n\f\t]", "");
        log = color.format(log);
        System.out.println(log);
        if (error != null) {
            error.printStackTrace();
        }
    }
}
