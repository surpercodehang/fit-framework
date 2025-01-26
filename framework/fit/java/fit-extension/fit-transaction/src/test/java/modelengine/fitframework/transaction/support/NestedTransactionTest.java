/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
import java.sql.Savepoint;

/**
 * 为 {@link NestedTransaction} 提供单元测试。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
@DisplayName("测试 NestedTransaction 实现")
class NestedTransactionTest {
    private TransactionManager manager;
    private TransactionMetadata metadata;
    private Transaction parent;
    private Connection connection;

    @BeforeEach
    void setup() {
        this.manager = mock(TransactionManager.class);
        this.metadata = mock(TransactionMetadata.class);
        this.parent = mock(Transaction.class);
        this.connection = mock(Connection.class);
        when(this.parent.connection()).thenReturn(this.connection);
    }

    @Test
    @DisplayName("当父事务为 null 时抛出异常")
    void should_throw_when_parent_is_null() {
        TransactionCreationException exception = assertThrows(TransactionCreationException.class,
                () -> new NestedTransaction(this.manager, this.metadata, null));
        assertEquals("The parent of a nested transaction cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("当父事务不存在后端事务时抛出异常")
    void should_throw_when_parent_has_no_backend() {
        when(this.parent.hasBackend()).thenReturn(false);
        TransactionCreationException exception = assertThrows(TransactionCreationException.class,
                () -> new NestedTransaction(this.manager, this.metadata, this.parent));
        assertEquals("The parent of a nested transaction must have backend transaction.", exception.getMessage());
    }

    @Test
    @DisplayName("当创建保存点失败时抛出异常")
    void should_throw_when_fail_to_create_savepoint() throws SQLException {
        when(this.parent.hasBackend()).thenReturn(true);
        when(this.connection.setSavepoint(any())).thenThrow(new SQLException("test"));
        TransactionCreationException exception = assertThrows(TransactionCreationException.class,
                () -> new NestedTransaction(this.manager, this.metadata, this.parent));
        assertEquals("Failed to create savepoint for a nested transaction.", exception.getMessage());
    }

    @Test
    @DisplayName("创建事务时，会创建保存点")
    void should_create_save_point_with_create_transaction() throws SQLException {
        when(this.parent.hasBackend()).thenReturn(true);
        NestedTransaction transaction = new NestedTransaction(this.manager, this.metadata, this.parent);
        assertSame(this.parent.connection(), transaction.connection());
        verify(this.connection, times(1)).setSavepoint(any());
    }

    @Nested
    @DisplayName("测试事务的提交和回滚")
    class CommitAndRollbackTest {
        private Savepoint savepoint;
        private NestedTransaction transaction;

        @BeforeEach
        void setup() throws SQLException {
            this.savepoint = mock(Savepoint.class);
            when(parent.hasBackend()).thenReturn(true);
            when(connection.setSavepoint(any())).thenReturn(this.savepoint);
            this.transaction = new NestedTransaction(manager, metadata, parent);
            when(manager.active()).thenReturn(transaction);
        }

        @Test
        @DisplayName("当提交事务时，释放相应的保存点")
        void should_release_savepoint_when_commit() throws SQLException {
            this.transaction.commit();
            verify(connection, times(1)).releaseSavepoint(this.savepoint);
            verify(connection, times(0)).setAutoCommit(anyBoolean());
            verify(connection, times(0)).close();
        }

        @Test
        @DisplayName("当释放保存点失败时，提交事务失败")
        void should_throw_when_fail_to_release_savepoint() throws SQLException {
            doThrow(new SQLException("test")).when(connection).releaseSavepoint(any());
            TransactionCompletionException exception =
                    assertThrows(TransactionCompletionException.class, () -> this.transaction.commit());
            assertEquals("Failed to release savepoint for commit.", exception.getMessage());
        }

        @Test
        @DisplayName("当回滚事务时，将事务回滚到指定的保存点")
        void should_rollback_to_savepoint_when_rollback() throws SQLException {
            this.transaction.rollback();
            verify(connection, times(1)).rollback(this.savepoint);
            verify(connection, times(0)).setAutoCommit(anyBoolean());
            verify(connection, times(0)).close();
        }

        @Test
        @DisplayName("当回滚到保存点失败时，回滚事务失败")
        void should_throw_when_fail_to_rollback_to_savepoint() throws SQLException {
            doThrow(new SQLException("test")).when(connection).rollback(any());
            TransactionCompletionException exception =
                    assertThrows(TransactionCompletionException.class, () -> this.transaction.rollback());
            assertEquals("Failed to rollback to savepoint.", exception.getMessage());
        }
    }
}
