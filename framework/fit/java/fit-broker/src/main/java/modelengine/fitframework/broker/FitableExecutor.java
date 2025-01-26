/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.util.List;

/**
 * 表示泛服务的实现的执行器。
 *
 * @author 季聿阶
 * @since 2023-03-28
 */
public interface FitableExecutor {
    /**
     * 执行指定的泛服务实现。
     *
     * @param fitable 表示指定的泛服务实现的 {@link Fitable}。
     * @param targets 表示泛服务实现的地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     * @param context 表示调用上下文的 {@link InvocationContext}。
     * @param args 表示调用参数列表的 {@link Object}{@code []}。
     * @return 表示调用结果的 {@link Object}。
     */
    Object execute(Fitable fitable, List<Target> targets, InvocationContext context, Object[] args);
}
