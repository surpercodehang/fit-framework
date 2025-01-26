/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.integration.druid;

import static modelengine.fitframework.datasource.support.FitDataSourceConfig.INSTANCE_PREFIX;
import static modelengine.fitframework.datasource.support.FitDataSourceConfig.PRIMARY_MODE;
import static modelengine.fitframework.datasource.support.FitDataSourceConfig.SEPARATOR;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.datasource.support.AbstractFitDataSource;
import modelengine.fitframework.ioc.BeanContainer;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

/**
 * 框架数据源的实现。
 * <p>数据源的配置如下：
 * <ol>
 *     <li>{@code primary} 指定所使用的数据源。</li>
 *     <li>{@code instances} 表示可选的多个数据源的信息。</li>
 *     <li>{@code mode} 指定某个数据源的模式。</li>
 *     <li>{@code druid} 指定某个数据源的具体配置。</li>
 * </ol>
 * </p>
 * <p>例如：
 * <pre>
 * fit:
 *   datasource:
 *     primary: 'sample-datasource' # 表示所选用的示例数据源。
 *     instances:
 *       sample-datasource:
 *         mode: 'shared' # 表示该数据源的模式，可选共享(shared)或独占(exclusive)模式。
 *         url: 'jdbc:postgresql://${ip}:${port}/' # 将 ip 换成数据库服务器的 ip 地址，将 port 换成数据库服务器监听的端口。
 *         username: '${username}' # 将 username 替换为数据库的名称。
 *         password: '${password}' # 将 password 替换为数据库的密码。
 *         druid:
 *           initialSize: ${initialSize} # 将 initialSize 替换为连接池的初始化连接数。
 *           minIdle: ${midIdle} # 将 minIdle 替换为连接池的最小空闲连接数。
 *           maxActive: ${maxActive} # 将 maxActive 替换为数据库连接池的最大活动连接数。
 *           ... # 可根据具体需求，添加连接池所需配置项。
 * </pre>
 * </p>
 *
 * @author 易文渊
 * @author 李金绪
 * @since 2024-07-27
 */
@Component
public class FitDruidDataSource extends AbstractFitDataSource {
    public FitDruidDataSource(BeanContainer beanContainer, Config config) {
        super(beanContainer, config);
    }

    @Override
    protected DataSource buildDataSource(Config config, String name) {
        Properties properties = properties(config, name);
        if (properties.isEmpty()) {
            throw new IllegalStateException("The druid data source is not configured.");
        }
        try {
            return DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create druid data source from configurations.", e);
        }
    }

    private static Properties properties(Config config, String name) {
        Properties properties = new Properties();
        String primaryPrefix = INSTANCE_PREFIX + Config.canonicalizeKey(name) + SEPARATOR;
        Set<String> keys =
                config.keys().stream().filter(key -> key.startsWith(primaryPrefix)).collect(Collectors.toSet());
        for (String key : keys) {
            if (key.contains(PRIMARY_MODE)) {
                continue;
            }
            String actualKey = key.substring(key.lastIndexOf(SEPARATOR) + 1);
            actualKey = Config.canonicalizeKey(actualKey);
            String value = config.get(key, String.class);
            properties.setProperty(actualKey, value);
        }
        return properties;
    }
}