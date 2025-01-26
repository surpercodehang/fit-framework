/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.support;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigDecryptor;
import modelengine.fitframework.conf.ConfigValueSupplier;
import modelengine.fitframework.conf.support.MapConfig;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.MapBuilder;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * 表示模拟的配置类。
 *
 * @author 季聿阶
 * @since 2023-10-10
 */
public class MockConfig implements Config, ConfigValueSupplier {
    private final Config config;

    MockConfig() {
        this.config = new MapConfig("Mock",
                MapBuilder.<String, Object>get().put("prefix.k1", "v1").put("prefix.k2", "v2").put("k3", "v3").build());
    }

    @Override
    public String name() {
        return this.config.name();
    }

    @Override
    public Set<String> keys() {
        return this.config.keys();
    }

    @Override
    public Object get(String key, Type type) {
        return this.config.get(key, type);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return this.config.get(key, clazz);
    }

    @Override
    public void decrypt(@Nonnull ConfigDecryptor decryptor) {
        this.config.decrypt(decryptor);
    }

    @Override
    public Object get(String key) {
        return ConfigValueSupplier.get(this.config, key);
    }
}
