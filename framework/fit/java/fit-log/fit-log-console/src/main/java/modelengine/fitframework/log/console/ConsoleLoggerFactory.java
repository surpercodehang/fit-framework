/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log.console;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.log.LoggerFactory;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示 {@link LoggerFactory} 的控制台实现。
 *
 * @author 季聿阶
 * @since 2023-06-13
 */
public class ConsoleLoggerFactory implements LoggerFactory {
    private Logger.Level level = Logger.Level.INFO;
    private final Map<String, Logger> loggers = new ConcurrentHashMap<>();

    @Override
    public void initialize(Config config, ClassLoader frameworkClassLoader) {
        String initialLevel = config.get("logging.level", String.class);
        if (StringUtils.isNotBlank(initialLevel)) {
            this.level = Logger.Level.from(initialLevel);
        }
    }

    @Override
    public Logger getLogger(Class<?> clazz) {
        return this.getLogger(clazz == null ? "ROOT" : clazz.getName());
    }

    @Override
    public Logger getLogger(String name) {
        String actualName = StringUtils.isBlank(name) ? "ROOT" : name;
        return this.loggers.computeIfAbsent(actualName, key -> new ConsoleLogger(key, this.level));
    }

    @Override
    public void setGlobalLevel(Logger.Level level) {
        this.level = ObjectUtils.nullIf(level, Logger.Level.NONE);
        for (Logger logger : this.loggers.values()) {
            logger.setLevel(this.level);
        }
    }

    @Override
    public void setLevels(String basePackage, Logger.Level level) {
        Logger.Level actualLevel = ObjectUtils.nullIf(level, Logger.Level.NONE);
        for (Logger logger : this.loggers.values()) {
            if (StringUtils.equalsIgnoreCase(logger.name(), basePackage)
                    || StringUtils.startsWithIgnoreCase(logger.name(), basePackage + ".")) {
                logger.setLevel(actualLevel);
            }
        }
    }
}
