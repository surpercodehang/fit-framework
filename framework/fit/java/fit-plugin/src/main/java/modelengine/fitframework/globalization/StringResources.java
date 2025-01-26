/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.globalization;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link StringResource} 提供工具方法。
 *
 * @author 梁济时
 * @since 2022-11-22
 */
public final class StringResources {
    private static final String ENCODING_KEY = "fit.messages.encoding";
    private static final String BASENAME_KEY = "fit.messages.basename";
    private static final char BASENAME_SEPARATOR = ',';

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private StringResources() {}

    /**
     * 获取空的消息提供程序的实现。
     *
     * @return 表示空的消息提供程序的 {@link StringResource}。
     */
    public static StringResource empty() {
        return EmptyStringResource.INSTANCE;
    }

    /**
     * 为指定插件创建消息提供程序。
     *
     * @param plugin 表示插件的 {@link Plugin}。
     * @return 表示消息提供程序的 {@link StringResource}。
     */
    public static StringResource forPlugin(Plugin plugin) {
        notNull(plugin, "The plugin to create message provider cannot be null.");
        String encoding = StringUtils.trim(plugin.config().get(ENCODING_KEY, String.class));
        String baseName = StringUtils.trim(plugin.config().get(BASENAME_KEY, String.class));
        List<StringResource> providers = Optional.ofNullable(baseName)
                .map(p -> StringUtils.split(p, BASENAME_SEPARATOR))
                .map(Stream::of)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .map(p -> new ResourceBundleStringResource(plugin.pluginClassLoader(), p, encoding))
                .collect(Collectors.toList());
        return ObjectUtils.nullIf(StringResourceComposite.combine(providers), empty());
    }
}
