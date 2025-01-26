/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

/**
 * 为配置提供链式结构，并将整个链呈现为一个配置实例。
 *
 * @author 梁济时
 * @since 2022-12-14
 */
public interface ConfigChain extends Config {
    /**
     * 添加一个配置。
     *
     * @param config 表示待添加的配置的 {@link Config}。
     * @return 若将配置添加到链中，则为表示新添加的配置在链中的索引的 32 位整数，否则为 {@code -1}。
     */
    int addConfig(Config config);

    /**
     * 在指定索引处添加配置。
     *
     * @param index 表示待添加的配置添加到的索引值的 {@code int}。
     * @param config 表示待添加的配置的 {@link Config}。
     * @throws IndexOutOfBoundsException 当 {@code index} 索引超出限制时。
     * @throws IllegalArgumentException 当 {@code config} 为 {@code null} 时。
     */
    void insertConfig(int index, Config config);

    /**
     * 将一系列的配置添加到链中。
     *
     * @param configs 表示待添加的配置的 {@link Config}{@code []}。
     */
    void addConfigs(Config... configs);

    /**
     * 移除指定配置。
     *
     * @param config 表示待移除的配置的 {@link Config}。
     */
    void removeConfig(Config config);

    /**
     * 清空配置。
     */
    void clear();

    /**
     * 获取包含配置的数量。
     *
     * @return 表示配置的数量的 32 位整数。
     */
    int numberOfConfigs();

    /**
     * 获取指定索引处的配置。
     *
     * @param index 表示配置所在索引的 32 位整数。
     * @return 表示该索引处的配置的 {@link Config}。
     * @throws IndexOutOfBoundsException 索引超出限制。
     */
    Config configAt(int index);

    /**
     * 使用指定的监听程序订阅当前配置链的变化。
     *
     * @param listener 表示待添加订阅的监听程序的 {@link ConfigChainListener}。
     */
    void subscribe(ConfigChainListener listener);

    /**
     * 将指定的监听程序从当前配置链中取消定义。
     *
     * @param listener 表示待取消订阅的监听程序的 {@link ConfigChainListener}。
     */
    void unsubscribe(ConfigChainListener listener);
}
