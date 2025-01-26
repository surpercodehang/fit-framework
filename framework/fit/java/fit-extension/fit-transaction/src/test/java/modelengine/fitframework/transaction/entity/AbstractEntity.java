/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

/**
 * 为数据实体提供基类。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
public class AbstractEntity {
    /**
     * 准备语句。
     *
     * @param statement 表示待准备的语句的 {@link PreparedStatement}。
     * @param args 表示语句的执行参数的 {@link Object}{@code []}。
     * @return 表示准备好的语句的 {@link PreparedStatement}。
     * @throws SQLException 准备语句过程发生SQL异常。
     */
    private static PreparedStatement prepare(PreparedStatement statement, Object... args) throws SQLException {
        int index = 0;
        for (Object arg : args) {
            statement.setObject(++index, arg);
        }
        return statement;
    }

    /**
     * 执行SQL语句。
     *
     * @param connection 表示使用的数据库连接的 {@link Connection}。
     * @param sql 表示待执行的SQL语句的 {@link String}。
     * @param args 表示SQL的执行参数的 {@link Object}{@code []}。
     * @throws SQLException 执行过程发生SQL异常。
     */
    static void execute(Connection connection, String sql, Object... args) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            prepare(statement, args).executeUpdate();
        }
    }

    /**
     * 获取最近生成的自增ID。
     *
     * @param connection 表示使用的数据库连接的 {@link Connection}。
     * @return 表示新生成的ID的64位整数。
     * @throws SQLException 执行过程发生SQL异常。
     */
    static long lastId(Connection connection) throws SQLException {
        final String sql = "SELECT last_insert_id()";
        Object id = scalar(results -> results.getObject(1), connection, sql);
        if (id == null) {
            return 0L;
        } else if (id instanceof Number) {
            return ((Number) id).longValue();
        } else {
            throw new SQLException("Unexpected type of auto increment key: " + id.getClass().getName());
        }
    }

    /**
     * 执行SQL并返回结果集中的第一个数据记录。
     *
     * @param reader 表示数据行的读取程序的 {@link DataReader}。
     * @param connection 表示使用的数据库连接的 {@link Connection}。
     * @param sql 表示待执行的SQL的 {@link String}。
     * @param args 表示SQL的执行参数的 {@link Object}{@code []}。
     * @param <T> 表示数据的类型。
     * @return 若存在数据，则为表示读取到的数据的 {@link Object}，否则为 {@code null}。
     * @throws SQLException 执行过程发生SQL异常。
     */
    static <T> T scalar(DataReader<T> reader, Connection connection, String sql, Object... args) throws SQLException {
        List<T> list = query(reader, connection, sql, args);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询数据。
     *
     * @param reader 表示数据行的读取程序的 {@link DataReader}。
     * @param connection 表示使用的数据库连接的 {@link Connection}。
     * @param sql 表示待执行的SQL的 {@link String}。
     * @param args 表示SQL的执行参数的 {@link Object}{@code []}。
     * @param <T> 表示数据的类型。
     * @return 表示查询到的数据的列表的 {@link List}。
     * @throws SQLException 执行过程发生SQL异常。
     */
    static <T> List<T> query(DataReader<T> reader, Connection connection, String sql, Object... args)
            throws SQLException {
        List<T> list = new LinkedList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = prepare(statement, args).executeQuery()) {
            while (results.next()) {
                list.add(reader.perform(results));
            }
        }
        return list;
    }

    /**
     * 使用指定的数据源执行SQL更新。
     *
     * @param dataSource 表示数据源的 {@link DataSource}。
     * @param executor 表示SQL的执行程序的 {@link DataUpdater}。
     * @throws SQLException 执行过程发生SQL异常。
     */
    static void execute(DataSource dataSource, DataUpdater executor) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            executor.perform(connection);
        }
    }

    /**
     * 使用指定的数据源执行SQL查询。
     *
     * @param dataSource 表示数据源的 {@link DataSource}。
     * @param query 表示SQL的查询程序的 {@link DataRetriever}。
     * @param <T> 表示数据的类型。
     * @return 表示查询到的数据列表的 {@link List}。
     * @throws SQLException 执行过程发生SQL异常。
     */
    static <T> List<T> query(DataSource dataSource, DataRetriever<T> query) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return query.perform(connection);
        }
    }

    /**
     * 为数据提供基于数据库连接的更新程序。
     *
     * @author 梁济时
     * @since 2022-08-27
     */
    @FunctionalInterface
    interface DataUpdater {
        /**
         * 执行更新。
         *
         * @param connection 表示使用的数据库连接的 {@link Connection}。
         * @throws SQLException 执行过程发生SQL异常。
         */
        void perform(Connection connection) throws SQLException;
    }

    /**
     * 为数据提供基于数据库连接的检索程序。
     *
     * @param <T> 表示待检索的数据的类型。
     * @author 梁济时
     * @since 2022-08-27
     */
    @FunctionalInterface
    interface DataRetriever<T> {
        /**
         * 执行检索。
         *
         * @param connection 表示使用的数据库连接的 {@link Connection}。
         * @return 表示检索到的数据的列表的 {@link List}。
         * @throws SQLException 执行过程发生SQL异常。
         */
        List<T> perform(Connection connection) throws SQLException;
    }

    /**
     * 为从结果集中读取数据提供读取程序。
     *
     * @param <T> 表示数据的类型。
     * @author 梁济时
     * @since 2022-08-27
     */
    @FunctionalInterface
    interface DataReader<T> {
        /**
         * 从结果集中的当前行读取数据。
         *
         * @param results 表示包含数据的结果集的 {@link ResultSet}。
         * @return 表示读取到的数据的 {@link Object}。
         * @throws SQLException 执行过程发生SQL异常。
         */
        T perform(ResultSet results) throws SQLException;
    }
}
