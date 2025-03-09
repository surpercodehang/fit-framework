/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.type.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.type.TypeMatcher;
import modelengine.fitframework.type.annotation.MatchTypes;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 为 {@link TypeMatcher} 提供基于分发的组合模式实现。
 *
 * @author 梁济时
 * @since 2020-10-29
 */
public class TypeMatcherDispatcher implements TypeMatcher {
    private static final Map<Class<?>, Map<Class<?>, TypeMatcher.Factory>> FACTORIES = new HashMap<>();

    static {
        Arrays.asList(ClassMatcher.class,
                ClassParameterizedTypeMatcher.class,
                ParameterizedTypeClassMatcher.class,
                ParameterizedTypeMatcher.class,
                TypeVariableTypeMatcher.class).forEach(TypeMatcherDispatcher::addFactory);
    }

    private final Type currentType;
    private final TypeMatcher.Context context;

    /**
     * 使用指定的类型和上下文来初始化 {@link TypeMatcherDispatcher} 的新实例。
     *
     * @param currentType 表示待匹配的类型的 {@link Type}。
     * @param context 表示类型匹配的上下文的 {@link TypeMatcher.Context}。
     * @throws IllegalArgumentException 当 {@code currentType} 为 {@code null} 时。
     */
    public TypeMatcherDispatcher(Type currentType, TypeMatcher.Context context) {
        this.currentType = Validation.notNull(currentType, "The object type to match cannot be null.");
        this.context = ObjectUtils.nullIf(context, TypeMatcher.Context.empty());
    }

    @Override
    public boolean match(Type expectedType) {
        Validation.notNull(expectedType, "The expected type to match cannot be null.");
        return getFactory(this.currentType.getClass(),
                expectedType.getClass()).map(factory -> factory.create(this.currentType, this.context))
                .filter(matcher -> matcher.match(expectedType))
                .isPresent();
    }

    private static void addFactory(Class<?> matcherClass) {
        Validation.notNull(matcherClass, "No class to add factory.");
        MatchTypes annotation = matcherClass.getDeclaredAnnotation(MatchTypes.class);
        Validation.notNull(annotation, "No 'MatchTypes' annotation on class. [class={0}]", matcherClass.getName());
        TypeMatcher.Factory factory = ReflectionUtils.instantiate(annotation.factory());
        FACTORIES.computeIfAbsent(annotation.current(), key -> new HashMap<>()).put(annotation.expected(), factory);
    }

    private static Optional<TypeMatcher.Factory> getFactory(Class<?> objectClassType, Class<?> expectedClassType) {
        return map(FACTORIES, objectClassType).flatMap(factories -> map(factories, expectedClassType));
    }

    private static <V> Optional<V> map(Map<Class<?>, V> map, Class<?> expectedClass) {
        return map.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAssignableFrom(expectedClass))
                .map(Map.Entry::getValue)
                .findAny();
    }
}
