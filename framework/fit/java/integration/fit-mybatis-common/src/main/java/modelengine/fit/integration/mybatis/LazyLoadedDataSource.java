/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.integration.mybatis;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.util.LazyLoader;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 表示延迟加载的数据源。
 *
 * @author 梁济时
 * @since 2022-08-02
 */
final class LazyLoadedDataSource implements DataSource {
    private final LazyLoader<DataSource> dataSourceLoader;

    LazyLoadedDataSource(BeanContainer container) {
        this.dataSourceLoader = new LazyLoader<>(() -> container.beans().get(DataSource.class));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSourceLoader.get().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.dataSourceLoader.get().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.dataSourceLoader.get().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.dataSourceLoader.get().isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.dataSourceLoader.get().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.dataSourceLoader.get().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.dataSourceLoader.get().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.dataSourceLoader.get().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.dataSourceLoader.get().getParentLogger();
    }
}
