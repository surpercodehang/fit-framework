/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;

import java.sql.Connection;

/**
 * 为 {@link Transaction} 提供假事务的实现。
 *
 * <p>假事务实际并未启用事务，其中的SQL均在未启用事务的情况下执行。</p>
 * <p>假事务使用独立的数据库连接，这个数据库连接会在事务开始时（实例化）被建立，并在事务完成时（提交或回滚）被释放。</p>
 *
 * <p>若父事务不存在后端事务，那么将直接复用父事务的连接。</p>
 *
 * @author 梁济时
 * @since 2022-08-25
 */
public class DummyTransaction extends AbstractTransaction {
    private final Connection connection;

    /**
     * 使用所属的事务管理程序及事务的元数据初始化 {@link DummyTransaction} 类的新实例。
     *
     * @param manager 表示所属从事务管理程序的 {@link TransactionManager}。
     * @param metadata 表示事务的元数据的 {@link TransactionMetadata}。
     * @throws IllegalArgumentException {@code manager} 或 {@code metadata} 为 {@code null}。
     */
    public DummyTransaction(TransactionManager manager, TransactionMetadata metadata) {
        this(manager, metadata, null);
    }

    /**
     * 使用所属的管理程序、事务的元数据及父事务初始化 {@link DummyTransaction} 类的新实例。
     *
     * @param manager 表示所属从事务管理程序的 {@link TransactionManager}。
     * @param metadata 表示事务的元数据的 {@link TransactionMetadata}。
     * @param parent 表示父事务的 {@link Transaction}。
     * @throws IllegalArgumentException {@code manager} 或 {@code metadata} 为 {@code null}。
     */
    public DummyTransaction(TransactionManager manager, TransactionMetadata metadata, Transaction parent) {
        super(manager, metadata, parent);
        if (parent == null || parent.hasBackend()) {
            this.connection = openConnection(manager);
        } else {
            this.connection = null;
        }
    }

    @Override
    public boolean hasBackend() {
        return false;
    }

    @Override
    public final Connection connection() {
        if (this.connection == null) {
            return this.parent().connection();
        } else {
            return this.connection;
        }
    }

    @Override
    protected void doCommit() {
        closeConnection(this.connection);
    }

    @Override
    protected void doRollback() {
        closeConnection(this.connection);
    }
}
