/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.transaction.entity.UserEntity;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

/**
 * 提供集成测试。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
@DisplayName("集成测试（基于H2数据库）")
class IntegrationTest {
    private static final String URL = "jdbc:h2:mem:test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";

    private static JdbcDataSource dataSource;
    private static BeanContainer container;

    @BeforeAll
    static void setupAll() throws SQLException {
        dataSource = new JdbcDataSource();
        dataSource.setURL(URL);
        UserEntity.initialize(dataSource);
        container = mock(BeanContainer.class);
        BeanFactory factory = mock(BeanFactory.class);
        when(factory.get()).thenReturn(dataSource);
        when(container.factory(DataSource.class)).thenReturn(Optional.of(factory));
    }

    @AfterAll
    static void teardownAll() throws SQLException {
        UserEntity.destroy(dataSource);
    }

    @BeforeEach
    void setup() throws SQLException {
        UserEntity.truncate(dataSource);
    }


    @Nested
    @DisplayName("测试 Required 传播策略")
    class PropagationRequiredTest {
        private final TransactionMetadata metadata = TransactionMetadata.custom()
                .name("required-propagation-test")
                .propagation(TransactionPropagationPolicy.REQUIRED)
                .build();

        @Test
        @DisplayName("提交事务之前无法读取到数据，提交事务之后可以读取到数据")
        void can_read_after_commit() throws SQLException {
            TransactionManager manager = TransactionManager.create(container);

            Transaction transaction = manager.begin(metadata);
            UserEntity.insert(transaction.connection(), new UserEntity().name("u1"));

            // 提交之前无法读取到数据
            List<UserEntity> users = UserEntity.select(dataSource);
            assertTrue(users.isEmpty());

            transaction.commit();

            // 提交之后可读取到数据
            users = UserEntity.select(dataSource);
            assertEquals(1, users.size());
            assertEquals("u1", users.get(0).name());
        }

        @Test
        @DisplayName("回滚之后无法读取到数据")
        void cannot_read_after_rollback() throws SQLException {
            TransactionManager manager = TransactionManager.create(container);

            Transaction transaction = manager.begin(metadata);
            UserEntity.insert(transaction.connection(), new UserEntity().name("u1"));

            transaction.rollback();

            List<UserEntity> users = UserEntity.select(dataSource);
            assertTrue(users.isEmpty());
        }
    }
}
