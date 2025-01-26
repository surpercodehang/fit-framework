/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction;

/**
 * 为事务提供传播程序。
 *
 * @author 梁济时
 * @since 2022-08-24
 */
public interface TransactionPropagator {
    /**
     * 传播事务。
     *
     * @param manager 表示事物管理器的 {@link TransactionManager}。
     * @param parent 表示父事务的 {@link Transaction}。
     * @param metadata 表示事务的元数据的 {@link TransactionMetadata}。
     * @return 表示传播后的事务的 {@link Transaction}。
     */
    Transaction propagate(TransactionManager manager, Transaction parent, TransactionMetadata metadata);
}
