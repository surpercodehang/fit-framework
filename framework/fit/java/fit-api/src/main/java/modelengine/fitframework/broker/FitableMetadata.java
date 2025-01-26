/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

/**
 * 表示泛服务实现的元数据。
 *
 * @author 季聿阶
 * @since 2022-10-18
 */
public interface FitableMetadata {
    /** 表示默认的服务实现的版本号的 {@link String}。 */
    String DEFAULT_VERSION = "1.0.0";

    /**
     * 获取服务实现的唯一标识。
     *
     * @return 表示服务实现的唯一标识的 {@link String}。
     */
    String id();

    /**
     * 获取服务实现的版本号。
     *
     * @return 表示服务实现的版本号的 {@link String}。
     */
    String version();

    /**
     * 获取服务实现的所有别名信息。
     *
     * @return 表示服务实现的所有别名信息的 {@link Aliases}。
     */
    Aliases aliases();

    /**
     * 获取服务实现的所有标签信息。
     *
     * @return 表示服务实现的所有标签信息的 {@link Tags}。
     */
    Tags tags();

    /**
     * 获取服务实现的降级的唯一标识。
     *
     * @return 表示服务实现的降级的唯一标识的 {@link String}。
     */
    String degradationFitableId();

    /**
     * 获取服务实现所对应的服务。
     *
     * @return 表示服务实现所对应的服务的 {@link GenericableMetadata}。
     */
    GenericableMetadata genericable();

    /**
     * 将当前服务实现元数据转化为服务实现唯一标识。
     *
     * @return 表示转换后的服务实现唯一标识的 {@link UniqueFitableId}。
     */
    UniqueFitableId toUniqueId();
}
