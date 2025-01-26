/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.interceptor.inject;

import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.interceptor.MethodInvocation;
import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.aop.interceptor.MethodPointcut;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.AspectMethodMatcher;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.type.support.DefaultProceedingJoinPoint;
import modelengine.fitframework.aop.interceptor.support.AbstractAdviceMethodInterceptor;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolvers;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Aspect 参数注入的帮助类。
 *
 * @author 季聿阶
 * @author 郭龙飞
 * @since 2023-03-08
 */
public class AspectParameterInjectionHelper {
    private static final Set<Class<?>> SPECIAL_TYPES = new HashSet<>();
    private static final String ARG_NAMES_SEPARATOR = ",";
    private static final AnnotationMetadataResolver ANNOTATION_RESOLVER = AnnotationMetadataResolvers.create();

    static {
        SPECIAL_TYPES.add(ProceedingJoinPoint.class);
        SPECIAL_TYPES.add(JoinPoint.class);
        SPECIAL_TYPES.add(JoinPoint.StaticPart.class);
    }

    /**
     * 参数名称字符串转换为参数数组。
     *
     * @param argNames 参数名字符串。
     * @return 返回参数的 {@link String} {@code []}。
     */
    public static String[] toArgNames(String argNames) {
        return Arrays.stream(StringUtils.split(argNames, ARG_NAMES_SEPARATOR))
                .map(String::trim).toArray(String[]::new);
    }

    /**
     * 判断指定类型是不是 Aspect 内置的注入类型。
     * <p>Aspect 内置的注入类型有 3 种：
     * <ul>
     *     <li>{@link ProceedingJoinPoint}</li>
     *     <li>{@link JoinPoint}</li>
     *     <li>{@link JoinPoint.StaticPart}</li>
     * </ul>
     * </p>
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 如果是 Aspect 内置的注入类型，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isSpecialType(Class<?> clazz) {
        return SPECIAL_TYPES.contains(clazz);
    }

    /**
     * 获取调用拦截点的所有注入后的参数。
     *
     * @param method 表示拦截方法的 {@link Method}。
     * @param argNames 表示拦截方法上面定义的所有参数的名字数组的 {@link String}{@code []}。
     * @param parameterInjection 表示参数的注入信息的 {@link ParameterInjection}。
     * @param returnInjection 表示返回值的注入信息的 {@link ValueInjection}。
     * @param throwInjection 表示异常的注入信息的 {@link ValueInjection}。
     * @return 表示调用拦截点的所有注入后参数的 {@link Object}{@code []}。
     */
    public static Object[] getInjectionArgs(Method method, String[] argNames, ParameterInjection parameterInjection,
            @Nullable ValueInjection returnInjection, @Nullable ValueInjection throwInjection) {
        if (method.getParameterCount() == 0) {
            return AbstractAdviceMethodInterceptor.EMPTY_ARGS;
        }
        LazyLoader<Object> specialInjection =
                new LazyLoader<>(() -> new DefaultProceedingJoinPoint(parameterInjection.getJoinPoint()));
        LazyLoader<Map<String, Object>> argNameParameterMapping = new LazyLoader<>(() -> calArgNameParameterMapping(
                parameterInjection.getPointcut(),
                parameterInjection.getJoinPoint()));
        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            Class<?> type = method.getParameterTypes()[i];
            if (i == 0 && isSpecialType(type)) {
                // 约束：只有第一个参数有可能是内置参数
                args[i] = specialInjection.get();
            } else if (returnInjection != null && Objects.equals(argNames[i], returnInjection.getName())) {
                args[i] = returnInjection.getValue();
            } else if (throwInjection != null && Objects.equals(argNames[i], throwInjection.getName())) {
                args[i] = throwInjection.getValue();
            } else {
                args[i] = argNameParameterMapping.get().get(argNames[i]);
            }
        }
        return args;
    }

    private static Map<String, Object> calArgNameParameterMapping(MethodPointcut pointcut, MethodJoinPoint joinPoint) {
        AspectMethodMatcher methodMatcher = pointcut.matchers()
                .all()
                .stream()
                .filter(matcher -> matcher instanceof AspectMethodMatcher)
                .map(AspectMethodMatcher.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No AspectMethodMatcher."));
        MethodInvocation proxied = joinPoint.getProxiedInvocation();
        Object[] arguments = proxied.getArguments();
        Method method = proxied.getMethod();
        MethodInvocation proxy = joinPoint.getProxyInvocation();
        PointcutParameter[] pointcutParameter = methodMatcher.matchJoinPoint(method, arguments, proxy, proxied);
        return Stream.of(pointcutParameter)
                .collect(Collectors.toMap(PointcutParameter::getName, PointcutParameter::getBinding));
    }

    /**
     * 获取方法注解提供元数据定义。
     *
     * @param annotatedElement 待解析元数据的 {@link AnnotatedElement}。
     * @return 表示方法注解提供元数据定义。
     * @throws IllegalArgumentException {@code annotatedElement} 为 {@code null}。
     * @see Class
     * @see Method
     * @see java.lang.reflect.AnnotatedType
     * @see java.lang.reflect.Field
     * @see java.lang.reflect.Parameter
     */
    public static AnnotationMetadata getAnnotationMetadata(AnnotatedElement annotatedElement) {
        Validation.notNull(annotatedElement, "The annotatedElement cannot be null.");
        return ANNOTATION_RESOLVER.resolve(annotatedElement);
    }
}
