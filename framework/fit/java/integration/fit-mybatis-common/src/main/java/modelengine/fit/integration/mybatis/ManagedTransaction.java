/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.integration.mybatis;

import org.apache.ibatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 表示从 {@code transaction} 组件适配 {@code Mybatis} 所需的事务类型。
 *
 * @author 梁济时
 * @since 2023-06-27
 */
class ManagedTransaction implements Transaction {
    private static final int STATE_NONE = 0x0;
    private static final int STATE_COMMIT = 0x01;
    private static final int STATE_ROLLBACK = 0x02;

    private final modelengine.fitframework.transaction.Transaction transaction;
    private int state = STATE_NONE;

    ManagedTransaction(modelengine.fitframework.transaction.Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public Connection getConnection() {
        return this.transaction.connection();
    }

    private boolean set(int state) {
        if (!this.is(state)) {
            this.state = this.state | state;
            return true;
        } else {
            return false;
        }
    }

    private boolean is(int state) {
        return (this.state & state) == state;
    }

    @Override
    public void commit() throws SQLException {
        if (this.set(STATE_COMMIT)) {
            this.transaction.commit();
        }
    }

    @Override
    public void rollback() {
        if (this.set(STATE_ROLLBACK)) {
            this.transaction.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.state == STATE_NONE) {
            this.rollback();
        }
    }

    @Override
    public Integer getTimeout() {
        return null;
    }
}
