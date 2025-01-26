/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionCreationException;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;

import java.sql.Connection;

/**
 * 为 {@link Transaction} 提供直接继承的实现。
 *
 * <p>直接继承的事务，是一个逻辑的概念，其所有的配置都继承自其父事务。</p>
 * <p>直接继承的事务使用父事务的连接，并且在提交和回滚时不进行任何操作。实际效果等同于在父事务中执行操作。</p>
 *
 * @author 梁济时
 * @since 2022-08-26
 */
public class InheritedTransaction extends AbstractTransaction {
    /**
     * 使用所属的管理程序初始化 {@link InheritedTransaction} 类的新实例。
     *
     * @param manager 表示所属从事务管理程序的 {@link TransactionManager}。
     * @param metadata 表示事务的元数据的 {@link TransactionMetadata}。
     * @param parent 表示父事务的 {@link Transaction}。
     * @throws IllegalArgumentException {@code manager} 为 {@code null}。
     */
    public InheritedTransaction(TransactionManager manager, TransactionMetadata metadata, Transaction parent) {
        super(manager, metadata, parent);
        if (parent == null) {
            throw new TransactionCreationException("The parent of a inherited transaction cannot be null.");
        }
    }

    @Override
    public Connection connection() {
        return this.parent().connection();
    }

    @Override
    public boolean hasBackend() {
        return this.parent().hasBackend();
    }

    @Override
    protected void doCommit() {}

    @Override
    protected void doRollback() {}
}
