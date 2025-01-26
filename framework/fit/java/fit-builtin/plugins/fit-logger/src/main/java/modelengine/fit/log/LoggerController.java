/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.log;

import modelengine.fit.http.annotation.DocumentIgnored;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestForm;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.log.support.FitChanger;
import modelengine.fit.log.support.Log4j2Changer;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 表示日志级别的动态设置器。
 *
 * @author 季聿阶
 * @since 2023-12-22
 */
@DocumentIgnored
@RequestMapping(path = "/loggers")
@Component
public class LoggerController {
    private static final Logger log = Logger.get(LoggerController.class);

    private final Map<LoggerType, LoggerLevelChanger> changers;

    public LoggerController(FitRuntime runtime) {
        this.changers = MapBuilder.<LoggerType, LoggerLevelChanger>get()
                .put(LoggerType.FIT, FitChanger.INSTANCE)
                .put(LoggerType.LOG4J2, new Log4j2Changer(runtime))
                .build();
    }

    /**
     * 将指定日志系统的日志级别调整为指定值。
     *
     * @param type 表示待调整日志系统的 {@link String}。
     * @param pluginName 表示待调整的日志记录器所在的插件的 {@link String}。
     * @param packageName 表示待调整日志记录器的包路径的 {@link String}。
     * @param name 表示待调整日志记录器的名字的 {@link String}。
     * @param level 表示调整到的日志级别的 {@link String}。
     */
    @PutMapping(path = "/levels")
    @ResponseStatus(code = HttpResponseStatus.NO_CONTENT)
    public void changeLevel(@RequestQuery(name = "type", defaultValue = "fit") String type,
            @RequestQuery(name = "plugin") String pluginName,
            @RequestQuery(name = "package", defaultValue = "") String packageName,
            @RequestQuery(name = "name", defaultValue = "") String name, @RequestForm(name = "level") String level) {
        LoggerType loggerType = LoggerType.from(type);
        this.changers.get(loggerType).changeLevel(pluginName, packageName, name, level);
        log.info("Change logger level successfully. [type={}, plugin={}, package={}, name={}, level={}]",
                type,
                pluginName,
                packageName,
                name,
                level);
    }
}
