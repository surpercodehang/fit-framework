/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.util.Optional;
import java.util.Set;

/**
 * 为服务实现的本地执行器提供仓库。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-09-24
 */
public interface LocalExecutorRepository {
    /**
     * 获取仓库的注册入口。
     *
     * @return 表示注册入口的 {@link Registry}。
     */
    Registry registry();

    /**
     * 获取仓库的名称。
     *
     * @return 表示仓库名称的 {@link String}。
     */
    String name();

    /**
     * 获取仓库内的所有服务实现的本地执行器集合。
     *
     * @return 表示仓库内的所有服务实现的本地执行器集合的 {@link Set}{@code <}{@link LocalExecutor}{@code >}。
     */
    Set<LocalExecutor> executors();

    /**
     * 获取仓库内的指定服务的服务实现的本地执行器集合。
     *
     * @param id 表示指定服务的唯一标识的 {@link UniqueGenericableId}。
     * @return 仓库内的指定服务的服务实现的本地执行器集合的 {@link Set}{@code <}{@link LocalExecutor}{@code >}。
     * @throws IllegalArgumentException 当 {@code id} 为 {@code null} 时。
     */
    Set<LocalExecutor> executors(UniqueGenericableId id);

    /**
     * 获取仓库内的指定服务实现的本地执行器。
     *
     * @param id 表示指定服务实现的唯一标识的 {@link UniqueFitableId}。
     * @return 表示仓库内的指定服务实现的本地执行器的 {@link Optional}{@code <}{@link LocalExecutor}{@code >}。
     * @throws IllegalArgumentException 当 {@code id} 为 {@code null} 时。
     */
    Optional<LocalExecutor> executor(UniqueFitableId id);

    /**
     * 为本地执行器仓库提供注册入口。
     *
     * @author 梁济时
     * @author 季聿阶
     * @since 2020-09-24
     */
    @FunctionalInterface
    interface Registry {
        /**
         * 注册一个本地执行器的实例。
         * <p>当 {@code executor} 为 {@code null} 时，将移除对指定服务实现的本地执行器。</p>
         *
         * @param uniqueFitableId 表示指定服务实现的唯一标识的 {@link UniqueFitableId}。
         * @param executor 表示本地执行器的 {@link LocalExecutor}。若为 {@code null} 则移除本地实现。
         * @throws IllegalArgumentException 当 {@code uniqueFitableId} 为 {@code null} 时。
         */
        void register(UniqueFitableId uniqueFitableId, LocalExecutor executor);
    }
}
