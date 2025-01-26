/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service;

import modelengine.fitframework.broker.UniqueFitableId;

/**
 * 本地进程的所有服务信息的缓存。
 *
 * @author 季聿阶
 * @since 2021-11-22
 */
public interface LocalFitableCache {
    /**
     * 判断本地进程的服务信息缓存中是否存在指定服务实现。
     *
     * @param id 表示服务实现的唯一标识的 {@link UniqueFitableId}。
     * @return 如果缓存中存在，则返回 {@code true}，否则，返回 {@code false}。
     * @throws IllegalArgumentException 当 {@code id} 为 {@code null} 时。
     */
    boolean contains(UniqueFitableId id);
}
