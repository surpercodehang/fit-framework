/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.log;

/**
 * 表示日志级别的修改器。
 *
 * @author 季聿阶
 * @since 2023-12-24
 */
@FunctionalInterface
public interface LoggerLevelChanger {
    /**
     * 修改日志记录器的级别。
     *
     * @param pluginName 表示待修改的日志记录器所在插件的 {@link String}。
     * @param packageName 表示待修改日志记录器的包名称的 {@link String}。
     * @param name 表示待修改日志记录器的名字的 {@link String}。
     * @param level 表示修改后的日志级别的 {@link String}。
     */
    void changeLevel(String pluginName, String packageName, String name, String level);
}
