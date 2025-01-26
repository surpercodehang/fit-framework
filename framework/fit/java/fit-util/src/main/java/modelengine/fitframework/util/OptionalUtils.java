/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 支持多级获取.上一级 {@link Supplier} 的返回值为 {@link Optional#empty()} 时，才触发下一级的 {@link Supplier#get()}.
 *
 * @author 张越
 * @since 2021-02-02
 */
public class OptionalUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private OptionalUtils() {}

    /**
     * 初始化方法.
     *
     * @param supplier 数据提供者.
     * @param <R> 返回结果泛型参数.
     * @return {@link Op} 对象，支持后续操作.
     */
    public static <R> Op<R> get(Supplier<Optional<R>> supplier) {
        return new AbstractOp<R>(supplier) {
            @Override
            public Op<R> orElse(Supplier<Optional<R>> supplier) {
                Supplier<Optional<R>> preSupplier = this.supplier;
                this.supplier = () -> {
                    Optional<R> optionalR = preSupplier.get();
                    return optionalR.isPresent() ? optionalR : supplier.get();
                };
                return this;
            }

            @Override
            public R orDefault(R r) {
                return this.supplier.get().orElse(r);
            }

            @Override
            public R orGetDefault(Supplier<R> supplier) {
                return this.supplier.get().orElseGet(supplier);
            }

            @Override
            public <E extends Throwable> R orElseThrow(Supplier<? extends E> exceptionSupplier) throws E {
                return this.supplier.get().orElseThrow(exceptionSupplier);
            }
        };
    }

    private abstract static class AbstractOp<R> implements Op<R> {
        /** 数据提供者 */
        protected Supplier<Optional<R>> supplier;

        public AbstractOp(Supplier<Optional<R>> supplier) {
            this.supplier = supplier;
        }
    }

    /**
     * 支持的操作接口.
     *
     * @author 张越
     * @since 2021-02-02
     */
    public interface Op<R> {
        /**
         * 传入下一级提供者.
         *
         * @param supplier 数据提供者.
         * @return {@link Op} 对象，支持后续操作.
         */
        Op<R> orElse(Supplier<Optional<R>> supplier);

        /**
         * 终止操作.
         *
         * @param r 默认返回值.
         * @return 返回值对象.
         */
        R orDefault(R r);

        /**
         * 终止操作.返回默认supplier提供的值.
         *
         * @param supplier 默认数据结果提供者.
         * @return 返回值对象.
         */
        R orGetDefault(Supplier<R> supplier);

        /**
         * 终止操作.若返回结果为empty，则抛出异常；否则，返回结果.
         *
         * @param exSupplier 异常提供者.
         * @param <E> 异常类型参数.
         * @return 返回值对象.
         * @throws E 具体返回的异常泛型参数.
         */
        <E extends Throwable> R orElseThrow(Supplier<? extends E> exSupplier) throws E;
    }
}
