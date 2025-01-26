/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionCompletionException;
import modelengine.fitframework.transaction.TransactionCreationException;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 为 {@link IndependentTransaction} 提供单元测试。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
@DisplayName("测试 IndependentTransaction 实现")
class IndependentTransactionTest {
    private TransactionManager manager;
    private TransactionMetadata metadata;
    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    void setup() {
        this.manager = mock(TransactionManager.class);
        this.metadata = mock(TransactionMetadata.class);
        this.dataSource = mock(DataSource.class);
        this.connection = mock(Connection.class);

        when(this.manager.dataSource()).thenReturn(this.dataSource);
    }

    @Nested
    @DisplayName("使用自有连接")
    class UseNewConnectionTest {
        @Nested
        @DisplayName("有父事务，且父事务有后端事务")
        class WithParentHasBackend {
            private Transaction parent;

            @BeforeEach
            void setup() {
                this.parent = mock(Transaction.class);
                when(this.parent.hasBackend()).thenReturn(true);
            }

            @Test
            @DisplayName("当与数据库建立连接失败时，创建事务失败")
            void should_throw_when_fail_to_connect() throws SQLException {
                when(dataSource.getConnection()).thenThrow(new SQLException("test"));
                TransactionCreationException exception = assertThrows(TransactionCreationException.class,
                        () -> new IndependentTransaction(manager, metadata, this.parent));
                assertEquals("Failed to open connection for transaction.", exception.getMessage());
            }

            @Test
            @DisplayName("禁用 autoCommit 失败时，创建事务失败")
            void should_throw_when_fail_to_disable_auto_commit() throws SQLException {
                when(dataSource.getConnection()).thenReturn(connection);
                doThrow(new SQLException("test")).when(connection).setAutoCommit(false);
                TransactionCreationException exception = assertThrows(TransactionCreationException.class,
                        () -> new IndependentTransaction(manager, metadata, this.parent));
                assertEquals("Failed to disable auto-commit for connection.", exception.getMessage());
            }

            @Test
            @DisplayName("实例化事务时，从数据源中获取新的连接，并禁用 autoCommit")
            void should_create_transaction_with_new_connection() throws SQLException {
                when(dataSource.getConnection()).thenReturn(connection);
                IndependentTransaction transaction = new IndependentTransaction(manager, metadata);
                when(manager.active()).thenReturn(transaction);
                assertSame(connection, transaction.connection());
                verify(dataSource, times(1)).getConnection();
                verify(connection, times(1)).setAutoCommit(false);
            }
        }

        @Nested
        @DisplayName("没有有父事务")
        class WithNoParent {
            @Test
            @DisplayName("当与数据库建立连接失败时，创建事务失败")
            void should_throw_when_fail_to_connect() throws SQLException {
                when(dataSource.getConnection()).thenThrow(new SQLException("test"));
                TransactionCreationException exception = assertThrows(TransactionCreationException.class,
                        () -> new IndependentTransaction(manager, metadata));
                assertEquals("Failed to open connection for transaction.", exception.getMessage());
            }

            @Test
            @DisplayName("禁用 autoCommit 失败时，创建事务失败")
            void should_throw_when_fail_to_disable_auto_commit() throws SQLException {
                when(dataSource.getConnection()).thenReturn(connection);
                doThrow(new SQLException("test")).when(connection).setAutoCommit(false);
                TransactionCreationException exception = assertThrows(TransactionCreationException.class,
                        () -> new IndependentTransaction(manager, metadata));
                assertEquals("Failed to disable auto-commit for connection.", exception.getMessage());
            }

            @Test
            @DisplayName("实例化事务时，从数据源中获取新的连接，并禁用 autoCommit")
            void should_create_transaction_with_new_connection() throws SQLException {
                when(dataSource.getConnection()).thenReturn(connection);
                IndependentTransaction transaction = new IndependentTransaction(manager, metadata);
                when(manager.active()).thenReturn(transaction);
                assertSame(connection, transaction.connection());
                verify(dataSource, times(1)).getConnection();
                verify(connection, times(1)).setAutoCommit(false);
            }
        }

        @Nested
        @DisplayName("事务提交和回滚")
        class SuccessTest {
            private IndependentTransaction transaction;

            @BeforeEach
            void setup() throws SQLException {
                when(dataSource.getConnection()).thenReturn(connection);
                transaction = new IndependentTransaction(manager, metadata);
                when(manager.active()).thenReturn(transaction);
            }

            @Test
            @DisplayName("提交事务时，首先提交后端事务，之后关闭数据库连接")
            void should_commit_backend_transaction_and_then_close_connection() throws SQLException {
                this.transaction.commit();
                verify(connection, times(1)).commit();
                verify(connection, times(1)).setAutoCommit(true);
                verify(connection, times(1)).close();
            }

            @Test
            @DisplayName("当提交后端事务失败时，提交事务失败，抛出异常")
            void should_throw_when_fail_to_commit_backend_transaction() throws SQLException {
                doThrow(new SQLException("test")).when(connection).commit();
                TransactionCompletionException exception =
                        assertThrows(TransactionCompletionException.class, () -> this.transaction.commit());
                assertEquals("Failed to commit backend transaction.", exception.getMessage());
            }

            @Test
            @DisplayName("提交事务时，若重置 autoCommit 属性失败，则提交事务失败，抛出异常")
            void should_throw_when_fail_to_reset_auto_commit_after_commit() throws SQLException {
                doThrow(new SQLException("test")).when(connection).setAutoCommit(true);
                TransactionCompletionException exception =
                        assertThrows(TransactionCompletionException.class, () -> this.transaction.commit());
                assertEquals("Failed to restore auto-commit for connection.", exception.getMessage());
            }

            @Test
            @DisplayName("提交事务时，若关闭数据库连接失败，则提交事务失败，抛出异常")
            void should_throw_when_fail_to_close_connection_after_commit() throws SQLException {
                doThrow(new SQLException("test")).when(connection).close();
                TransactionCompletionException exception =
                        assertThrows(TransactionCompletionException.class, () -> this.transaction.commit());
                assertEquals("Failed to close connection when complete transaction.", exception.getMessage());
            }

            @Test
            @DisplayName("回滚事务时，首先回滚后端事务，之后关闭数据库连接")
            void should_rollback_backend_transaction_and_then_close_connection() throws SQLException {
                this.transaction.rollback();
                verify(connection, times(1)).rollback();
                verify(connection, times(1)).setAutoCommit(true);
                verify(connection, times(1)).close();
            }

            @Test
            @DisplayName("当回滚后端事务失败时，回滚事务失败，抛出异常")
            void should_throw_when_fail_to_rollback_backend_transaction() throws SQLException {
                doThrow(new SQLException("test")).when(connection).rollback();
                TransactionCompletionException exception =
                        assertThrows(TransactionCompletionException.class, () -> this.transaction.rollback());
                assertEquals("Failed to rollback backend transaction.", exception.getMessage());
            }

            @Test
            @DisplayName("回滚事务时，若重置 autoCommit 属性失败，则回滚事务失败，抛出异常")
            void should_throw_when_fail_to_reset_auto_commit_after_rollback() throws SQLException {
                doThrow(new SQLException("test")).when(connection).setAutoCommit(true);
                TransactionCompletionException exception =
                        assertThrows(TransactionCompletionException.class, () -> this.transaction.rollback());
                assertEquals("Failed to restore auto-commit for connection.", exception.getMessage());
            }

            @Test
            @DisplayName("回滚事务时，若关闭数据库连接失败，则回滚事务失败，抛出异常")
            void should_throw_when_fail_to_close_connection_after_rollback() throws SQLException {
                doThrow(new SQLException("test")).when(connection).close();
                TransactionCompletionException exception =
                        assertThrows(TransactionCompletionException.class, () -> this.transaction.rollback());
                assertEquals("Failed to close connection when complete transaction.", exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("使用父事务的连接")
    class UseConnectionOfParentTest {
        private Transaction parent;

        @BeforeEach
        void setup() {
            this.parent = mock(Transaction.class);
            when(this.parent.hasBackend()).thenReturn(false);
            when(this.parent.connection()).thenReturn(connection);
        }

        @Test
        @DisplayName("使用父事务的连接")
        void should_use_connection_of_parent() throws SQLException {
            IndependentTransaction transaction = new IndependentTransaction(manager, metadata, this.parent);
            assertSame(connection, transaction.connection());
            verify(dataSource, times(0)).getConnection();
            verify(connection, times(1)).setAutoCommit(false);
        }

        @Test
        @DisplayName("提交事务时，不会关闭连接")
        void should_not_close_connection_when_commit() throws SQLException {
            IndependentTransaction transaction = new IndependentTransaction(manager, metadata, this.parent);
            when(manager.active()).thenReturn(transaction);
            transaction.commit();
            verify(connection, times(0)).close();
            verify(connection, times(1)).setAutoCommit(true);
            verify(connection, times(1)).commit();
        }

        @Test
        @DisplayName("回滚事务时，不会关闭连接")
        void should_not_close_connection_when_rollback() throws SQLException {
            IndependentTransaction transaction = new IndependentTransaction(manager, metadata, this.parent);
            when(manager.active()).thenReturn(transaction);
            transaction.rollback();
            verify(connection, times(0)).close();
            verify(connection, times(1)).setAutoCommit(true);
            verify(connection, times(1)).rollback();
        }
    }
}
