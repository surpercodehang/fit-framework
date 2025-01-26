/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigLoadException;
import modelengine.fitframework.conf.ConfigLoadingResult;
import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

/**
 * 为 {@link Properties} 格式的配置提供加载程序。
 *
 * @author 梁济时
 * @since 2022-12-19
 */
public class PropertiesConfigLoader extends AbstractConfigLoader {
    private static final String FILE_EXTENSION = ".properties";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public Set<String> extensions() {
        return Collections.singleton(FILE_EXTENSION);
    }

    @Override
    public ConfigLoadingResult load(Resource resource, String name) {
        if (!StringUtils.equalsIgnoreCase(FileUtils.extension(resource.filename()), FILE_EXTENSION)) {
            return ConfigLoadingResult.failure();
        }
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(resource.read(), CHARSET)) {
            properties.load(reader);
        } catch (IOException ex) {
            throw new ConfigLoadException(StringUtils.format(
                    "Failed to load data of config from resource. [resource={0}]",
                    resource), ex);
        }
        String configName = this.nameOfConfig(resource, name);
        Config config = Config.fromProperties(configName, properties);
        return ConfigLoadingResult.success(config);
    }
}
