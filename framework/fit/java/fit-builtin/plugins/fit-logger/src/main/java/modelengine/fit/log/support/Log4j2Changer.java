/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.log.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.log.LoggerLevelChanger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;

/**
 * 表示 {@link LoggerLevelChanger} 的 Log4j2 的实现。
 *
 * @author 季聿阶
 * @since 2023-12-24
 */
public class Log4j2Changer implements LoggerLevelChanger {
    private final FitRuntime runtime;

    public Log4j2Changer(FitRuntime runtime) {
        this.runtime = notNull(runtime, "The FIT runtime cannot be null.");
    }

    @Override
    public void changeLevel(String pluginName, String packageName, String name, String level) {
        org.apache.logging.log4j.Level log4j2Level = org.apache.logging.log4j.Level.getLevel(level);
        Plugin plugin = this.runtime.plugin(pluginName)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("No plugin. [pluginName={0}]",
                        pluginName)));
        LoggerContext context = LogManager.getContext(plugin.pluginClassLoader(), false);
        if (StringUtils.isNotBlank(name)) {
            ExtendedLogger logger = context.getLogger(name);
            if (logger instanceof Logger) {
                Logger actualLogger = ObjectUtils.cast(logger);
                actualLogger.setLevel(log4j2Level);
            }
        } else {
            Configurator.setAllLevels(packageName, log4j2Level);
        }
    }
}
