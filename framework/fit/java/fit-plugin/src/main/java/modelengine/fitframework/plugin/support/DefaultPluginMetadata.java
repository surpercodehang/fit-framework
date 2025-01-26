/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.support;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.io.virtualization.VirtualDirectory;
import modelengine.fitframework.plugin.PluginCategory;
import modelengine.fitframework.plugin.PluginKey;
import modelengine.fitframework.plugin.PluginMetadata;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 为 {@link PluginMetadata} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-06-06
 */
public class DefaultPluginMetadata implements PluginMetadata {
    private final String group;
    private final String name;
    private final List<String> hierarchicalNames;
    private final String version;
    private final URL location;
    private final PluginCategory category;
    private final int level;

    /**
     * 使用插件的位置、名称、类别和级别初始化 {@link DefaultPluginMetadata} 类的新实例。
     *
     * @param key 表示插件的键的 {@link PluginKey}。
     * @param location 表示插件位置的 {@link VirtualDirectory}。
     * @param category 表示插件的类别的 {@link PluginCategory}。默认为 {@link PluginCategory#USER}。
     * @param level 表示插件的级别的32位整数。
     * @throws IllegalArgumentException 当 {@code location} 为 {@code null} 或 {@code name} 为空白字符串时。
     */
    public DefaultPluginMetadata(PluginKey key, URL location, PluginCategory category, int level) {
        this(key, null, location, category, level);
    }

    /**
     * 使用插件的位置、名称、类别和级别初始化 {@link DefaultPluginMetadata} 类的新实例。
     *
     * @param key 表示插件的键的 {@link PluginKey}。
     * @param hierarchicalNames 表示插件层次化的名字的 {@link String}。
     * @param location 表示插件位置的 {@link VirtualDirectory}。
     * @param category 表示插件的类别的 {@link PluginCategory}。默认为 {@link PluginCategory#USER}。
     * @param level 表示插件的级别的32位整数。
     * @throws IllegalArgumentException 当 {@code location} 为 {@code null} 或 {@code name} 为空白字符串时。
     */
    public DefaultPluginMetadata(PluginKey key, String hierarchicalNames, URL location, PluginCategory category,
            int level) {
        this.group = key.group();
        this.name = key.name();
        if (StringUtils.isNotBlank(hierarchicalNames)) {
            this.hierarchicalNames =
                    StringUtils.split(hierarchicalNames, Config.SEPARATOR_DOT, ArrayList::new, StringUtils::isNotBlank);
        } else {
            this.hierarchicalNames =
                    StringUtils.split(this.name, Config.SEPARATOR_HYPHEN, ArrayList::new, StringUtils::isNotBlank);
        }
        this.version = key.version();
        this.location = Validation.notNull(location, "The location of plugin cannot be null.");
        this.category = ObjectUtils.nullIf(category, PluginCategory.USER);
        this.level = level;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public URL location() {
        return this.location;
    }

    @Override
    public List<String> hierarchicalNames() {
        return this.hierarchicalNames;
    }

    @Override
    public PluginCategory category() {
        return this.category;
    }

    @Override
    public int level() {
        return this.level;
    }

    @Override
    public String toString() {
        return PluginKey.identify(this);
    }
}
