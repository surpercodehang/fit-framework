/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.event;

import modelengine.fitframework.broker.LocalExecutor;
import modelengine.fitframework.broker.UniqueFitableId;

/**
 * 表示本地执行器注册完毕的观察者。
 *
 * @author 季聿阶
 * @since 2023-03-25
 */
@FunctionalInterface
public interface LocalExecutorRegisteredObserver {
    /**
     * 当本地执行器注册完毕时触发。
     *
     * @param id 表示注册的本地执行器对应的服务实现的唯一标识的 {@link UniqueFitableId}。
     * @param executor 表示被注册的本地执行器的 {@link LocalExecutor}。
     */
    void onLocalExecutorRegistered(UniqueFitableId id, LocalExecutor executor);
}
