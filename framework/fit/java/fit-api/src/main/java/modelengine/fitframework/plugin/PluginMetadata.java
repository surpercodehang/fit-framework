/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin;

import java.net.URL;
import java.util.Comparator;
import java.util.List;

/**
 * 为插件提供元数据。
 *
 * @author 梁济时
 * @since 2022-06-06
 */
public interface PluginMetadata extends PluginKey {
    /**
     * 获取插件的位置。
     *
     * @return 表示插件位置的 {@link URL}。
     */
    URL location();

    /**
     * 获取插件层次化的名字。
     *
     * @return 表示插件层次化的名字的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> hierarchicalNames();

    /**
     * 获取插件的类别。
     *
     * @return 表示插件类别的 {@link PluginCategory}。
     */
    PluginCategory category();

    /**
     * 获取插件的级别。
     * <p>插件级别按分类生效。</p>
     *
     * @return 表示插件级别的32位整数。
     */
    int level();

    /**
     * 表示插件元数据按启动顺序的比较程序。
     *
     * @return 表示按启动顺序的比较程序的 {@link Comparator}{@code <}{@link PluginMetadata}{@code >}。
     */
    static Comparator<PluginMetadata> startupComparator() {
        return PluginMetadataComparators.STARTUP;
    }
}
