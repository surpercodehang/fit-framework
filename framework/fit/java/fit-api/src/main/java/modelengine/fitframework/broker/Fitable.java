/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.util.List;

/**
 * 表示可执行的泛服务实现对象。
 *
 * @author 季聿阶
 * @since 2023-03-07
 */
public interface Fitable extends FitableMetadata {
    /**
     * 获取服务实现所对应的服务。
     *
     * @return 表示服务实现所对应的服务的 {@link Genericable}。
     */
    @Override
    Genericable genericable();

    /**
     * 获取服务实现所有的地址列表。
     *
     * @return 表示服务实现所有的地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     */
    List<Target> targets();

    /**
     * 执行服务实现。
     *
     * @param context 表示调用上下文的 {@link InvocationContext}。
     * @param args 表示调用参数列表的 {@link Object}{@code []}。
     * @return 表示调用结果的 {@link Object}。
     */
    Object execute(InvocationContext context, Object[] args);
}
