/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

/**
 * 为 {@link ConfigChain} 提供组合模式的实现。
 *
 * @author 梁济时
 * @since 2022-12-16
 */
public interface ConfigLoaderChain extends ConfigLoader {
    /**
     * 添加一个配置加载程序。
     *
     * @param loader 表示待添加的配置加载程序的 {@link ConfigLoader}。
     */
    void addLoader(ConfigLoader loader);

    /**
     * 移除一个配置加载程序。
     *
     * @param loader 表示待移除的配置加载程序的 {@link ConfigLoader}。
     */
    void removeLoader(ConfigLoader loader);

    /**
     * 获取链中包含配置加载程序的数量。
     *
     * @return 表示配置加载程序的数量的 32 位整数。
     */
    int numberOfLoaders();

    /**
     * 获取指定索引处的配置加载程序。
     *
     * @param index 表示配置加载程序所在索引的 32 位整数。
     * @return 表示该索引处的配置加载程序的 32 位整数。
     * @throws IndexOutOfBoundsException 索引超出限制。
     */
    ConfigLoader loaderAt(int index);

    /**
     * 使用指定的监听程序订阅链的状态变化。
     *
     * @param listener 表示当链的状态发生变化时通知的监听程序的 {@link ConfigLoaderChainListener}。
     */
    void subscribe(ConfigLoaderChainListener listener);

    /**
     * 将指定的监听程序从链中取消状态变化的订阅。
     *
     * @param listener 表示待取消订阅的 {@link ConfigLoaderChainListener}。
     */
    void unsubscribe(ConfigLoaderChainListener listener);

    /**
     * 创建一个配置加载程序链的默认实现的新实例。
     *
     * @return 表示新创建的配置加载程序链的默认实现的 {@link ConfigLoaderChain}。
     */
    static ConfigLoaderChain createDefault() {
        return ConfigLoaders.createChain();
    }
}
