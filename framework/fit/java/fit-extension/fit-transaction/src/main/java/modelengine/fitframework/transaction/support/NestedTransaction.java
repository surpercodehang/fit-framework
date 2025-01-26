/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionCompletionException;
import modelengine.fitframework.transaction.TransactionCreationException;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * 为 {@link Transaction} 提供嵌套事务的实现。
 * <p>嵌套事务必须在一个拥有后端事务的父事务中使用。</p>
 * <p>嵌套事务通过数据库连接的 {@code savepoint} 能力来实现。</p>
 *
 * @author 梁济时
 * @since 2022-08-25
 */
public final class NestedTransaction extends AbstractTransaction {
    private final Savepoint savepoint;

    /**
     * 使用所属的管理程序初始化 {@link NestedTransaction} 类的新实例。
     *
     * @param manager 表示所属从事务管理程序的 {@link TransactionManager}。
     * @param metadata 表示事务的元数据的 {@link TransactionMetadata}。
     * @param parent 表示父事务的 {@link Transaction}。
     * @throws IllegalArgumentException {@code manager} 为 {@code null}。
     */
    public NestedTransaction(TransactionManager manager, TransactionMetadata metadata, Transaction parent) {
        super(manager, metadata, parent);
        if (parent == null) {
            throw new TransactionCreationException("The parent of a nested transaction cannot be null.");
        }
        if (!parent.hasBackend()) {
            throw new TransactionCreationException("The parent of a nested transaction must have backend transaction.");
        }
        try {
            this.savepoint = this.connection().setSavepoint(metadata.name());
        } catch (SQLException e) {
            throw new TransactionCreationException("Failed to create savepoint for a nested transaction.", e);
        }
    }

    @Override
    public boolean hasBackend() {
        return true;
    }

    @Override
    public Connection connection() {
        return this.parent().connection();
    }

    @Override
    protected void doCommit() {
        try {
            this.connection().releaseSavepoint(this.savepoint);
        } catch (SQLException e) {
            throw new TransactionCompletionException("Failed to release savepoint for commit.", e);
        }
    }

    @Override
    protected void doRollback() {
        try {
            this.connection().rollback(this.savepoint);
        } catch (SQLException e) {
            throw new TransactionCompletionException("Failed to rollback to savepoint.", e);
        }
    }
}
