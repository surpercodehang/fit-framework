/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge;

import modelengine.fitframework.merge.support.AbortConflictResolver;
import modelengine.fitframework.merge.support.OverrideConflictResolver;
import modelengine.fitframework.merge.support.SkipConflictResolver;
import modelengine.fitframework.pattern.builder.BuilderFactory;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;

/**
 * 表示冲突处理器。
 *
 * @param <K> 表示冲突键的类型的 {@link K}。
 * @param <V> 表示待处理冲突的值的类型的 {@link V}。
 * @param <C> 表示冲突上下文类型的 {@link C}。
 * @author 季聿阶
 * @since 2022-07-30
 */
@FunctionalInterface
public interface ConflictResolver<K, V, C extends Conflict<K>> {
    /**
     * 处理冲突。
     *
     * @param v1 表示冲突的第一个值的 {@link V}。
     * @param v2 表示冲突的第二个值的 {@link V}。
     * @param context 表示冲突上下文的 {@link C}。
     * @return 表示冲突处理结果的 {@link Result}{@code <}{@link V}{@code >}。
     */
    Result<V> resolve(V v1, V v2, C context);

    /**
     * 将一系列的冲突处理器合并成一个组合的冲突处理器。
     *
     * @param resolvers 表示待合并的冲突处理器的列表的 {@link List}{@code <}{@link ConflictResolver}{@code <}{@link
     * K}{@code , }{@link V}{@code , }{@link C}{@code >>}。
     * @param <K> 表示冲突键的类型的 {@link K}。
     * @param <V> 表示待处理冲突的值的类型的 {@link V}。
     * @param <C> 表示冲突上下文类型的 {@link C}。
     * @return 表示合并后的组合冲突处理器的 {@link ConflictResolver}{@code <}{@link K}{@code , }{@link V}{@code
     * , }{@link C}{@code >}。
     */
    static <K, V, C extends Conflict<K>> ConflictResolver<K, V, C> combine(List<ConflictResolver<K, V, C>> resolvers) {
        if (CollectionUtils.isEmpty(resolvers)) {
            return null;
        }
        return (v1, v2, context) -> {
            Result<V> ret = Result.<V>builder().resolved(false).build();
            for (ConflictResolver<K, V, C> resolver : resolvers) {
                if (resolver == null) {
                    continue;
                }
                ret = resolver.resolve(v1, v2, context);
                if (ret.resolved()) {
                    return ret;
                }
            }
            return ret;
        };
    }

    /**
     * 将两个冲突处理器合并成一个组合的冲突处理器。
     *
     * @param first 表示待合并的第一个冲突处理器的 {@link ConflictResolver}{@code <}{@link K}{@code , }{@link V}{@code
     * , }{@link C}{@code >}。
     * @param second 表示待合并的第二个冲突处理器的 {@link ConflictResolver}{@code <}{@link K}{@code , }{@link V}{@code
     * , }{@link C}{@code >}。
     * @param <K> 表示冲突键的类型的 {@link K}。
     * @param <V> 表示待处理冲突的值的类型的 {@link V}。
     * @param <C> 表示冲突上下文类型的 {@link C}。
     * @return 表示合并后的组合冲突处理器的 {@link ConflictResolver}{@code <}{@link K}{@code , }{@link V}{@code
     * , }{@link C}{@code >}。
     */
    static <K, V, C extends Conflict<K>> ConflictResolver<K, V, C> combine(ConflictResolver<K, V, C> first,
            ConflictResolver<K, V, C> second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return (v1, v2, context) -> {
                Result<V> ret = first.resolve(v1, v2, context);
                if (ret.resolved()) {
                    return ret;
                } else {
                    return second.resolve(v1, v2, context);
                }
            };
        }
    }

    /**
     * 根据指定的冲突处理策略获取冲突处理器。
     *
     * @param policy 表示指定的冲突处理策略的 {@link ConflictResolutionPolicy}。
     * @param <K> 表示冲突键的类型的 {@link K}。
     * @param <V> 表示待处理冲突的值的类型的 {@link V}。
     * @param <C> 表示冲突上下文类型的 {@link C}。
     * @return 表示获取的冲突处理器的 {@link ConflictResolver}{@code <}{@link K}{@code , }{@link V}{@code
     * , }{@link C}{@code >}。
     */
    static <K, V, C extends Conflict<K>> ConflictResolver<K, V, C> resolver(ConflictResolutionPolicy policy) {
        ConflictResolutionPolicy actualPolicy = ObjectUtils.nullIf(policy, ConflictResolutionPolicy.ABORT);
        switch (actualPolicy) {
            case SKIP:
                return ObjectUtils.cast(new SkipConflictResolver<>());
            case OVERRIDE:
                return ObjectUtils.cast(new OverrideConflictResolver<>());
            default:
                return ObjectUtils.cast(new AbortConflictResolver<>());
        }
    }

    /**
     * 表示冲突处理器处理的结果。
     *
     * @param <V> 表示处理完冲突的数据的类型的 {@link V}。
     */
    interface Result<V> {
        /**
         * 判断冲突处理结果是否成功。
         *
         * @return 如果冲突处理成功，返回 {@code true}，否则，返回 {@code false}。
         */
        boolean resolved();

        /**
         * 获取冲突处理结果。
         *
         * @return 表示冲突处理结果的 {@link V}。
         */
        V result();

        /**
         * 获取冲突处理失败的原因。
         *
         * @return 表示冲突处理失败原因的 {@link ConflictException}。
         */
        ConflictException cause();

        /**
         * {@link Result} 的构建器。
         *
         * @param <V> 表示处理完冲突的数据的类型的 {@link V}。
         */
        interface Builder<V> {
            /**
             * 向当前构建器中设置冲突是否处理成功。
             *
             * @param resolved 表示冲突是否成功的标志的 {@code boolean}。
             * @return 表示当前构建器的 {@link Builder}{@code <}{@link V}{@code >}。
             */
            Builder<V> resolved(boolean resolved);

            /**
             * 向当前构建器中设置冲突处理的结果。
             *
             * @param result 表示冲突处理结果的 {@link V}。
             * @return 表示当前构建器的 {@link Builder}{@code <}{@link V}{@code >}。
             */
            Builder<V> result(V result);

            /**
             * 向当前构建器中设置冲突处理失败的原因。
             *
             * @param cause 表示冲突处理失败原因的 {@link ConflictException}。
             * @return 表示当前构建器的 {@link Builder}{@code <}{@link V}{@code >}。
             */
            Builder<V> cause(ConflictException cause);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link Result}{@code <}{@link V}{@code >}。
             */
            Result<V> build();
        }

        /**
         * 获取 {@link Result} 的构建器。
         *
         * @return 表示 {@link Result} 的构建器的 {@link Builder}{@code <}{@link V}{@code >}。
         */
        static <V> Builder<V> builder() {
            return builder(null);
        }

        /**
         * 获取 {@link Result} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link Result}。
         * @return 表示 {@link Result} 的构建器的 {@link Builder}{@code <}{@link V}{@code >}。
         */
        static <V> Builder<V> builder(Result<V> value) {
            return ObjectUtils.cast(BuilderFactory.get(Result.class, Builder.class).create(value));
        }
    }
}
