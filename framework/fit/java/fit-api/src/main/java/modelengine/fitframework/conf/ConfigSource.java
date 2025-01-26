/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf;

/**
 * 为配置提供数据来源。
 *
 * @author 梁济时
 * @since 2022-05-25
 */
public interface ConfigSource {
    /**
     * 获取数据来源的优先级。
     * <p>值越小优先级越高。</p>
     *
     * @return 表示优先级的32位整数。
     */
    int priority();

    /**
     * 获取指定配置的值。
     *
     * @param key 表示配置的键的 {@link String}。
     * @return 表示配置的值的 {@link Object}。
     */
    Object get(String key);
}
