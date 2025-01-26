/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.util.List;

/**
 * 表示可执行的泛服务对象。
 *
 * @author 季聿阶
 * @since 2023-03-07
 */
public interface Genericable extends GenericableMetadata {
    /**
     * 获取服务的所有实现列表。
     *
     * @return 表示服务的所有实现列表的 {@link List}{@code <}{@link Fitable}{@code >}。
     */
    @Override
    List<Fitable> fitables();

    /**
     * 公益企业服务的指定实现。
     *
     * @param fitableId 表示指定实现的唯一标识的 {@link String}。
     * @param fitableVersion 表示服务实现的版本号的 {@link String}。
     * @return 表示服务的指定实现的 {@link Fitable}。
     * @throws modelengine.fitframework.broker.client.FitableNotFoundException 当找不到指定实现时。
     */
    Fitable fitable(String fitableId, String fitableVersion);

    /**
     * 执行服务。
     *
     * @param context 表示调用上下文的 {@link InvocationContext}。
     * @param args 表示调用参数列表的 {@link Object}{@code []}。
     * @return 表示调用结果的 {@link Object}。
     */
    Object execute(InvocationContext context, Object[] args);
}
