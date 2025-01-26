/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionCompletionException;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 为 {@link Transaction} 提供独立事务实现。
 *
 * <p>独立事务存在自己完整的生命周期，独立事务提交后，其是否生效仅取决于其自身是提交还是回滚。</p>
 * <p>即使存在父事务，也不会因为父事务的结果而影响本事务的结果。</p>
 *
 * <p>若父事务不存在后端事务，那么将直接复用父事务的连接。</p>
 *
 * @author 梁济时
 * @since 2022-08-24
 */
public final class IndependentTransaction extends AbstractTransaction {
    private final Connection connection;

    /**
     * 使用所属的管理程序及事务的元数据初始化 {@link IndependentTransaction} 类的新实例。
     *
     * @param manager 表示所属从事务管理程序的 {@link TransactionManager}。
     * @param metadata 表示事务的元数据的 {@link TransactionMetadata}。
     * @throws IllegalArgumentException {@code manager} 或 {@code metadata} 为 {@code null}。
     */
    public IndependentTransaction(TransactionManager manager, TransactionMetadata metadata) {
        this(manager, metadata, null);
    }

    /**
     * 使用所属的管理程序、事务的元数据及父事务初始化 {@link IndependentTransaction} 类的新实例。
     *
     * @param manager 表示所属从事务管理程序的 {@link TransactionManager}。
     * @param metadata 表示事务的元数据的 {@link TransactionMetadata}。
     * @param parent 表示父事务的 {@link Transaction}。
     * @throws IllegalArgumentException {@code manager} 或 {@code metadata} 为 {@code null}。
     */
    public IndependentTransaction(TransactionManager manager, TransactionMetadata metadata, Transaction parent) {
        super(manager, metadata, parent);
        if (parent == null || parent.hasBackend()) {
            this.connection = openConnection(manager);
        } else {
            this.connection = null;
        }
        disableAutoCommit(this.connection());
    }

    @Override
    public Connection connection() {
        if (this.connection == null) {
            return this.parent().connection();
        } else {
            return this.connection;
        }
    }

    @Override
    public boolean hasBackend() {
        return true;
    }

    @Override
    protected void doCommit() {
        try {
            this.connection().commit();
        } catch (SQLException e) {
            throw new TransactionCompletionException("Failed to commit backend transaction.", e);
        }
        enableAutoCommit(this.connection());
        closeConnection(this.connection);
    }

    @Override
    protected void doRollback() {
        try {
            this.connection().rollback();
        } catch (SQLException e) {
            throw new TransactionCompletionException("Failed to rollback backend transaction.", e);
        }
        enableAutoCommit(this.connection());
        closeConnection(this.connection);
    }
}
