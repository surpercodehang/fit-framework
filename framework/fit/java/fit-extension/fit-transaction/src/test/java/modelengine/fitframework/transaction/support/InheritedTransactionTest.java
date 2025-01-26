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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionCreationException;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 为 {@link InheritedTransaction} 提供单元测试。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
@DisplayName("测试 InheritedTransaction 实现")
class InheritedTransactionTest {
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
    @DisplayName("当父事务为 null 时，抛出异常")
    void should_throw_when_parent_is_null() {
        TransactionCreationException exception = assertThrows(TransactionCreationException.class,
                () -> new InheritedTransaction(this.manager, this.metadata, null));
        assertEquals("The parent of a inherited transaction cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("使用父事务的连接")
    void should_use_parent_connection() {
        InheritedTransaction transaction = new InheritedTransaction(this.manager, this.metadata, this.parent);
        assertSame(parent.connection(), transaction.connection());
    }

    @Test
    @DisplayName("当提交事务时，不执行任何实际操作")
    void should_do_nothing_when_commit() throws SQLException {
        InheritedTransaction transaction = new InheritedTransaction(this.manager, this.metadata, this.parent);
        when(this.manager.active()).thenReturn(transaction);
        transaction.commit();
        verify(this.connection, times(0)).close();
        verify(this.connection, times(0)).setAutoCommit(anyBoolean());
        verify(this.connection, times(0)).commit();
        verify(this.connection, times(0)).releaseSavepoint(any());
    }

    @Test
    @DisplayName("当回滚事务时，不执行任何实际操作")
    void should_do_nothing_when_rollback() throws SQLException {
        InheritedTransaction transaction = new InheritedTransaction(this.manager, this.metadata, this.parent);
        when(this.manager.active()).thenReturn(transaction);
        transaction.rollback();
        verify(this.connection, times(0)).close();
        verify(this.connection, times(0)).setAutoCommit(anyBoolean());
        verify(this.connection, times(0)).rollback();
        verify(this.connection, times(0)).rollback(any());
    }
}
