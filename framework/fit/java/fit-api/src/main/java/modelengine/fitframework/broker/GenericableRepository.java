/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link Genericable} 的仓库。
 *
 * @author 季聿阶
 * @since 2023-03-08
 */
public interface GenericableRepository {
    /**
     * 获取仓库的名字。
     *
     * @return 表示仓库名字的 {@link String}。
     */
    String name();

    /**
     * 根据指定的服务唯一标识获取服务。
     *
     * @param id 表示服务唯一标识的 {@link String}。
     * @param version 表示服务版本号的 {@link String}。
     * @return 表示获取的服务的 {@link Optional}{@code <}{@link Genericable}{@code >}。
     */
    Optional<Genericable> get(String id, String version);

    /**
     * 获取所有的服务。
     *
     * @return 表示所有服务信息的 {@link Map}{@code <}{@link UniqueGenericableId}{@code , }{@link Genericable}{@code >}。
     */
    Map<UniqueGenericableId, Genericable> getAll();
}
