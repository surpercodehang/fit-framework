/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.json.schema.util;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link modelengine.fitframework.json.schema.JsonSchema} 类型的工具类。
 *
 * @author 季聿阶
 * @since 2023-08-26
 */
public class SchemaTypeUtils {
    private static final String TYPE_NAME_OF = "_of_";
    private static final String TYPE_NAME_AND = "_and_";
    private static final String JAVA_PACKAGE_PREFIX = "java.";

    private SchemaTypeUtils() {}

    private static Set<Type> getObjectTypes(Type type, Set<Type> visited) {
        if (visited.contains(type)) {
            return Collections.emptySet();
        }
        visited.add(type);
        if (type instanceof Class) {
            Class<?> clazz = cast(type);
            if (clazz.isArray()) {
                clazz = clazz.getComponentType();
            }
            if (isObjectType(clazz)) {
                Set<Type> types = getFieldTypes(clazz, visited);
                types.add(clazz);
                return types;
            } else {
                return Collections.emptySet();
            }
        } else if (type instanceof ParameterizedType) {
            Set<Type> types = new HashSet<>();
            if (isObjectType(type)) {
                types.add(type);
            }
            ParameterizedType actual = cast(type);
            return Stream.of(actual.getActualTypeArguments())
                    .map((Type actualType) -> getObjectTypes(actualType, visited))
                    .reduce(types, (s1, s2) -> {
                        s1.addAll(s2);
                        return s1;
                    });
        } else {
            return Collections.emptySet();
        }
    }

    private static Set<Type> getFieldTypes(Class<?> clazz, Set<Type> visited) {
        Field[] fields = clazz.getDeclaredFields();
        Set<Type> types = new HashSet<>();
        for (Field field : fields) {
            types.addAll(getObjectTypes(field.getGenericType(), visited));
        }
        return types;
    }

    /**
     * 判断指定类型是否为对象的类型。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @return 如果指定类型是对象的类型，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isObjectType(Type type) {
        if (type instanceof Class) {
            Class<?> actual = ReflectionUtils.ignorePrimitiveClass(cast(type));
            if (actual == void.class) {
                return false;
            }
            if (Map.class.isAssignableFrom(actual)) {
                return true;
            }
            if (Enum.class.isAssignableFrom(actual) || List.class.isAssignableFrom(actual)) {
                return false;
            }
            return !actual.getName().startsWith(JAVA_PACKAGE_PREFIX);
        } else if (type instanceof ParameterizedType) {
            Class<?> raw = cast(((ParameterizedType) type).getRawType());
            return !List.class.isAssignableFrom(raw);
        } else {
            return false;
        }
    }

    /**
     * 判断指定类型是否为数组的类型。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @return 如果指定类型是数组的类型，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isArrayType(Type type) {
        if (type instanceof Class) {
            Class<?> actual = cast(type);
            return actual.isArray() || List.class.isAssignableFrom(actual);
        } else if (type instanceof ParameterizedType) {
            Class<?> raw = cast(((ParameterizedType) type).getRawType());
            return List.class.isAssignableFrom(raw);
        } else {
            return false;
        }
    }

    /**
     * 判断指定类型是否为枚举的类型。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @return 如果指定类型是枚举的类型，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isEnumType(Type type) {
        if (type instanceof Class) {
            Class<?> actual = cast(type);
            return actual.isEnum();
        } else {
            return false;
        }
    }

    /**
     * 获取指定类型的名字。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @return 表示指定类型名字的 {@link String}。
     * @throws IllegalArgumentException 当 {@code type} 为 {@code null} 时。
     */
    public static String getTypeName(Type type) {
        notNull(type, "The type cannot be null.");
        if (type instanceof Class) {
            Class<?> actual = cast(type);
            if (actual.isArray()) {
                Class<?> componentType = actual.getComponentType();
                return componentType.getSimpleName() + "Array";
            } else {
                return actual.getSimpleName();
            }
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            String rawTypeName = parameterizedType.getRawType().getTypeName();
            return rawTypeName + TYPE_NAME_OF + Stream.of(parameterizedType.getActualTypeArguments())
                    .map(SchemaTypeUtils::getTypeName)
                    .collect(Collectors.joining(TYPE_NAME_AND));
        }
        return type.getTypeName();
    }
}
