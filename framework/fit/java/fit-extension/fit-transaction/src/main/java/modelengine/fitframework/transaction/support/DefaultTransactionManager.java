/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;
import modelengine.fitframework.transaction.TransactionPreparationException;
import modelengine.fitframework.transaction.UnexpectedTransactionStateException;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

import javax.sql.DataSource;

/**
 * 为 {@link TransactionManager} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-08-25
 */
@Component
public class DefaultTransactionManager implements TransactionManager {
    private final LazyLoader<DataSource> dataSourceLoader;
    private final ThreadLocal<Stack<Transaction>> transactions;

    /**
     * 使用 Bean 容器初始化 {@link DefaultTransactionManager} 类的新实例。
     *
     * @param container 表示当前插件的 Bean 容器的 {@link BeanContainer}。
     * @throws IllegalArgumentException 当 {@code container} 为 {@code null} 时。
     */
    public DefaultTransactionManager(BeanContainer container) {
        notNull(container, "The bean container cannot be null.");
        this.dataSourceLoader = new LazyLoader<>(() -> container.factory(DataSource.class)
                .map(BeanFactory::get)
                .map(DataSource.class::cast)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No data source instance in current plugin. [plugin={0}]",
                        container.plugin().metadata().name()))));
        this.transactions = ThreadLocal.withInitial(Stack::new);
    }

    @Override
    public DataSource dataSource() {
        return this.dataSourceLoader.get();
    }

    @Override
    public Transaction begin(TransactionMetadata metadata) {
        TransactionMetadata actualMetadata = TransactionMetadata.withDefault(metadata);
        Transaction transaction = actualMetadata.propagation().propagate(this, active(), actualMetadata);
        activate(transaction);
        return transaction;
    }

    @Override
    public Transaction active() {
        Stack<Transaction> transactionItems = this.transactions.get();
        return transactionItems.empty() ? null : transactionItems.peek();
    }

    @Override
    public void activate(Transaction transaction) {
        if (transaction != null) {
            prepareTransaction(transaction);
            this.transactions.get().push(transaction);
        }
    }

    @Override
    public void deactivate(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        if (transaction == this.active()) {
            this.transactions.get().pop();
            prepareTransaction(this.active());
        } else {
            throw new UnexpectedTransactionStateException(StringUtils.format(
                    "The transaction to deactivate is not active. [transaction={0}]",
                    transaction.metadata().name()));
        }
    }

    private static void prepareTransaction(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        TransactionMetadata metadata = transaction.metadata();
        Connection connection = transaction.connection();
        try {
            metadata.isolation().accept(transaction.connection());
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException suppressed) {
                e.addSuppressed(suppressed);
            }
            throw new TransactionPreparationException(StringUtils.format(
                    "Failed to accept isolation level for connection. [name={0}, isolation={1}]",
                    metadata.name(),
                    metadata.isolation()), e);
        }
        try {
            connection.setReadOnly(metadata.readonly());
        } catch (SQLException e) {
            throw new TransactionPreparationException(StringUtils.format(
                    "Failed to set readonly property of hosting connection. [name={0}, readonly={1}]",
                    metadata.name(),
                    metadata.readonly()), e);
        }
    }
}
