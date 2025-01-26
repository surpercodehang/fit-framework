/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.util.StringUtils;

import java.util.Set;

/**
 * 为配置提供加载程序。
 *
 * @author 梁济时
 * @since 2022-12-16
 */
public interface ConfigLoader {
    /**
     * 获取所支持的配置的扩展名。
     *
     * @return 表示扩展名的集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> extensions();

    /**
     * 从指定的资源中加载配置。
     *
     * @param resource 表示待加载的配置的资源的 {@link Resource}。
     * @return 表示配置的加载结果的 {@link ConfigLoadingResult}。
     * @throws IllegalArgumentException {@code name} 或 {@code in} 为 {@code null}。
     * @throws ConfigLoadException 加载配置失败。
     */
    default ConfigLoadingResult load(Resource resource) {
        return this.load(resource, null);
    }

    /**
     * 从指定的资源中加载配置。
     *
     * @param resource 表示待加载的配置的资源的 {@link Resource}。
     * @param name 表示配置的名称的 {@link String}。
     * @return 表示配置的加载结果的 {@link ConfigLoadingResult}。
     * @throws IllegalArgumentException {@code name} 或 {@code in} 为 {@code null}。
     * @throws ConfigLoadException 加载配置失败。
     */
    ConfigLoadingResult load(Resource resource, String name);

    /**
     * 获取空的配置加载程序的实例。
     *
     * @return 表示空的配置加载程序的 {@link ConfigLoader}。
     */
    static ConfigLoader empty() {
        return ConfigLoaders.empty();
    }

    /**
     * 使用指定的配置加载程序，加载包含配置的资源。
     *
     * @param loader 表示配置加载程序的 {@link ConfigLoader}。
     * @param resource 表示包含配置的资源的 {@link Resource}。
     * @return 表示从资源加载到的配置的 {@link Config}。
     * @throws IllegalArgumentException {@code loader} 或 {@code resource} 为 {@code null}。
     * @throws ConfigLoadException 加载配置失败。
     */
    static Config loadConfig(ConfigLoader loader, Resource resource) {
        return loadConfig(loader, resource, null);
    }

    /**
     * 使用指定的配置加载程序，加载包含配置的资源。
     *
     * @param loader 表示配置加载程序的 {@link ConfigLoader}。
     * @param resource 表示包含配置的资源的 {@link Resource}。
     * @param name 表示所加载的配置的名称的 {@link String}。
     * @return 表示从资源加载到的配置的 {@link Config}。
     * @throws IllegalArgumentException {@code loader} 或 {@code resource} 为 {@code null}。
     * @throws ConfigLoadException 加载配置失败。
     */
    static Config loadConfig(ConfigLoader loader, Resource resource, String name) {
        notNull(loader, "The loader to load config cannot be null.");
        notNull(resource, "The resource of config to load cannot be null.");
        ConfigLoadingResult result = loader.load(resource, name);
        if (result.loaded()) {
            return result.config();
        } else {
            throw new ConfigLoadException(StringUtils.format("Failed to load config from resource. [resource={0}]",
                    resource));
        }
    }
}
