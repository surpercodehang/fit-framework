/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

/**
 * 表示可修改的配置。
 *
 * @author 梁济时
 * @since 2022-11-17
 */
public interface ModifiableConfig extends Config {
    /**
     * 设置配置的值。
     *
     * @param key 表示配置的键的 {@link String}。
     * @param value 表示配置的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code key} 不是一个有效的配置键时。
     */
    void set(String key, Object value);

    /**
     * 订阅配置值发生变化的状态变化。
     *
     * @param listener 表示状态变化时通知的监听程序的 {@link ModifiableConfigListener}。
     */
    void subscribe(ModifiableConfigListener listener);

    /**
     * 取消订阅配置值发生变化的状态变化。
     *
     * @param listener 表示状态变化时通知的监听程序的 {@link ModifiableConfigListener}。
     */
    void unsubscribe(ModifiableConfigListener listener);
}
