/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.type.support.DefaultTypeMatcherContext;
import modelengine.fitframework.type.support.TypeMatcherDispatcher;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 为 {@link Type} 提供匹配判定程序。
 *
 * @author 梁济时
 * @since 2020-10-29
 */
public interface TypeMatcher {
    /**
     * 检查是否是一个所期望类型的实例。
     *
     * @param expectedType 表示所期望的类型的 {@link Type}。
     * @return 若是一个期望类型的实例，则为 {@code true}；否则为 {@code false}。
     */
    boolean match(Type expectedType);

    /**
     * 为类型匹配判定提供上下文信息。
     *
     * @author 梁济时
     * @since 2020-10-29
     */
    interface Context {
        /**
         * 表示空的上下文信息。
         *
         * @return 表示上下文信息的 {@link Context}。
         */
        static Context empty() {
            return DefaultTypeMatcherContext.EMPTY;
        }

        /**
         * 获取指定名称的类变量的值。
         *
         * @param variableName 表示类变量名称的 {@link String}。
         * @return 若存在该名称的类变量，则为表示该类变量的值的 {@link Optional}{@code <}{@link Type}{@code >}；否则为
         * {@link Optional#empty()}。
         */
        Optional<Type> getVariableValue(String variableName);

        /**
         * 为类型匹配上下文信息提供构建器。
         *
         * @author 梁济时
         * @since 2020-10-29
         */
        interface Builder {
            /**
             * 设置指定名称的类变量的值。
             *
             * @param name 表示类变量的名称的 {@link String}。
             * @param value 表示类变量的值的 {@link Type}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            default Builder setVariableValue(String name, Type value) {
                return this.setVariableValue(name, () -> value);
            }

            /**
             * 设置指定名称的类变量的值。
             *
             * @param name 表示类变量的名称的 {@link String}。
             * @param valueSupplier 表示类变量的值的提供者的 {@link Supplier}{@code <}{@link Type}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder setVariableValue(String name, Supplier<Type> valueSupplier);

            /**
             * 构建类型匹配上下文的新实例。
             *
             * @return 表示类型匹配上下文的实例的 {@link Context}。
             */
            Context build();
        }

        /**
         * 获取一个构建器，用以创建 {@link TypeMatcher.Context} 类的新实例。
         *
         * @return 表示类匹配上下文的构建器的 {@link Builder}。
         */
        static Builder builder() {
            return new DefaultTypeMatcherContext.Builder();
        }
    }

    /**
     * 为实例化 {@link TypeMatcher} 提供工厂。
     *
     * @author 梁济时
     * @since 2020-10-29
     */
    interface Factory {
        /**
         * 创建一个 {@link TypeMatcher} 的新实例。
         *
         * @param currentType 表示当前类型的 {@link Type}。
         * @param context 表示匹配判定过程上下文的 {@link Context}。
         * @return 表示新实例化的类型匹配判定程序的 {@link TypeMatcher}。
         */
        TypeMatcher create(Type currentType, Context context);
    }

    /**
     * 判定指定对象类型是否可以匹配到期望的类型。
     *
     * @param currentType 表示对象类型的 {@link Type}。
     * @param expectedType 表示所期望的类型的 {@link Type}。
     * @return 若可以匹配到所期望的类型，则为 {@code true}；否则为 {@code false}。
     */
    static boolean match(Type currentType, Type expectedType) {
        return match(currentType, expectedType, null);
    }

    /**
     * 判定指定对象类型是否可以匹配到期望的类型。
     *
     * @param currentType 表示对象类型的 {@link Type}。
     * @param expectedType 表示所期望的类型的 {@link Type}。
     * @param context 表示匹配过程的上下文的 {@link Context}。
     * @return 若可以匹配到所期望的类型，则为 {@code true}；否则为 {@code false}。
     */
    static boolean match(Type currentType, Type expectedType, Context context) {
        Validation.notNull(currentType, "The object type to match cannot be null.");
        Validation.notNull(expectedType, "The expected type to match cannot be null.");
        return new TypeMatcherDispatcher(currentType, context).match(expectedType);
    }
}
