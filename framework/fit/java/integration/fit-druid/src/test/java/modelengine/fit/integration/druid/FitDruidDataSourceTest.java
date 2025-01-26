/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.integration.druid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.support.MapConfig;
import modelengine.fitframework.datasource.FitDataSource;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanRegistry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示 {@link FitDruidDataSource} 的测试集。
 *
 * @author 梁济时
 * @author 易文渊
 * @since 2022-08-02
 */
@DisplayName("测试 FitDruidDataSource 类")
class FitDruidDataSourceTest {
    @Test
    @DisplayName("当配置存在时，正确注册数据源")
    void shouldRegisterDataSourceToContainer() throws SQLException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("fit.datasource.primary.", "app-engine");
        properties.put("fit.datasource.instances.app-engine.mode.", "shared");
        properties.put("fit.datasource.instances.app-engine.druid.driver", "org.h2.Driver");
        properties.put("fit.datasource.instances.app-engine.url",
                "jdbc:h2:mem:test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        properties.put("fit.datasource.instances.app-engine.username", "root");
        properties.put("fit.datasource.instances.app-engine.password", "");

        Config config = new MapConfig("p", properties);

        BeanContainer.Beans beans = mock(BeanContainer.Beans.class);
        when(beans.get(Config.class)).thenReturn(config);

        BeanContainer container = mock(BeanContainer.class);
        when(container.all()).thenReturn(Collections.emptyList());
        when(container.beans()).thenReturn(beans);

        FitDataSource fitDruidDataSource = new FitDruidDataSource(container, config);

        try (Connection connection = fitDruidDataSource.get().getConnection()) {
            assertThat(testConnection(connection)).isEqualTo(true);
        }
    }

    private static boolean testConnection(Connection connection) throws SQLException {
        String sql = "SELECT 1";
        try (Statement statement = connection.createStatement(); ResultSet results = statement.executeQuery(sql)) {
            return results.next() && results.getLong(1) == 1L && !results.next();
        }
    }

    @Test
    @DisplayName("当配置不正确时，抛出异常")
    void shouldNotRegisterDataSourceIfConfigNotSupplied() {
        MapConfig config = new MapConfig("m", null);
        config.set("druid", null);

        BeanContainer.Beans beans = mock(BeanContainer.Beans.class);
        when(beans.get(Config.class)).thenReturn(config);

        BeanRegistry registry = mock(BeanRegistry.class);

        BeanContainer container = mock(BeanContainer.class);
        when(container.registry()).thenReturn(registry);
        when(container.beans()).thenReturn(beans);

        assertThatThrownBy(() -> new FitDruidDataSource(container,
                config).get()).isInstanceOf(IllegalArgumentException.class);
    }
}