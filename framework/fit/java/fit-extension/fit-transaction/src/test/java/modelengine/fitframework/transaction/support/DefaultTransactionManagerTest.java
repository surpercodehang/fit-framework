/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionIsolationLevel;
import modelengine.fitframework.transaction.TransactionMetadata;
import modelengine.fitframework.transaction.TransactionPropagationPolicy;
import modelengine.fitframework.transaction.UnexpectedTransactionStateException;
import modelengine.fitframework.util.ThreadUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

/**
 * 为 {@link DefaultTransactionManager} 提供单元测试。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
@DisplayName("测试 DefaultTransactionManager 实现")
class DefaultTransactionManagerTest {
    private DataSource dataSource;
    private BeanContainer container;

    @BeforeEach
    void setup() {
        this.dataSource = mock(DataSource.class);
        this.container = mock(BeanContainer.class);
        BeanFactory factory = mock(BeanFactory.class);
        when(factory.get()).thenReturn(dataSource);
        when(this.container.factory(DataSource.class)).thenReturn(Optional.of(factory));
    }

    @Test
    @DisplayName("当数据源为 null 时，实例化事务管理程序失败")
    void should_throw_when_data_source_is_null() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> new DefaultTransactionManager(null));
        assertEquals("The bean container cannot be null.", exception.getMessage());
    }

    @Test
    @DisplayName("事务管理器通过属性返回输入的数据源")
    void should_return_input_data_source() {
        DefaultTransactionManager manager = new DefaultTransactionManager(this.container);
        assertSame(this.dataSource, manager.dataSource());
    }

    private static Transaction mockTransaction() {
        TransactionMetadata metadata = mock(TransactionMetadata.class);
        when(metadata.propagation()).thenReturn(TransactionPropagationPolicy.NEVER);
        when(metadata.isolation()).thenReturn(TransactionIsolationLevel.NONE);
        Connection connection = mock(Connection.class);
        Transaction transaction = mock(Transaction.class);
        when(transaction.metadata()).thenReturn(metadata);
        when(transaction.connection()).thenReturn(connection);
        return transaction;
    }

    @Test
    @DisplayName("激活事务仅影响当前线程")
    void should_activate_transaction_in_current_thread() throws ExecutionException, InterruptedException {
        DefaultTransactionManager manager = new DefaultTransactionManager(this.container);

        Transaction t1 = mockTransaction();
        Transaction t2 = mockTransaction();

        AtomicBoolean ok1 = new AtomicBoolean(false);
        AtomicBoolean ok2 = new AtomicBoolean(false);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<Transaction> future1 = pool.submit(() -> {
            manager.activate(t1);
            ok1.set(true);
            while (!ok2.get() && !Thread.currentThread().isInterrupted()) {
                ThreadUtils.sleep(0);
            }
            return manager.active();
        });
        Future<Transaction> future2 = pool.submit(() -> {
            manager.activate(t2);
            ok2.set(true);
            while (!ok1.get() && !Thread.currentThread().isInterrupted()) {
                ThreadUtils.sleep(0);
            }
            return manager.active();
        });
        Transaction actual1 = future1.get();
        Transaction actual2 = future2.get();
        assertSame(t1, actual1);
        assertSame(t2, actual2);
    }

    @Test
    @DisplayName("启动事务后，直接被激活")
    void should_activate_new_started_transaction() throws SQLException {
        Connection connection = mock(Connection.class);
        when(this.dataSource.getConnection()).thenReturn(connection);
        DefaultTransactionManager manager = new DefaultTransactionManager(this.container);
        Transaction transaction = manager.begin(null);
        assertSame(transaction, manager.active());
    }

    @Test
    @DisplayName("失活当前事务后，之前的事务被激活")
    void should_activate_previous_transaction_when_deactivate() {
        Transaction t1 = mockTransaction();
        Transaction t2 = mockTransaction();

        DefaultTransactionManager manager = new DefaultTransactionManager(this.container);

        manager.activate(t1);
        assertSame(t1, manager.active());
        manager.activate(null);
        assertSame(t1, manager.active());
        manager.activate(t2);
        assertSame(t2, manager.active());
        UnexpectedTransactionStateException exception =
                assertThrows(UnexpectedTransactionStateException.class, () -> manager.deactivate(t1));
        assertEquals("The transaction to deactivate is not active. [transaction=]", exception.getMessage());
        assertSame(t2, manager.active());
        manager.deactivate(t2);
        assertSame(t1, manager.active());
        manager.deactivate(null);
        assertSame(t1, manager.active());
        manager.deactivate(t1);
        assertNull(manager.active());
    }
}
