/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.transaction.TransactionIsolationLevel;
import modelengine.fitframework.transaction.TransactionMetadata;
import modelengine.fitframework.transaction.TransactionMetadataBuilder;
import modelengine.fitframework.transaction.TransactionPropagationPolicy;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link TransactionMetadataBuilder} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-08-22
 */
public class DefaultTransactionMetadataBuilder implements TransactionMetadataBuilder {
    /**
     * 表示空的 {@link Instance} 实例。
     */
    public static final TransactionMetadata EMPTY_TRANSACTION_METADATA = new Instance(TransactionMetadata.DEFAULT_NAME,
            TransactionPropagationPolicy.REQUIRED,
            TransactionIsolationLevel.READ_COMMITTED,
            0,
            false);

    private String name;
    private TransactionPropagationPolicy propagation;
    private TransactionIsolationLevel isolation;
    private int timeout;
    private boolean readonly;

    @Override
    public TransactionMetadataBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public TransactionMetadataBuilder propagation(TransactionPropagationPolicy propagation) {
        this.propagation = propagation;
        return this;
    }

    @Override
    public TransactionMetadataBuilder isolation(TransactionIsolationLevel isolation) {
        this.isolation = isolation;
        return this;
    }

    @Override
    public TransactionMetadataBuilder timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public TransactionMetadataBuilder readonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public TransactionMetadata build() {
        return new Instance(this.name, this.propagation, this.isolation, this.timeout, this.readonly);
    }

    /**
     * 为 {@link TransactionMetadata} 提供默认实现。
     *
     * @author 梁济时
     * @since 2022-08-22
     */
    private static class Instance implements TransactionMetadata {
        private final String name;
        private final TransactionPropagationPolicy propagation;
        private final TransactionIsolationLevel isolation;
        private final int timeout;
        private final boolean readonly;

        private Instance(String name, TransactionPropagationPolicy propagation, TransactionIsolationLevel isolation,
                int timeout, boolean readonly) {
            this.name = nullIf(name, DEFAULT_NAME);
            this.propagation = propagation;
            this.isolation = isolation;
            this.timeout = timeout;
            this.readonly = readonly;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public TransactionPropagationPolicy propagation() {
            return this.propagation;
        }

        @Override
        public TransactionIsolationLevel isolation() {
            return this.isolation;
        }

        @Override
        public int timeout() {
            return this.timeout;
        }

        @Override
        public boolean readonly() {
            return this.readonly;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {
                    Instance.class, this.name(), this.propagation(), this.isolation(), this.timeout(), this.readonly()
            });
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof Instance) {
                Instance another = (Instance) obj;
                return Objects.equals(this.name(), another.name()) && Objects.equals(this.propagation(),
                        another.propagation()) && Objects.equals(this.isolation(), another.isolation())
                        && Objects.equals(this.timeout(), another.timeout()) && Objects.equals(this.readonly(),
                        another.readonly());
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return StringUtils.format("[name={0}, propagationPolicy={1}, isolationLevel={2}, timeout={3}, "
                            + "readonly={4}]",
                    this.name(),
                    this.propagation(),
                    this.isolation(),
                    this.timeout(),
                    this.readonly());
        }
    }
}
