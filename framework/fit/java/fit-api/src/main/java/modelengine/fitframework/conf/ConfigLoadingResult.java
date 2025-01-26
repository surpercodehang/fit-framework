/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

/**
 * 为配置提供加载结果。
 *
 * @author 梁济时
 * @since 2022-12-16
 */
public interface ConfigLoadingResult {
    /**
     * 获取一个值，该值指示配置是否被成功加载。
     *
     * @return 若配置被成功加载，则为 {@code true}，否则为 {@code false}。
     */
    boolean loaded();

    /**
     * 获取被成功加载的配置。
     * <p>仅当 {@link #loaded()} 为 {@code true} 时有效。</p>
     *
     * @return 表示被加载的配置的 {@link Config}。
     */
    Config config();

    /**
     * 生成一个表示成功的结果。
     *
     * @param config 表示加载得到的配置的实例的 {@link Config}。
     * @return 表示加载成功的结果的 {@link ConfigLoadingResult}。
     * @throws IllegalArgumentException {@code config} 为 {@code null}。
     */
    static ConfigLoadingResult success(Config config) {
        return ConfigLoaders.success(config);
    }

    /**
     * 获取表示加载失败的结果。
     *
     * @return 表示加载失败的结果的 {@link ConfigLoadingResult}。
     */
    static ConfigLoadingResult failure() {
        return ConfigLoaders.failure();
    }
}
