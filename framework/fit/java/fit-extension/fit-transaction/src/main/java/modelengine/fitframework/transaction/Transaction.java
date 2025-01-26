/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction;

import java.sql.Connection;

/**
 * 为事务提供定义。
 *
 * @author 梁济时
 * @since 2022-08-23
 */
public interface Transaction {
    /**
     * 获取事务的元数据。
     *
     * @return 表示事务元数据的 {@link TransactionMetadata}。
     */
    TransactionMetadata metadata();

    /**
     * 获取所属的事务管理程序。
     *
     * @return 表示所属的事务管理程序的 {@link TransactionManager}。
     */
    TransactionManager manager();

    /**
     * 获取父事务。
     *
     * @return 表示父事务的 {@link Transaction}。
     */
    Transaction parent();

    /**
     * 获取事务所使用的数据库连接。
     *
     * @return 表示数据库连接的 {@link Connection}。
     */
    Connection connection();

    /**
     * 获取一个值，该值指示事务是否处于活动状态。
     *
     * @return 若事务处于活动状态，则为 {@code true}，否则为 {@code false}。
     */
    boolean active();

    /**
     * 获取一个值，该值指示事务是否已完成。
     *
     * @return 若事务已完成，则为 {@code true}，否则为 {@code false}。
     */
    boolean complete();

    /**
     * 获取一个值，该值指示是否存在后端事务。
     *
     * @return 若存在后端事务，则为 {@code true}，否则为 {@code false}。
     */
    boolean hasBackend();

    /**
     * 提交事务。
     *
     * @throws UnexpectedTransactionStateException 当前事务已完成或未处于活动状态。
     */
    void commit();

    /**
     * 回滚事务。
     *
     * @throws UnexpectedTransactionStateException 当前事务已完成或未处于活动状态。
     */
    void rollback();
}
