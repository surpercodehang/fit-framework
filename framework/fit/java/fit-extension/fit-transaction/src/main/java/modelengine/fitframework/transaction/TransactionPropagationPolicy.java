/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction;

import modelengine.fitframework.transaction.support.DummyTransaction;
import modelengine.fitframework.transaction.support.IndependentTransaction;
import modelengine.fitframework.transaction.support.InheritedTransaction;
import modelengine.fitframework.transaction.support.NestedTransaction;
import modelengine.fitframework.util.StringUtils;

/**
 * 为事务提供传播策略的定义。
 *
 * @author 梁济时
 * @since 2022-08-22
 */
public enum TransactionPropagationPolicy implements TransactionPropagator {
    /**
     * 当不存在事务时，开始新的事务，否则加入当前事务。
     */
    REQUIRED {
        @Override
        public Transaction propagate(TransactionManager manager, Transaction parent, TransactionMetadata metadata) {
            if (hasBackend(parent)) {
                return new InheritedTransaction(manager, metadata, parent);
            } else {
                return new IndependentTransaction(manager, metadata, parent);
            }
        }
    },

    /**
     * 当存在事务时，在当前事务中执行，否则不使用事务直接执行。
     */
    SUPPORTS {
        @Override
        public Transaction propagate(TransactionManager manager, Transaction parent, TransactionMetadata metadata) {
            if (parent == null) {
                return new DummyTransaction(manager, metadata);
            } else {
                return new InheritedTransaction(manager, metadata, parent);
            }
        }
    },

    /**
     * 必须在一个已有事务中运行，否则将抛出异常。
     */
    MANDATORY {
        @Override
        public Transaction propagate(TransactionManager manager, Transaction parent, TransactionMetadata metadata) {
            if (hasBackend(parent)) {
                return new InheritedTransaction(manager, metadata, parent);
            } else {
                throw new TransactionCreationException(StringUtils.format(
                        "A transaction with mandatory propagation must in an existing transaction. [name={0}]",
                        metadata.name()));
            }
        }
    },

    /**
     * 不论当前是否存在事务，都将启动新的事务来执行。
     */
    REQUIRES_NEW {
        @Override
        public Transaction propagate(TransactionManager manager, Transaction parent, TransactionMetadata metadata) {
            return new IndependentTransaction(manager, metadata, parent);
        }
    },

    /**
     * 不论当前是否存在事务，都不在事务中执行。
     */
    NOT_SUPPORTED {
        @Override
        public Transaction propagate(TransactionManager manager, Transaction parent, TransactionMetadata metadata) {
            return new DummyTransaction(manager, metadata, parent);
        }
    },

    /**
     * 不支持在事务中执行，如果存在事务，则抛出异常。
     */
    NEVER {
        @Override
        public Transaction propagate(TransactionManager manager, Transaction parent, TransactionMetadata metadata) {
            if (TransactionPropagationPolicy.hasBackend(parent)) {
                throw new TransactionCreationException(StringUtils.format(
                        "A transaction with never propagation cannot in any existing transaction. [name={0}]",
                        metadata.name()));
            } else {
                return new DummyTransaction(manager, metadata);
            }
        }
    },

    /**
     * 如果当前存在事务，则开始嵌套事务并执行，否则开始新事务执行。
     */
    NESTED {
        @Override
        public Transaction propagate(TransactionManager manager, Transaction parent, TransactionMetadata metadata) {
            if (TransactionPropagationPolicy.hasBackend(parent)) {
                return new NestedTransaction(manager, metadata, parent);
            } else {
                return new IndependentTransaction(manager, metadata);
            }
        }
    };

    /**
     * 检查是否存在后端事务。
     *
     * <p>存在后端事务时，{@code transaction} 必然不为 {@code null}。</p>
     *
     * @param transaction 表示待检查的事务的 {@link Transaction}。
     * @return 若存在后端事务，则为 {@code true}，否则为 {@code false}。
     */
    private static boolean hasBackend(Transaction transaction) {
        return transaction != null && transaction.hasBackend();
    }
}
