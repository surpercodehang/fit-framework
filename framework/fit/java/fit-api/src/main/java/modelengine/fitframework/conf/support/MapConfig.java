/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import modelengine.fitframework.conf.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 为 {@link modelengine.fitframework.conf.ModifiableConfig} 提供使用 {@link Map} 作为数据源的实现。
 *
 * @author 梁济时
 * @since 2022-09-02
 */
public class MapConfig extends AbstractModifiableConfig {
    private final Map<String, Object> map;

    /**
     * 使用配置的名称和配置包含的值的映射初始化 {@link MapConfig} 类的新实例。
     *
     * @param name 表示配置的名称的 {@link String}。
     * @param map 表示配置包含的值的映射的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public MapConfig(String name, Map<String, Object> map) {
        super(name);
        if (map == null) {
            this.map = new HashMap<>();
        } else {
            this.map = new HashMap<>(map.size());
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    this.map.put(Config.canonicalizeKey(entry.getKey()), entry.getValue());
                }
            }
        }
    }

    @Override
    public Set<String> keys() {
        return this.map.keySet();
    }

    @Override
    public Object getWithCanonicalKey(String key) {
        return this.map.get(key);
    }

    @Override
    public void setWithCanonicalKey(String key, Object value) {
        this.map.put(key, value);
        this.notifyValueChanged(key);
    }
}
