/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

/**
 * 为 {@link ConfigChain} 提供监听程序。
 *
 * @author 梁济时
 * @since 2022-12-14
 */
public interface ConfigChainListener {
    /**
     * 当向配置链中添加配置时被回调的方法。
     *
     * @param chain 表示配置被添加到的配置链的 {@link ConfigChain}。
     * @param config 表示添加到链中的配置的 {@link Config}。
     */
    default void onConfigAdded(ConfigChain chain, Config config) {}

    /**
     * 当从配置链中移除配置时被回调的方法。
     *
     * @param chain 表示配置从中移除的配置链的 {@link ConfigChain}。
     * @param config 表示被移除的配置的 {@link Config}。
     */
    default void onConfigRemoved(ConfigChain chain, Config config) {}

    /**
     * 当配置的值发生变化时被回调的方法。
     *
     * @param chain 表示值发生变化的配置所在的链的 {@link ConfigChain}。
     * @param config 表示值发生变化的配置的 {@link Config}。
     * @param key 表示值发生变化的键的 {@link String}。
     */
    default void onConfigChanged(ConfigChain chain, ModifiableConfig config, String key) {}
}
