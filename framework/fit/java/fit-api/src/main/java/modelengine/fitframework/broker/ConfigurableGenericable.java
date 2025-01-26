/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 表示可配置的 {@link Genericable}。
 *
 * @author 季聿阶
 * @since 2023-03-26
 */
public interface ConfigurableGenericable extends Genericable {
    /**
     * 设置服务的名字。
     *
     * @param name 表示待设置的服务名字的 {@link String}。
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable name(String name);

    /**
     * 设置服务的类型。
     *
     * @param type 表示待设置的服务类型的 {@link GenericableType}。
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable type(GenericableType type);

    /**
     * 设置服务的方法。
     *
     * @param method 表示待设置的服务方法的 {@link Method}。
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable method(Method method);

    /**
     * 设置服务的默认路由的服务实现的唯一标识。
     *
     * @param defaultFitableId 表示默认路由的服务实现唯一标识的 {@link String}。
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable route(String defaultFitableId);

    /**
     * 设置服务的所有标签的集合。
     *
     * @param tags 表示待设置的所有标签的集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable tags(Set<String> tags);

    /**
     * 添加一个标签。
     *
     * @param tag 表示待添加的标签的 {@link String}。
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable appendTag(String tag);

    /**
     * 删除一个标签。
     *
     * @param tag 表示待删除的标签的 {@link String}。
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable removeTag(String tag);

    /**
     * 清除服务的所有标签。
     *
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable clearTags();

    /**
     * 设置服务的所有实现。
     *
     * @param fitables 表示待设置的服务的所有实现的 {@link List}{@code <}{@link Fitable}{@code >}。
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable fitables(List<Fitable> fitables);

    /**
     * 添加一个服务实现。
     *
     * @param fitable 表示待添加的服务实现的 {@link Fitable}。
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable appendFitable(Fitable fitable);

    /**
     * 清除所有的服务实现。
     *
     * @return 表示当前可配置的服务的 {@link ConfigurableGenericable}。
     */
    ConfigurableGenericable clearFitables();
}
