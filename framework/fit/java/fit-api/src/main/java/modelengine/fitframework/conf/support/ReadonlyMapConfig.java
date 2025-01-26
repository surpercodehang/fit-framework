/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigDecryptor;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.log.Logger;

import java.util.Map;
import java.util.Set;

/**
 * 为 {@link Config} 提供基于映射存储配置值的实现。
 *
 * @author 梁济时
 * @since 2022-12-14
 */
public class ReadonlyMapConfig extends AbstractConfig {
    private static final Logger log = Logger.get(ReadonlyMapConfig.class);

    private final MapConfig map;

    /**
     * 使用包含配置值的映射初始化 {@link ReadonlyMapConfig} 类的新实例。
     *
     * @param map 表示包含配置的值的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public ReadonlyMapConfig(Map<String, Object> map) {
        this(null, map);
    }

    /**
     * 使用配置的名称及包含配置值的映射初始化 {@link ReadonlyMapConfig} 类的新实例。
     *
     * @param name 表示配置的名称的 {@link String}。
     * @param map 表示包含配置的值的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public ReadonlyMapConfig(String name, Map<String, Object> map) {
        super(name);
        this.map = new MapConfig(name, map);
    }

    @Override
    public Set<String> keys() {
        return this.map.keys();
    }

    @Override
    public Object getWithCanonicalKey(String key) {
        return this.map.get(key);
    }

    @Override
    public void decrypt(@Nonnull ConfigDecryptor decryptor) {
        log.debug("The readonly map config does not support decrypting.");
    }
}
