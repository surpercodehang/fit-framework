/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import java.util.Set;

/**
 * 表示可配置的 {@link Fitable}。
 *
 * @author 季聿阶
 * @since 2023-03-26
 */
public interface ConfigurableFitable extends Fitable {
    /**
     * 设置所有别名的集合。
     *
     * @param aliases 表示待设置的别名集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable aliases(Set<String> aliases);

    /**
     * 添加一个别名。
     *
     * @param alias 表示待添加的别名的 {@link String}。
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable appendAlias(String alias);

    /**
     * 删除一个别名。
     *
     * @param alias 表示待删除的别名的 {@link String}。
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable removeAlias(String alias);

    /**
     * 清除所有别名。
     *
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable clearAliases();

    /**
     * 设置所有标签的集合
     *
     * @param tags 表示所有标签的集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable tags(Set<String> tags);

    /**
     * 添加一个标签。
     *
     * @param tag 表示待添加的标签的 {@link String}。
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable appendTag(String tag);

    /**
     * 删除一个标签。
     *
     * @param tag 表示待删除的标签的 {@link String}。
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable removeTag(String tag);

    /**
     * 清除所有的标签。
     *
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable clearTags();

    /**
     * 设置降级的服务实现的唯一标识。
     *
     * @param degradationFitableId 表示降级的服务实现的唯一标识的 {@link String}。
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable degradationFitableId(String degradationFitableId);

    /**
     * 设置服务实现所对应的服务。
     *
     * @param genericable 表示服务实现所对应的服务的 {@link Genericable}。
     * @return 表示当前可配置的服务实现的 {@link ConfigurableFitable}。
     */
    ConfigurableFitable genericable(Genericable genericable);
}
