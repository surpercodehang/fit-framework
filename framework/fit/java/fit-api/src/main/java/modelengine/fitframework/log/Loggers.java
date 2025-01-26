/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.log.support.NoOperationLoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 表示日志系统的帮助类。
 *
 * @author 季聿阶
 * @since 2023-06-14
 */
public class Loggers {
    private static volatile LoggerFactory factory;

    /**
     * 根据应用配置信息初始化日志系统。
     *
     * @throws IllegalArgumentException 当系统中除了默认实现以外，存在多个日志实现时。
     */
    public static void initialize() {
        initialize(null, null);
    }

    /**
     * 根据应用配置信息初始化日志系统。
     *
     * @param config 表示应用配置信息的 {@link Config}。
     * @param frameworkClassLoader 表示 FIT 框架的类加载器的 {@link ClassLoader}。
     * @throws IllegalArgumentException 当系统中除了默认实现以外，存在多个日志实现时。
     */
    public static void initialize(Config config, ClassLoader frameworkClassLoader) {
        ClassLoader classLoader = nullIf(frameworkClassLoader, Thread.currentThread().getContextClassLoader());
        Config actualConfig =
                getIfNull(config, () -> Config.fromMap(classLoader.getClass().getName(), new HashMap<>()));
        if (factory != null) {
            return;
        }
        synchronized (Loggers.class) {
            if (factory == null) {
                ServiceLoader<LoggerFactory> serviceLoader = ServiceLoader.load(LoggerFactory.class, classLoader);
                List<LoggerFactory> factories = new ArrayList<>();
                serviceLoader.forEach(factories::add);
                isTrue(factories.size() <= 1,
                        "Too many log implements in class path. Please specify the correct log implement.");
                if (factories.isEmpty()) {
                    factory = NoOperationLoggerFactory.INSTANCE;
                } else {
                    factory = factories.get(0);
                }
                factory.initialize(actualConfig, classLoader);
            }
        }
    }

    /**
     * 销毁日志系统。
     */
    public static void destroy() {
        if (factory == null) {
            return;
        }
        synchronized (Loggers.class) {
            factory = null;
        }
    }

    /**
     * 获取统一日志系统的工厂。
     *
     * @return 表示统一日志系统的工厂的 {@link LoggerFactory}。
     * @throws IllegalStateException 当日志系统的工厂还未初始化时。
     */
    public static LoggerFactory getFactory() {
        if (factory == null) {
            return NoOperationLoggerFactory.INSTANCE;
        }
        synchronized (Loggers.class) {
            return factory;
        }
    }
}
