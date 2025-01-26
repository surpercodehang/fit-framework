/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type.support;

import modelengine.fitframework.type.TypeMatcher;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 为 {@link TypeMatcher.Context} 提供默认实现。
 *
 * @author 梁济时
 * @since 2020-10-29
 */
public class DefaultTypeMatcherContext implements TypeMatcher.Context {
    /** 表示空的上下文信息。 */
    public static final DefaultTypeMatcherContext EMPTY = new DefaultTypeMatcherContext(Collections.emptyMap());

    private final Map<String, Supplier<Type>> variableValues;

    /**
     * 使用类变量的值初始化 {@link DefaultTypeMatcherContext} 类的新实例。
     *
     * @param variableValues 表示变量值的 {@link Map}{@code <}{@link String}{@code , }{@link Supplier}{@code <
     * }{@link Type}{@code >>}。
     */
    private DefaultTypeMatcherContext(Map<String, Supplier<Type>> variableValues) {
        this.variableValues = variableValues;
    }

    @Override
    public Optional<Type> getVariableValue(String variableName) {
        Supplier<Type> supplier = this.variableValues.get(variableName);
        if (supplier == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(supplier.get());
    }

    /**
     * 为 {@link TypeMatcher.Context.Builder} 提供默认实现。
     *
     * @author 梁济时
     * @since 2020-10-29
     */
    public static class Builder implements TypeMatcher.Context.Builder {
        private final Map<String, Supplier<Type>> variableValues;

        /**
         * 初始化 {@link Builder} 类的新实例。
         */
        public Builder() {
            this.variableValues = new HashMap<>();
        }

        @Override
        public TypeMatcher.Context.Builder setVariableValue(String name, Supplier<Type> valueSupplier) {
            this.variableValues.put(name, valueSupplier);
            return this;
        }

        @Override
        public TypeMatcher.Context build() {
            return new DefaultTypeMatcherContext(this.variableValues);
        }
    }
}
