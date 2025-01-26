/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.broker.LocalExecutor;

import java.util.List;

/**
 * 表示注册服务实现的服务。
 *
 * @author 李鑫
 * @since 2021-11-29
 */
@Genericable
public interface RegisterFitableService {
    /**
     * 将指定服务实现在注册中心进行注册。
     *
     * @param fitableMetadata 表示待注册的服务列表的 {@link List}{@code <}{@link LocalExecutor}{@code >}。
     */
    void register(List<LocalExecutor> fitableMetadata);

    /**
     * 将指定服务实现在注册中心进行注销。
     *
     * @param fitableMetadata 表示待注销的服务列表的 {@link List}{@code <}{@link LocalExecutor}{@code >}。
     */
    void unregister(List<LocalExecutor> fitableMetadata);
}
