/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor;

import modelengine.fitframework.pattern.builder.BuilderFactory;

/**
 * {@link MethodInterceptor} 的优先级解析器。
 *
 * @author 詹高扬
 * @since 2022-08-01
 */
@FunctionalInterface
public interface OrderResolver {
    /**
     * 表示对拦截器进行解析。
     *
     * @param methodInterceptor 表示待获取顺序优先级的方法拦截器的 {@link MethodInterceptor}。
     * @return 返回拦截器的优先级。
     */
    Result resolve(MethodInterceptor methodInterceptor);

    /**
     * 将指定的两个优先级解析器进行合并。
     *
     * @param r1 表示待合并的第一个优先级解析器的 {@link OrderResolver}。
     * @param r2 表示待合并的第二个优先级解析器的 {@link OrderResolver}。
     * @return 表示合并后的优先级解析器的 {@link OrderResolver}。
     */
    static OrderResolver combine(OrderResolver r1, OrderResolver r2) {
        if (r1 == null) {
            return r2;
        } else if (r2 == null) {
            return r1;
        } else {
            return mi -> {
                Result result = r1.resolve(mi);
                if (result.success()) {
                    return result;
                }
                return r2.resolve(mi);
            };
        }
    }

    /**
     * 表示解析结果。
     */
    interface Result {
        /**
         * 获取优先级顺序。
         *
         * @return 表示优先级顺序的 {@code int}。
         */
        int order();

        /**
         * 获取解析结果。
         *
         * @return 表示解析结果的 {@code boolean}。
         */
        boolean success();

        /**
         * {@link Result} 的构建器。
         */
        interface Builder {
            /**
             * 向构建器中设置解析结果优先级。
             *
             * @param order 表示解析获得的优先级的 {@code int}。
             * @return 返回当前构建器的 {@link Result.Builder}。
             */
            Builder order(int order);

            /**
             * 向构建器中设置能否进行解析。
             *
             * @param success 表示解析结果的 {@code boolean}。
             * @return 返回当前构建器的 {@link Result.Builder}。
             */
            Builder success(boolean success);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link Result}。
             */
            Result build();
        }

        /**
         * 获取 {@link Result} 的构建器。
         *
         * @return 表示 {@link Result} 的构建器的 {@link Result.Builder}。
         */
        static Builder builder() {
            return builder(null);
        }

        /**
         * 获取 {@link Result} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link Result}。
         * @return 表示 {@link Result} 的构建器的 {@link Result.Builder}。
         */
        static Builder builder(Result value) {
            return BuilderFactory.get(Result.class, Builder.class).create(value);
        }
    }
}
