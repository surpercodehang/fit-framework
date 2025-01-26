/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.beans.convert.ConversionService;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigValueSupplier;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Type;

/**
 * 为 {@link Config} 提供基类。
 *
 * @author 梁济时
 * @since 2022-12-14
 */
public abstract class AbstractConfig implements Config, ConfigValueSupplier {
    private final String name;

    /**
     * 使用配置的名称初始化 {@link AbstractConfig} 类的新实例。
     *
     * @param name 表示配置的名称的 {@link String}。
     */
    public AbstractConfig(String name) {
        this.name = nullIf(name, StringUtils.EMPTY);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Object get(String key) {
        return this.getWithCanonicalKey(Config.canonicalizeKey(key));
    }

    /**
     * 获取指定规范化键的值。
     *
     * @param key 表示规范化后的键的 {@link String}。
     * @return 表示指定规范化键的值的 {@link Object}。
     */
    protected abstract Object getWithCanonicalKey(String key);

    @Override
    public Object get(String key, Type type) {
        Object value = this.get(key);
        return ConversionService.forConfig().convert(value, type);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = this.get(key);
        return ConversionService.forConfig().convert(value, clazz);
    }

    @Override
    public String toString() {
        String display = this.name();
        if (StringUtils.isEmpty(display)) {
            display = super.toString();
        }
        return display;
    }
}
