/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fitframework.annotation.Genericable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 泛服务的工具类。
 *
 * @author 季聿阶
 * @since 2022-06-17
 */
public class GenericableUtils {
    /** 表示 Genericable 类仅存在唯一的服务方法时的方法名字。 */
    public static final String GENERICABLE_METHOD_NAME = "process";

    private GenericableUtils() {}

    /**
     * 获取指定微服务定义的唯一标识。
     *
     * @param microGenericableClass 表示微服务类型的 {@link Class}{@code <?>}。
     * @param methodName 表示指定方法名字的 {@link String}。
     * @param parameterTypes 表示指定方法参数类型列表的 {@link Class}{@code <?>[]}。
     * @return 表示指定接口中的指定方法作为服务的唯一标识的 {@link String}。
     * @throws IllegalArgumentException 当 {@code microGenericableClass} 为 {@code null}，或当 {@code methodName}
     * 为空白字符串，或当 {@code parameterTypes} 中的类型为 {@code null} 时。
     * @throws IllegalStateException 当在 {@code microGenericableClass} 中找不到指定的方法时。
     */
    public static String getGenericableId(Class<?> microGenericableClass, String methodName,
            Class<?>[] parameterTypes) {
        notNull(microGenericableClass, "The interface class cannot be null.");
        notBlank(methodName, "The method name cannot be blank.");
        Class<?>[] actualParameterTypes = getIfNull(parameterTypes, () -> new Class<?>[0]);
        IntStream.range(0, actualParameterTypes.length)
                .forEach(index -> notNull(actualParameterTypes[index],
                        "The parameter type cannot be null. [index={0}]",
                        index));
        try {
            Method interfaceMethod = microGenericableClass.getDeclaredMethod(methodName, actualParameterTypes);
            return getMicroGenericableId(interfaceMethod);
        } catch (NoSuchMethodException e) {
            String parameters = Stream.of(actualParameterTypes).map(Class::getName).collect(Collectors.joining(","));
            String method = methodName + "(" + parameters + ")";
            throw new IllegalStateException(StringUtils.format("Failed to get interface method. [method={0}]", method),
                    e);
        }
    }

    /**
     * 获取指定方法的唯一标识。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示指定方法的唯一标识的 {@link String}。
     * @throws IllegalArgumentException 当 {@code method} 为 {@code null}，或 {@code method} 不是一个接口方法时。
     */
    public static String getGenericableId(Method method) {
        notNull(method, "The genericable method cannot be null.");
        Method interfaceMethod = ReflectionUtils.getInterfaceMethod(method)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "The method is not an interface method. [method={0}]",
                        method.getName())));
        Genericable annotation = interfaceMethod.getDeclaredAnnotation(Genericable.class);
        if (annotation == null) {
            return getMicroGenericableId(interfaceMethod);
        }
        return StringUtils.isBlank(annotation.id()) ? annotation.value() : annotation.id();
    }

    private static String getMicroGenericableId(Method interfaceMethod) {
        ReflectionUtils.Pattern pattern = new ReflectionUtils.Pattern(false, true, true, true, true);
        return ReflectionUtils.toString(interfaceMethod, pattern);
    }

    /**
     * 获取指定的类型中定义的宏服务方法。
     *
     * @param genericableClass 表示宏服务类型的 {@link Class}{@code <?>}。
     * @return 表示宏服务方法的 {@link Optional}{@code <}{@link Method}{@code >}。
     * @throws IllegalArgumentException 当 {@code genericableClass} 为 {@code null} 时。
     * @throws IllegalStateException 当 {@code genericableClass} 中定义了多个宏服务方法时。
     */
    public static Optional<Method> getMacroGenericableMethod(Class<?> genericableClass) {
        notNull(genericableClass, "Class of genericable cannot be null.");
        Method[] methods = ReflectionUtils.getDeclaredMethods(genericableClass);
        Method genericableMethod = null;
        for (Method method : methods) {
            if (isMacroGenericableMethod(method)) {
                if (genericableMethod == null) {
                    genericableMethod = method;
                } else {
                    throw new IllegalStateException(StringUtils.format(
                            "Multiple genericable methods declared. [class={0}]",
                            genericableClass.getName()));
                }
            }
        }
        return Optional.ofNullable(genericableMethod);
    }

    /**
     * 检查指定的方法是否是一个宏服务方法。
     * <p>本方法的实现默认认为 {@code Method} 被定义在宏服务接口中，不会进行该校验。</p>
     *
     * @param method 表示待检查的方法的 {@link Method}。
     * @return 若方法是一个宏服务方法，则为 {@code true}，否则为 {@code false}。
     */
    private static boolean isMacroGenericableMethod(Method method) {
        return !Modifier.isStatic(method.getModifiers()) && StringUtils.equals(method.getName(),
                GENERICABLE_METHOD_NAME);
    }
}
