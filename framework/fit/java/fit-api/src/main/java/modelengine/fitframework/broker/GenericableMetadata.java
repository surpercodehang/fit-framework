/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.util.List;

/**
 * 表示泛服务的元数据。
 *
 * @author 季聿阶
 * @since 2022-10-18
 */
public interface GenericableMetadata {
    /** 表示默认的泛服务的版本号的 {@link String}。 */
    String DEFAULT_VERSION = "1.0.0";

    /**
     * 获取服务的唯一标识。
     *
     * @return 表示服务的唯一标识的 {@link String}。
     */
    String id();

    /**
     * 获取服务的版本号。
     *
     * @return 表示服务的版本号的 {@link String}。
     */
    String version();

    /**
     * 获取服务的名字。
     *
     * @return 表示服务的名字的 {@link String}。
     */
    String name();

    /**
     * 获取服务的类型。
     *
     * @return 表示服务的类型的 {@link GenericableType}。
     */
    GenericableType type();

    /**
     * 获取服务的方法信息。
     *
     * @return 表示服务的方法信息的 {@link GenericableMethod}。
     */
    GenericableMethod method();

    /**
     * 获取服务的路由信息。
     *
     * @return 表示服务的路由信息的 {@link Route}。
     */
    Route route();

    /**
     * 获取服务的所有标签信息。
     *
     * @return 表示服务的所有标签信息的 {@link Tags}。
     */
    Tags tags();

    /**
     * 获取服务的所有实现列表。
     *
     * @return 表示服务的所有实现列表的 {@link List}{@code <? extends }{@link FitableMetadata}{@code >}。
     */
    List<? extends FitableMetadata> fitables();

    /**
     * 将当前服务元数据转化为服务唯一标识。
     *
     * @return 表示转换后的服务唯一标识的 {@link UniqueGenericableId}。
     */
    UniqueGenericableId toUniqueId();
}
