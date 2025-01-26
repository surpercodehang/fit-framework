/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示 {@link ApplicationConfig} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-07-08
 */
public class DefaultApplication implements ApplicationConfig {
    private String name;
    private Map<String, Object> extensions;

    /**
     * 设置应用名的配置项。
     *
     * @param name 表示待设置的应用名的配置项的 {@link String}。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置应用的扩展信息。
     *
     * @param extensions 表示需要设置的扩展信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Map<String, Object> extensions() {
        if (this.extensions == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.extensions);
    }

    @Override
    public Map<String, String> visualExtensions() {
        Map<String, String> visualExtensions = new HashMap<>();
        Map<String, String> flattenedMap = MapUtils.flat(this.extensions(), ".");
        for (Map.Entry<String, String> entry : flattenedMap.entrySet()) {
            visualExtensions.put(Config.visualizeKey(entry.getKey()), entry.getValue());
        }
        return visualExtensions;
    }

    @Override
    public String toString() {
        String content = StringUtils.format("/{\"name\": \"{0}\"/}", this.name);
        return StringUtils.format("/{\"application\": {0}/}", content);
    }
}
