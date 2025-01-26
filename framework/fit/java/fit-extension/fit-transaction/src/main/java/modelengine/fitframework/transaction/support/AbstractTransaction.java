/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionCompletionException;
import modelengine.fitframework.transaction.TransactionCreationException;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;
import modelengine.fitframework.transaction.UnexpectedTransactionStateException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 为 {@link Transaction} 提供基类。
 *
 * @author 梁济时
 * @since 2022-08-24
 */
abstract class AbstractTransaction implements Transaction {
    private final TransactionManager manager;
    private final TransactionMetadata metadata;
    private final Transaction parent;
    private boolean complete;

    /**
     * 使用所属的管理程序、事务的元数据及父事务初始化 {@link AbstractTransaction} 类的新实例。
     *
     * @param manager 表示所属从事务管理程序的 {@link TransactionManager}。
     * @param metadata 表示事务的元数据的 {@link TransactionMetadata}。
     * @param parent 表示父事务的 {@link Transaction}。
     * @throws IllegalArgumentException {@code manager} 或 {@code metadata} 为 {@code null}。
     */
    AbstractTransaction(TransactionManager manager, TransactionMetadata metadata, Transaction parent) {
        this.manager = notNull(manager, "The manager of a transaction cannot be null.");
        this.metadata = notNull(metadata, "The metadata of a transaction cannot be null.");
        this.parent = parent;
        this.complete = false;
    }

    /**
     * 获取事务所属的管理程序。
     *
     * @return 表示所属的管理程序的 {@link TransactionManager}。
     */
    @Override
    public final TransactionManager manager() {
        return this.manager;
    }

    @Override
    public final TransactionMetadata metadata() {
        return this.metadata;
    }

    @Override
    public final Transaction parent() {
        return this.parent;
    }

    @Override
    public final boolean active() {
        return this.manager.active() == this;
    }

    @Override
    public final boolean complete() {
        return this.complete;
    }

    @Override
    public final void commit() {
        if (this.complete()) {
            throw new UnexpectedTransactionStateException("Cannot commit a transaction that is complete.");
        } else if (!this.active()) {
            throw new UnexpectedTransactionStateException("Cannot commit a transaction that is not active.");
        } else {
            this.complete(this::doCommit);
        }
    }

    @Override
    public final void rollback() {
        if (this.complete()) {
            throw new UnexpectedTransactionStateException("Cannot rollback a transaction that is complete.");
        } else if (!this.active()) {
            throw new UnexpectedTransactionStateException("Cannot rollback a transaction that is not active.");
        } else {
            this.complete(this::doRollback);
        }
    }

    private void complete(Runnable action) {
        try {
            action.run();
        } catch (TransactionCompletionException ex) {
            try {
                this.doComplete();
            } catch (UnexpectedTransactionStateException suppressed) {
                ex.addSuppressed(suppressed);
            }
            throw ex;
        }
        this.doComplete();
    }

    /**
     * 执行事务提交的核心逻辑。
     */
    protected abstract void doCommit();

    /**
     * 执行事务回滚的核心逻辑。
     */
    protected abstract void doRollback();

    private void doComplete() {
        this.complete = true;
        this.manager.deactivate(this);
    }

    /**
     * 建立数据库连接。
     *
     * <p>因为建立连接仅会发生在打开事务的阶段，因此在失败时，将抛出 {@link TransactionCreationException} 异常。</p>
     *
     * @param manager 表示事务所属管理程序的 {@link TransactionManager}。
     * @return 表示新建立的连接的 {@link Connection}。
     * @throws TransactionCreationException 建立连接失败。
     */
    protected static Connection openConnection(TransactionManager manager) {
        try {
            return manager.dataSource().getConnection();
        } catch (SQLException e) {
            throw new TransactionCreationException("Failed to open connection for transaction.", e);
        }
    }

    /**
     * 关闭数据库连接。
     *
     * <p>因为关闭连接必然发生在事务完成阶段，因此失败时，将抛出 {@link TransactionCompletionException} 异常。</p>
     *
     * @param connection 表示待关闭的连接的 {@link Connection}。
     * @throws TransactionCompletionException 关闭连接失败。
     */
    protected static void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            throw new TransactionCompletionException("Failed to close connection when complete transaction.", e);
        }
    }

    /**
     * 禁用自动提交。
     *
     * <p>因为禁用自动提交仅会发生在打开事务的阶段，因此在失败时，将抛出 {@link TransactionCreationException} 异常。</p>
     *
     * @param connection 表示待禁用自动提交的连接的 {@link Connection}。
     * @throws TransactionCreationException 禁用自动提交。
     */
    protected static void disableAutoCommit(Connection connection) {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new TransactionCreationException("Failed to disable auto-commit for connection.", e);
        }
    }

    /**
     * 启用自动提交。
     *
     * <p>因为启用自动提交必然发生在事务完成阶段，因此失败时，将抛出 {@link TransactionCompletionException} 异常。</p>
     *
     * @param connection 表示待启用自动提交的连接的 {@link Connection}。
     * @throws TransactionCompletionException 启用自动提交失败。
     */
    protected static void enableAutoCommit(Connection connection) {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new TransactionCompletionException("Failed to restore auto-commit for connection.", e);
        }
    }
}
