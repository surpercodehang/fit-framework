/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.entity;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.util.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

/**
 * 为集成测试提供 {@code user} 表的 ORM 定义。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
public final class UserEntity extends AbstractEntity {
    private long id;
    private String name;

    /**
     * 初始化 {@link UserEntity} 类的新实例。
     */
    public UserEntity() {
        this(0, null);
    }

    /**
     * 使用用户的唯一标识和名称初始化 {@link UserEntity} 类的新实例。
     *
     * @param id 表示用户的唯一标识的64位整数。
     * @param name 表示用户名称的 {@link String}。
     */
    public UserEntity(long id, String name) {
        this.id(id).name(name);
    }

    /**
     * 获取用户的唯一标识。
     *
     * @return 表示用户唯一标识的64位整数。
     */
    public long id() {
        return this.id;
    }

    /**
     * 设置用户的唯一标识。
     *
     * @param id 表示用户唯一标识的64位整数。
     * @return 表示当前用户实体的 {@link UserEntity}。
     */
    public UserEntity id(long id) {
        this.id = id;
        return this;
    }

    /**
     * 获取用户的名称。
     *
     * @return 表示用户名称的 {@link String}。
     */
    public String name() {
        return this.name;
    }

    /**
     * 设置用户的名称。
     *
     * @param name 表示用户名称的 {@link String}。
     * @return 表示当前用户实体的 {@link UserEntity}。
     */
    public UserEntity name(String name) {
        this.name = nullIf(name, StringUtils.EMPTY);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof UserEntity) {
            UserEntity another = (UserEntity) obj;
            return this.id() == another.id() && Objects.equals(this.name(), another.name());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {UserEntity.class, this.id(), this.name()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[id={0}, name={1}]", this.id(), this.name());
    }

    /**
     * 初始化数据库，创建 {@code user} 表。
     *
     * @param connection 表示初始化过程使用的数据库连接的 {@link Connection}。
     * @throws SQLException 执行过程发生 SQL 异常。
     */
    public static void initialize(Connection connection) throws SQLException {
        final String sql = "CREATE TABLE `user`(" + "`id` BIGINT PRIMARY KEY AUTO_INCREMENT, "
                + "`name` VARCHAR(255) NOT NULL UNIQUE)";
        execute(connection, sql);
    }

    /**
     * 初始化数据库，
     * <p>创建 {@code user} 表。</p>
     *
     * @param dataSource 表示初始化过程使用的数据源的 {@link DataSource}。
     * @throws SQLException 执行过程发生 SQL 异常。
     */
    public static void initialize(DataSource dataSource) throws SQLException {
        execute(dataSource, UserEntity::initialize);
    }

    /**
     * 将指定用户信息插入到数据库中。
     *
     * @param connection 表示插入过程使用的数据库连接的 {@link Connection}。
     * @param user 表示待插入到数据库的用户实体的 {@link UserEntity}。
     * @throws SQLException 插入过程发生 SQL 异常。
     */
    public static void insert(Connection connection, UserEntity user) throws SQLException {
        final String sql = "INSERT INTO `user`(`name`) VALUES(?)";
        execute(connection, sql, user.name());
        user.id(lastId(connection));
    }

    /**
     * 将指定用户信息更新到数据库中。
     *
     * @param connection 表示更新过程使用的数据库连接的 {@link Connection}。
     * @param user 表示待更新到数据库的用户实体的 {@link UserEntity}。
     * @throws SQLException 更新过程发生 SQL 异常。
     */
    public static void update(Connection connection, UserEntity user) throws SQLException {
        final String sql = "UPDATE `user` SET `name` = ? WHERE `id` = ?";
        execute(connection, sql, user.name(), user.id());
    }

    /**
     * 从数据库中查询所有用户信息。
     *
     * @param connection 表示查新过程使用的数据库连接的 {@link Connection}。
     * @return 表示从数据库中读取到的所有用户信息的 {@link List}{@code <}{@link UserEntity}{@code >}。
     * @throws SQLException 查询过程发生 SQL 异常。
     */
    public static List<UserEntity> select(Connection connection) throws SQLException {
        final String sql = "SELECT `id`, `name` FROM `user`";
        return query(UserEntity::read, connection, sql);
    }

    /**
     * 从数据库中查询所有用户信息。
     *
     * @param dataSource 表示查新过程使用的数据源的 {@link DataSource}。
     * @return 表示从数据库中读取到的所有用户信息的 {@link List}{@code <}{@link UserEntity}{@code >}。
     * @throws SQLException 查询过程发生 SQL 异常。
     */
    public static List<UserEntity> select(DataSource dataSource) throws SQLException {
        return query(dataSource, UserEntity::select);
    }

    /**
     * 销毁数据库中所占用的资源。
     * <p>丢弃 {@code user} 表。</p>
     *
     * @param connection 表示销毁过程使用的数据库连接的 {@link Connection}。
     * @throws SQLException 销毁过程发生 SQL 异常。
     */
    public static void destroy(Connection connection) throws SQLException {
        final String sql = "DROP TABLE `user`";
        execute(connection, sql);
    }

    /**
     * 销毁数据库中所占用的资源。
     * <p>丢弃 {@code user} 表。</p>
     *
     * @param dataSource 表示销毁过程使用的数据源的 {@link DataSource}。
     * @throws SQLException 销毁过程发生 SQL 异常。
     */
    public static void destroy(DataSource dataSource) throws SQLException {
        execute(dataSource, UserEntity::destroy);
    }

    /**
     * 使数据库中的用户信息回归到初始化完成的状态。
     * <p>丢弃所有数据，并使自增索引回归到初始状态。</p>
     *
     * @param connection 表示回归过程使用的数据库连接的 {@link Connection}。
     * @throws SQLException 回归过程发生 SQL 异常。
     */
    public static void truncate(Connection connection) throws SQLException {
        final String sql = "TRUNCATE TABLE `user`";
        execute(connection, sql);
    }

    /**
     * 使数据库中的用户信息回归到初始化完成的状态。
     * <p>丢弃所有数据，并使自增索引回归到初始状态。</p>
     *
     * @param dataSource 表示回归过程使用的数据源的 {@link DataSource}。
     * @throws SQLException 回归过程发生 SQL 异常。
     */
    public static void truncate(DataSource dataSource) throws SQLException {
        execute(dataSource, UserEntity::truncate);
    }

    /**
     * 从结果集中当前数据记录中读取用户实体信息。
     *
     * @param results 表示包含用户实体信息的数据集的 {@link ResultSet}。
     * @return 表示读取到的用户信息的 {@link UserEntity}。
     * @throws SQLException 读取过程发生 SQL 异常。
     */
    private static UserEntity read(ResultSet results) throws SQLException {
        return new UserEntity().id(results.getLong(1)).name(results.getString(2));
    }
}
