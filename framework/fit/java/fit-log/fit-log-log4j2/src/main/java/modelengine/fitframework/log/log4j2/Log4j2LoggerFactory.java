/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log.log4j2;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.log.LoggerFactory;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.spi.LoggerContext;

/**
 * 表示 {@link LoggerFactory} 的 Log4j2 的实现。
 *
 * @author 季聿阶
 * @since 2023-06-14
 */
public class Log4j2LoggerFactory implements LoggerFactory {
    private volatile LoggerContext context;

    @Override
    public void initialize(Config config, ClassLoader frameworkClassLoader) {
        notNull(config, "The config to initialize log4j2 cannot be null.");
        notNull(frameworkClassLoader, "The framework classloader cannot be null.");
        String specifiedConfigFile = config.get("logging.config", String.class);
        if (StringUtils.isNotBlank(specifiedConfigFile)) {
            this.context = Configurator.initialize(null, frameworkClassLoader.getParent(), specifiedConfigFile);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(LogManager::shutdown));
    }

    @Override
    public Logger getLogger(Class<?> clazz) {
        return new Log4j2Logger(this.getContext().getLogger(clazz));
    }

    @Override
    public Logger getLogger(String name) {
        return new Log4j2Logger(this.getContext().getLogger(name));
    }

    private LoggerContext getContext() {
        if (this.context == null) {
            this.context = LogManager.getContext(LoggerFactory.class.getClassLoader(), false);
        }
        return this.context;
    }

    @Override
    public void setGlobalLevel(Logger.Level level) {
        Level log4j2Level = Log4j2LevelConverter.from(level);
        Configurator.setLevel(ObjectUtils.<String>cast(null), log4j2Level);
    }

    @Override
    public void setLevels(String basePackage, Logger.Level level) {
        Level log4j2Level = Log4j2LevelConverter.from(level);
        Configurator.setAllLevels(basePackage, log4j2Level);
    }
}
