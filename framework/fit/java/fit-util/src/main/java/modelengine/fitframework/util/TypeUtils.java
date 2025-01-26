/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;

/**
 * 为类型提供工具方法。
 *
 * @author 梁济时
 * @since 2022-07-05
 */
public final class TypeUtils {
    /**
     * 表示空的类型数组。
     */
    public static final Type[] EMPTY_ARRAY = new Type[0];

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private TypeUtils() {}

    /**
     * 将 {@link Type} 转换为 {@link Class}{@code <}{@link Object}{@code >}。
     *
     * @param type 表示待转换的类型的 {@link Type}。
     * @return 表示转换后的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     */
    public static Class<?> toClass(Type type) {
        if (type instanceof Class) {
            return ObjectUtils.cast(type);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = ObjectUtils.cast(type);
            return ObjectUtils.cast(parameterizedType.getRawType());
        } else {
            throw new IllegalStateException(StringUtils.format("Not support type. [type={0}]",
                    type.getClass().getName()));
        }
    }

    /**
     * 若类型是泛型类，则使用默认的约束将其转为参数化类型。
     *
     * @param clazz 表示原始类型的 {@link Class}。
     * @return 若是泛型类，则返回其使用默认约束的参数化类型的 {@link ParameterizedType}，否则返回表示原始类型的 {@link Class}。
     */
    public static Type withDefault(Class<?> clazz) {
        notNull(clazz, "The class to try to make parameterized type cannot be null.");
        TypeVariable<?>[] parameters = clazz.getTypeParameters();
        if (parameters.length < 1) {
            return clazz;
        }
        Type[] arguments = new Type[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            TypeVariable<?> parameter = parameters[i];
            arguments[i] = wildcard(parameter.getBounds(), null);
        }
        return parameterized(clazz, arguments);
    }

    /**
     * 获取类型变量在定义处的定义位置。
     * <p>当类型变量找不到定义位置时，返回 {@code -1}。</p>
     *
     * @param typeVariable 表示类型变量的 {@link TypeVariable}{@code <?>}。
     * @return 表示类型变量在定义处的定义位置 {@code int}。
     */
    public static int getTypeVariableIndex(TypeVariable<?> typeVariable) {
        TypeVariable<?>[] typeVariables = typeVariable.getGenericDeclaration().getTypeParameters();
        for (int i = 0; i < typeVariables.length; i++) {
            if (Objects.equals(typeVariable, typeVariables[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 生成一个通配符类型。
     *
     * @param upperBounds 表示指定上限类型数组的 {@link Type}{@code []}。
     * @param lowerBounds 表示指定下限类型数组的 {@link Type}{@code []}。
     * @return 表示生成的通配符类型的 {@link WildcardType}。
     */
    public static WildcardType wildcard(Type[] upperBounds, Type[] lowerBounds) {
        return new Wildcard(upperBounds, lowerBounds);
    }

    /**
     * 生成一个参数化类型。
     *
     * @param rawClass 表示参数化类型的原始类型的 {@link Class}{@code <?>}。
     * @param arguments 表示参数化类型的类型参数数组的 {@link Type}{@code []}。
     * @return 表示生成的参数化类型的 {@link ParameterizedType}。
     */
    public static ParameterizedType parameterized(Class<?> rawClass, Type[] arguments) {
        return parameterized(rawClass, arguments, null);
    }

    /**
     * 生成一个参数化类型。
     *
     * @param rawClass 表示参数化类型的原始类型的 {@link Class}{@code <?>}。
     * @param arguments 表示参数化类型的类型参数数组的 {@link Type}{@code []}。
     * @param ownerType 表示定义了该参数化类型的类型 {@link Type}。
     * @return 表示生成的参数化类型的 {@link ParameterizedType}。
     */
    public static ParameterizedType parameterized(Class<?> rawClass, Type[] arguments, Type ownerType) {
        return new Parameterized(ownerType, rawClass, arguments);
    }

    private static class Wildcard implements WildcardType {
        private final Type[] upperBounds;
        private final Type[] lowerBounds;

        private Wildcard(Type[] upperBounds, Type[] lowerBounds) {
            this.upperBounds = ObjectUtils.nullIf(upperBounds, EMPTY_ARRAY);
            this.lowerBounds = ObjectUtils.nullIf(lowerBounds, EMPTY_ARRAY);
            if (this.upperBounds.length > 0 && this.lowerBounds.length > 0) {
                throw new IllegalArgumentException("A wildcard type cannot contain both upper and lower bounds.");
            }
        }

        @Override
        public Type[] getUpperBounds() {
            return this.upperBounds;
        }

        @Override
        public Type[] getLowerBounds() {
            return this.lowerBounds;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append('?');
            if (this.upperBounds.length > 0) {
                builder.append(" extends ").append(this.upperBounds[0].getTypeName());
                for (int i = 1; i < this.upperBounds.length; i++) {
                    builder.append(" & ").append(this.upperBounds[i].getTypeName());
                }
            }
            if (this.lowerBounds.length > 0) {
                builder.append(" super ").append(this.lowerBounds[0].getTypeName());
                for (int i = 1; i < this.lowerBounds.length; i++) {
                    builder.append(" & ").append(this.lowerBounds[i].getTypeName());
                }
            }
            return builder.toString();
        }
    }

    private static class Parameterized implements ParameterizedType {
        private final Type ownerType;
        private final Class<?> rawClass;
        private final Type[] arguments;

        private Parameterized(Type ownerType, Class<?> rawClass, Type[] arguments) {
            this.ownerType = ownerType;
            notNull(rawClass, "The raw class of a parameterized type cannot be null.");
            notNull(arguments, "The arguments of a parameterized type cannot be null.");
            if (rawClass.getTypeParameters().length < 1) {
                throw new IllegalArgumentException(StringUtils.format(
                        "The raw class of a parameterized type does not contain any type parameters. [raw={0}]",
                        rawClass.getTypeName()));
            }
            if (rawClass.getTypeParameters().length != arguments.length) {
                throw new IllegalArgumentException(StringUtils.format(
                        "The number of arguments does not match required. [raw={0}, required={1}, actual={2}]",
                        rawClass.getTypeName(),
                        rawClass.getTypeParameters().length,
                        arguments.length));
            }
            this.rawClass = rawClass;
            this.arguments = new Type[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                this.arguments[i] = ObjectUtils.nullIf(arguments[i], Object.class);
            }
        }

        @Override
        public Type getOwnerType() {
            return this.ownerType;
        }

        @Override
        public Type getRawType() {
            return this.rawClass;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return this.arguments;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.arguments) ^ Objects.hashCode(this.rawClass) ^ Objects.hashCode(this.ownerType);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof ParameterizedType) {
                ParameterizedType another = (ParameterizedType) obj;
                return Objects.equals(this.getOwnerType(), another.getOwnerType()) && Objects.equals(this.getRawType(),
                        another.getRawType()) && Arrays.equals(this.getActualTypeArguments(),
                        another.getActualTypeArguments());
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.rawClass.getTypeName()).append('<').append(this.arguments[0].getTypeName());
            for (int i = 1; i < this.arguments.length; i++) {
                builder.append(", ").append(this.arguments[i].getTypeName());
            }
            return builder.append('>').toString();
        }
    }
}
