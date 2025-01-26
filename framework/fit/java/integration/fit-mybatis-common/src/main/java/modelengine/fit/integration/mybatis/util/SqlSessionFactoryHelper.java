/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.integration.mybatis.util;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.jvm.scan.PackageScanner;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginKey;
import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.util.StringUtils;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 {@link org.apache.ibatis.session.SqlSessionFactory} 提供统一的工具类。
 *
 * @author 季聿阶
 * @since 2023-04-05
 */
public final class SqlSessionFactoryHelper {
    private static final String CONFIG_PREFIX = "mybatis.";

    private SqlSessionFactoryHelper() {}

    /**
     * 提取配置信息中的 Mybatis 的配置，转换成 {@link Properties}。
     *
     * @param config 表示配置信息的 {@link Config}。
     * @return 表示转换后的信息的 {@link Properties}。
     */
    public static Properties properties(Config config) {
        Properties properties = new Properties();
        Set<String> keys =
                config.keys().stream().filter(key -> key.startsWith(CONFIG_PREFIX)).collect(Collectors.toSet());
        for (String key : keys) {
            String actualKey = key.substring(CONFIG_PREFIX.length());
            String value = config.get(key, String.class);
            properties.setProperty(actualKey, value);
        }
        return properties;
    }

    /**
     * 加载配置信息中指定位置的映射器集合。
     *
     * @param properties 表示配置信息的 {@link Properties}。
     * @param plugin 表示当前插件的 {@link Plugin}。
     * @param configuration 表示 Mybatis 的配置类的 {@link Configuration}。
     */
    public static void loadMappers(Properties properties, Plugin plugin, Configuration configuration) {
        loadMappersFromXml(properties, plugin, configuration);
        scanMappersInPackage(properties, plugin, configuration);
    }

    private static void loadMappersFromXml(Properties properties, Plugin plugin, Configuration configuration) {
        String mapperLocations = properties.getProperty(Config.canonicalizeKey("mapper-locations"));
        if (mapperLocations != null) {
            try {
                Resource[] resources = plugin.resolverOfResources().resolve(mapperLocations);
                loadMappersFromXml(configuration, resources);
            } catch (IOException ex) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to resolve mapper resources in class loader. [plugin={0}, pattern={1}, error={2}]",
                        PluginKey.identify(plugin.metadata()),
                        mapperLocations,
                        ex.getMessage()), ex);
            }
        }
    }

    private static void loadMappersFromXml(Configuration configuration, Resource[] resources) throws IOException {
        for (Resource resource : resources) {
            try (InputStream in = resource.read()) {
                XMLMapperBuilder builder =
                        new XMLMapperBuilder(in, configuration, resource.filename(), configuration.getSqlFragments());
                builder.parse();
            }
        }
    }

    private static void scanMappersInPackage(Properties properties, Plugin plugin, Configuration configuration) {
        String mapperScan = properties.getProperty(Config.canonicalizeKey("mapper-scan"));
        if (mapperScan != null) {
            PackageScanner.forClassLoader(plugin.pluginClassLoader(), ((scanner, clazz) -> {
                MapperAnnotationBuilder builder = new MapperAnnotationBuilder(configuration, clazz);
                builder.parse();
                configuration.addMapper(clazz);
            })).scan(Collections.singletonList(mapperScan));
        }
    }
}
