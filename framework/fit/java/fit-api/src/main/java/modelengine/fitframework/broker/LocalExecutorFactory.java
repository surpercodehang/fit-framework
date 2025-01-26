/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fitframework.plugin.Plugin;

import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link LocalExecutor} 的工厂。
 *
 * @author 季聿阶
 * @since 2023-03-26
 */
public interface LocalExecutorFactory {
    /**
     * 根据指定的服务实现唯一标识获取本地执行器。
     *
     * @param id 表示指定服务实现的唯一标识的 {@link UniqueFitableId}。
     * @return 表示获取的本地执行器的 {@link Optional}{@code <}{@link LocalExecutor}{@code >}。
     */
    Optional<LocalExecutor> get(UniqueFitableId id);

    /**
     * 获取指定插件中的所有服务实现的列表。
     *
     * @param plugin 表示指定插件的 {@link Plugin}。
     * @param isMicro 表示是否为微服务的标记的 {@code boolean}。
     * @return 表示指定插件中的所有服务实现的列表的 {@link List}{@code <}{@link FitableMetadata}{@code >}。
     */
    List<LocalExecutor> get(Plugin plugin, boolean isMicro);
}
