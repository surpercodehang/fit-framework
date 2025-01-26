/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.type.TypeMatcher;
import modelengine.fitframework.type.annotation.MatchTypes;
import modelengine.fitframework.util.IntegerUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.function.Supplier;

/**
 * 为对象类型和期望类型都为 {@link ParameterizedType} 的情况提供匹配判定程序。
 *
 * @author 梁济时
 * @since 2020-10-29
 */
@MatchTypes(current = ParameterizedType.class, expected = ParameterizedType.class,
        factory = ParameterizedTypeMatcher.Factory.class)
public class ParameterizedTypeMatcher extends AbstractTypeMatcher<ParameterizedType, ParameterizedType> {
    /**
     * 使用对象类型和上下文信息初始化 {@link ParameterizedTypeMatcher} 类的新实例。
     *
     * @param objectType 表示对象类型的 {@link ParameterizedType}。
     * @param context 表示上下文信息的 {@link TypeMatcher.Context}。
     */
    public ParameterizedTypeMatcher(ParameterizedType objectType, Context context) {
        super(objectType, context);
    }

    @Override
    protected boolean match0(ParameterizedType expectedType) {
        if (this.getCurrentType().getRawType() == expectedType.getRawType()) {
            return this.matchTypeArguments(this.getCurrentType().getActualTypeArguments(),
                    expectedType.getActualTypeArguments());
        }
        return this.matchSuperTypes((Class<?>) this.getCurrentType().getRawType(), expectedType);
    }

    @Override
    protected TypeMatcher.Context getSuperContext() {
        Class<?> rawClass = (Class<?>) this.getCurrentType().getRawType();
        TypeVariable<?>[] typeVariables = rawClass.getTypeParameters();
        Type[] actualTypeArguments = this.getCurrentType().getActualTypeArguments();
        TypeMatcher.Context.Builder builder = TypeMatcher.Context.builder();
        for (int i = 0; i < typeVariables.length; i++) {
            TypeVariable<?> variable = typeVariables[i];
            Type argument = actualTypeArguments[i];
            builder.setVariableValue(variable.getName(), this.supplyVariable(argument));
        }
        return builder.build();
    }

    private boolean matchTypeArguments(Type[] current, Type[] required) {
        Validation.isTrue(IntegerUtils.equals(current.length, required.length),
                "The current and required type number should be the same");
        for (int i = 0; i < current.length; i++) {
            if (!TypeMatcher.match(current[i], required[i], this.getContext())) {
                return false;
            }
        }
        return true;
    }

    private Supplier<Type> supplyVariable(Type argument) {
        if (argument instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable<?>) argument;
            return () -> this.getContext().getVariableValue(variable.getName()).orElse(null);
        } else {
            return () -> argument;
        }
    }

    /**
     * 为创建 {@link ParameterizedTypeMatcher} 实例提供工厂。
     *
     * @author 梁济时
     * @since 2020-10-29
     */
    public static class Factory implements TypeMatcher.Factory {
        @Override
        public TypeMatcher create(Type currentType, Context context) {
            return new ParameterizedTypeMatcher(ObjectUtils.cast(currentType), context);
        }
    }
}
