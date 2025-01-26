/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction;

import modelengine.fitframework.transaction.support.DefaultTransactionMetadataBuilder;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 为事务提供元数据。
 *
 * @author 梁济时
 * @since 2022-08-22
 */
public interface TransactionMetadata {
    /**
     * 表示默认的事务名称。
     */
    String DEFAULT_NAME = "<UNNAMED>";

    /**
     * 获取事务的名称。
     *
     * @return 表示事务名称的 {@link String}。
     */
    String name();

    /**
     * 获取事务的传播策略。
     *
     * @return 表示传播策略的 {@link TransactionPropagationPolicy}。
     */
    TransactionPropagationPolicy propagation();

    /**
     * 获取事务的隔离级别。
     *
     * <p>事务的隔离级别仅在有事务时生效，是否有事务取决于{@link #propagation() 传播策略}。</p>
     *
     * @return 表示隔离级别的 {@link TransactionIsolationLevel}。
     */
    TransactionIsolationLevel isolation();

    /**
     * 获取事务的超时时间。
     *
     * @return 表示超时时间的秒数的32位整数。
     */
    int timeout();

    /**
     * 获取一个值，该值指示事务是否是只读的。
     * <p>如果是只读的，则会进行相应的优化。</p>
     *
     * @return 若为 {@code true}，则表示事务是只读的，否则不是只读的。
     */
    boolean readonly();

    /**
     * 获取空的事务元数据实例。
     *
     * @return 表示空的事务元数据实例的 {@link TransactionMetadata}。
     */
    static TransactionMetadata empty() {
        return DefaultTransactionMetadataBuilder.EMPTY_TRANSACTION_METADATA;
    }

    /**
     * 返回一个构建程序，用以定制化事务元数据信息。
     *
     * @return 表示用以定制化事务元数据信息的构建程序的 {@link TransactionMetadataBuilder}。
     */
    static TransactionMetadataBuilder custom() {
        return new DefaultTransactionMetadataBuilder();
    }

    /**
     * 为指定的事务元数据包装默认值。
     *
     * @param metadata 表示事务元数据的 {@link TransactionMetadata}。
     * @return 表示携带了默认值的元数据的 {@link TransactionMetadata}。
     */
    static TransactionMetadata withDefault(TransactionMetadata metadata) {
        if (metadata == null) {
            return empty();
        } else {
            return custom().name(ObjectUtils.nullIf(metadata.name(), DEFAULT_NAME))
                    .propagation(ObjectUtils.nullIf(metadata.propagation(), TransactionPropagationPolicy.REQUIRED))
                    .isolation(ObjectUtils.nullIf(metadata.isolation(), TransactionIsolationLevel.READ_COMMITTED))
                    .timeout(metadata.timeout())
                    .readonly(metadata.readonly())
                    .build();
        }
    }
}
