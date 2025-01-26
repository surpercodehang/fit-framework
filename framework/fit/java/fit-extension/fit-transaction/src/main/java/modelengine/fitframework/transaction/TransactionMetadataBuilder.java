/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction;

/**
 * 为 {@link TransactionMetadata} 提供构建程序。
 *
 * @author 梁济时
 * @since 2022-08-22
 */
public interface TransactionMetadataBuilder {
    /**
     * 设置事务的名称。
     *
     * @param name 表示事务名称的 {@link String}。
     * @return 表示当前构建程序的 {@link TransactionMetadataBuilder}。
     */
    TransactionMetadataBuilder name(String name);

    /**
     * 设置事务的传播策略。
     *
     * @param propagation 表示传播策略的 {@link TransactionPropagationPolicy}。
     * @return 表示当前构建程序的 {@link TransactionMetadataBuilder}。
     */
    TransactionMetadataBuilder propagation(TransactionPropagationPolicy propagation);

    /**
     * 设置事务的隔离级别。
     *
     * @param isolation 表示隔离级别的 {@link TransactionIsolationLevel}。
     * @return 表示当前构建程序的 {@link TransactionMetadataBuilder}。
     */
    TransactionMetadataBuilder isolation(TransactionIsolationLevel isolation);

    /**
     * 设置事务的超时时间。
     *
     * @param timeout 表示超时时间的秒数的32位整数。
     * @return 表示当前构建程序的 {@link TransactionMetadataBuilder}。
     */
    TransactionMetadataBuilder timeout(int timeout);

    /**
     * 设置一个值，该值指示事务是否是只读的。
     * <p>如果是只读的，则会进行相应的优化。</p>
     *
     * @param readonly 若为 {@code true}，则表示事务是只读的，否则不是只读的。
     * @return 表示当前构建程序的 {@link TransactionMetadataBuilder}。
     */
    TransactionMetadataBuilder readonly(boolean readonly);

    /**
     * 构建事务元数据的新实例。
     *
     * @return 表示新构建的事务元数据实例的 {@link TransactionMetadata}。
     */
    TransactionMetadata build();
}
