/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.log.support;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.log.LoggerFactory;

/**
 * 表示 {@link LoggerFactory} 的忽略日志的实现。
 *
 * @author 季聿阶
 * @since 2023-06-15
 */
public class NoOperationLoggerFactory implements LoggerFactory {
    /** 表示 {@link NoOperationLoggerFactory} 的单例。 */
    public static final NoOperationLoggerFactory INSTANCE = new NoOperationLoggerFactory();

    private NoOperationLoggerFactory() {}

    @Override
    public void initialize(Config config, ClassLoader frameworkClassLoader) {}

    @Override
    public Logger getLogger(Class<?> clazz) {
        return NoOperationLogger.INSTANCE;
    }

    @Override
    public Logger getLogger(String name) {
        return NoOperationLogger.INSTANCE;
    }

    @Override
    public void setGlobalLevel(Logger.Level level) {}

    @Override
    public void setLevels(String basePackage, Logger.Level level) {}
}
