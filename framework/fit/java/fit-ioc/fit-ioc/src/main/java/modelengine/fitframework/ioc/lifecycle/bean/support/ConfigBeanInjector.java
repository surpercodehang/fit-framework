/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.beans.BeanAccessor;
import modelengine.fitframework.beans.convert.ConversionService;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 为 {@link BeanInjector} 提供基于配置的实现。
 *
 * @author 梁济时
 * @since 2023-01-06
 */
public class ConfigBeanInjector implements BeanInjector {
    private final Config config;
    private final String key;

    public ConfigBeanInjector(Config config, String key) {
        this.config = config;
        this.key = key;
    }

    @Override
    public void inject(Object bean) {
        Type type = TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class});
        Map<String, Object> values = cast(this.config.get(this.key, type));
        if (values == null) {
            return;
        }
        BeanAccessor.of(bean.getClass(), ConversionService.forConfig()).accept(bean, values);
    }
}
