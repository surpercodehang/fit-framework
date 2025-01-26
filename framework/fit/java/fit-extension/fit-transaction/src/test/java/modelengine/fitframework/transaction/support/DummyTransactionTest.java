/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;
import modelengine.fitframework.transaction.UnexpectedTransactionStateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 为 {@link DummyTransaction} 提供单元测试。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
@DisplayName("测试 DummyTransaction 实现")
class DummyTransactionTest {
    private TransactionManager manager;
    private TransactionMetadata metadata;
    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    void setup() throws SQLException {
        this.manager = mock(TransactionManager.class);
        this.metadata = mock(TransactionMetadata.class);
        this.dataSource = mock(DataSource.class);
        this.connection = mock(Connection.class);
        when(this.manager.dataSource()).thenReturn(this.dataSource);
        when(this.dataSource.getConnection()).thenReturn(this.connection);
    }

    @Test
    @DisplayName("创建新的连接，但不启用事务")
    void should_open_new_connection_but_not_use_transaction() throws SQLException {
        DummyTransaction transaction = new DummyTransaction(this.manager, this.metadata);
        when(this.manager.active()).thenReturn(transaction);
        transaction.commit();

        // 事务会打开一个新的数据库连接，用以在事务中执行SQL
        verify(this.dataSource, times(1)).getConnection();
        // 事务不会启动实际的底层事务，也不会调用 Connection.commit() 方法，而是在提交时直接关闭数据库连接
        verify(this.connection, times(0)).setAutoCommit(anyBoolean());
        verify(this.connection, times(1)).close();
    }

    @Test
    @DisplayName("事务使用独立的数据库连接，中间不会使用事务，")
    void should_not_rollback_connection_but_close_when_rollback() throws SQLException {
        DummyTransaction transaction = new DummyTransaction(this.manager, this.metadata);
        when(this.manager.active()).thenReturn(transaction);
        transaction.rollback();

        // 事务会打开一个新的数据库连接，用以在事务中执行SQL
        verify(this.dataSource, times(1)).getConnection();
        // 事务不会启动实际的底层事务，也不会调用 Connection.commit() 方法，而是在提交时直接关闭数据库连接
        verify(this.connection, times(0)).setAutoCommit(anyBoolean());
        verify(this.connection, times(1)).close();
    }

    @Test
    @DisplayName("当父事务存在后端事务时，使用新的连接")
    void should_use_new_connection_when_parent_has_backend() throws SQLException {
        Transaction parent = mock(Transaction.class);
        when(parent.hasBackend()).thenReturn(true);
        Connection parentConnection = mock(Connection.class);
        when(parent.connection()).thenReturn(parentConnection);

        DummyTransaction transaction = new DummyTransaction(this.manager, this.metadata, parent);
        when(this.manager.active()).thenReturn(transaction);

        Connection currentConnection = transaction.connection();
        assertSame(this.connection, currentConnection);
        verify(this.dataSource, times(1)).getConnection();
    }

    @Test
    @DisplayName("当父事务不存在后端事务时，复用父事务的连接")
    void should_reuse_connection_when_parent_has_no_backend() throws SQLException {
        Transaction parent = mock(Transaction.class);
        when(parent.hasBackend()).thenReturn(false);
        Connection parentConnection = mock(Connection.class);
        when(parent.connection()).thenReturn(parentConnection);

        DummyTransaction transaction = new DummyTransaction(this.manager, this.metadata, parent);
        when(this.manager.active()).thenReturn(transaction);

        Connection currentConnection = transaction.connection();
        assertSame(parentConnection, currentConnection);
        verify(this.dataSource, times(0)).getConnection();
    }

    @Nested
    @DisplayName("测试基类（AbstractTransaction）中的能力")
    class SuperclassTest {
        @Test
        @DisplayName("当所提交的事务未处于活动状态时，抛出异常")
        void should_throw_when_commit_transaction_that_is_not_active() {
            Transaction active = mock(Transaction.class);
            when(manager.active()).thenReturn(active);
            DummyTransaction transaction = new DummyTransaction(manager, metadata);
            UnexpectedTransactionStateException exception =
                    assertThrows(UnexpectedTransactionStateException.class, transaction::commit);
            assertEquals("Cannot commit a transaction that is not active.", exception.getMessage());
        }

        @Test
        @DisplayName("当所提交的事务已完成时，抛出异常")
        void should_throw_when_commit_transaction_that_is_complete() {
            DummyTransaction transaction = new DummyTransaction(manager, metadata);
            when(manager.active()).thenReturn(transaction);
            transaction.commit();
            assertTrue(transaction.complete());
            UnexpectedTransactionStateException exception =
                    assertThrows(UnexpectedTransactionStateException.class, transaction::commit);
            assertEquals("Cannot commit a transaction that is complete.", exception.getMessage());
        }

        @Test
        @DisplayName("当所回滚的事务未处于活动状态时，抛出异常")
        void should_throw_when_rollback_transaction_that_is_not_active() {
            Transaction active = mock(Transaction.class);
            when(manager.active()).thenReturn(active);
            DummyTransaction transaction = new DummyTransaction(manager, metadata);
            UnexpectedTransactionStateException exception =
                    assertThrows(UnexpectedTransactionStateException.class, transaction::rollback);
            assertEquals("Cannot rollback a transaction that is not active.", exception.getMessage());
        }

        @Test
        @DisplayName("当所提交的事务已完成时，抛出异常")
        void should_throw_when_rollback_transaction_that_is_complete() {
            DummyTransaction transaction = new DummyTransaction(manager, metadata);
            when(manager.active()).thenReturn(transaction);
            transaction.rollback();
            assertTrue(transaction.complete());
            UnexpectedTransactionStateException exception =
                    assertThrows(UnexpectedTransactionStateException.class, transaction::rollback);
            assertEquals("Cannot rollback a transaction that is complete.", exception.getMessage());
        }
    }
}
