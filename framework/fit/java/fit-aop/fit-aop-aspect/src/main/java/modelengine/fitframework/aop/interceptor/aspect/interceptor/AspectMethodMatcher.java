/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.interceptor;

import static modelengine.fitframework.inspection.Validation.lessThan;

import modelengine.fitframework.aop.interceptor.MethodInvocation;
import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.inject.AspectParameterInjectionHelper;
import modelengine.fitframework.aop.interceptor.aspect.parser.ExpressionParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import modelengine.fitframework.aop.interceptor.aspect.parser.model.ShadowMatch;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.ArgsParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.BaseParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParser;
import modelengine.fitframework.aop.interceptor.aspect.util.ExpressionUtils;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 Aspect 定义的方法拦截器定义的方法匹配器。
 *
 * @author 郭龙飞
 * @since 2023-03-08
 */
public class AspectMethodMatcher implements MethodMatcher {
    private final Map<Method, ShadowMatch> matchedMethods = new HashMap<>();
    private final List<ExpressionParser.Result> parseResults;
    private final PointcutParameter[] parameters;

    /**
     * 使用 Aspect 表达式、切面定义所在类的类型以及表达式中所有定义的参数列表来实例化 {@link AspectMethodMatcher}。
     *
     * @param expression 表示 Aspect 的匹配表达式的 {@link String}。
     * @param aspectClass 表示切面定义所在类的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param parameters 表示表达式中所有定义的参数列表的 {@link PointcutParameter}{@code []}。
     */
    public AspectMethodMatcher(String expression, Class<?> aspectClass, PointcutParameter[] parameters) {
        PointcutParser parser = new DefaultPointcutParser(expression, aspectClass, parameters);
        this.parseResults = parser.parse();
        this.parameters = parameters;
    }

    @Override
    public boolean couldMatch(Class<?> clazz) {
        List<String> boolExpressions = new ArrayList<>();
        this.parseResults.forEach(result -> {
            if (BaseParser.logicReferenceTypes.contains(result.type().getValue())) {
                boolExpressions.add(result.type().getValue());
            } else {
                boolExpressions.add(String.valueOf(result.couldMatch(clazz)));
            }
        });
        return ExpressionUtils.computeBoolExpression(boolExpressions);
    }

    @Override
    public MatchResult match(@Nonnull Method method) {
        List<String> boolExpressions = new ArrayList<>();
        Map<String, Integer> paramMapping = new HashMap<>();
        List<ExpressionParser.Result> bindingResults = new ArrayList<>();
        for (ExpressionParser.Result result : this.parseResults) {
            if (BaseParser.logicReferenceTypes.contains(result.type().getValue())) {
                boolExpressions.add(result.type().getValue());
            } else {
                boolExpressions.add(String.valueOf(result.match(method)));
            }
            if (PointcutSupportedType.ARGS.equals(result.type())) {
                ArgsParser.ArgsResult.ArgsModel content = ObjectUtils.cast(result.content());
                paramMapping = content.getParamMapping();
            }
            if (result.isBinding()) {
                bindingResults.add(result);
            }
        }
        boolean isMatched = ExpressionUtils.computeBoolExpression(boolExpressions);
        return new AspectMatchResult(isMatched, new ShadowMatch(this.parameters, paramMapping, bindingResults));
    }

    @Override
    public void choose(Method method, MatchResult result) {
        AspectMatchResult actual = Validation.isInstanceOf(result,
                AspectMatchResult.class,
                "Match result is not AspectMatchResult. [resultType={0}]",
                result.getClass().getName());
        this.matchedMethods.put(method, actual.getResult());
    }

    /**
     * 表示方法调用时传入的参数值的 {@link PointcutParameter} 列表。
     *
     * @param method 表示运行方法的 {@link Method}。
     * @param args 表示运行时参数的 {@link Object} 列表。
     * @param proxy 表示代理的执行方法 {@link MethodInvocation}。
     * @param proxied 表示被代理的执行方法 {@link MethodInvocation}。
     * @return 表示运行方法的 {@link PointcutParameter} 列表。
     */
    public PointcutParameter[] matchJoinPoint(Method method, Object[] args, MethodInvocation proxy,
            MethodInvocation proxied) {
        ShadowMatch shadowMatch = Validation.notNull(this.matchedMethods.get(method),
                "The shadow match cannot be null. [method={0}]",
                method.getName());
        Map<String, Integer> mapping = shadowMatch.getArgsNameIndex();
        PointcutParameter[] pointcutParameters = shadowMatch.getPointcutParameters();
        for (PointcutParameter parameter : pointcutParameters) {
            if (mapping.containsKey(parameter.getName())) {
                // args赋值
                Integer index = mapping.get(parameter.getName());
                lessThan(index,
                        args.length,
                        StringUtils.format("The index is out of bounds. [index={0},bound={1}]", index, args.length));
                parameter.setBinding(args[index]);
            } else {
                // 参数绑定赋值
                List<ExpressionParser.Result> bindingResults = shadowMatch.getBindingResults();
                bindingResults.stream()
                        .filter(ele -> Objects.equals(ele.content().toString(), parameter.getName()))
                        .findFirst()
                        .ifPresent(item -> parameter.setBinding(this.getBindingValue(item.type(),
                                parameter.getType(),
                                proxy,
                                proxied)));
            }
        }
        return pointcutParameters;
    }

    @Nullable
    private Object getBindingValue(PointcutSupportedType primitive, Class<?> classType, MethodInvocation proxy,
            MethodInvocation proxied) {
        Map<PointcutSupportedType, Function<PointcutSupportedType, Object>> map =
                this.getMethodMap(classType, proxy, proxied);
        Function<PointcutSupportedType, Object> bindingValue = Validation.notNull(map.get(primitive),
                StringUtils.format("Pointcut supported type error. [primitive={0}]", primitive.getValue()));
        return bindingValue.apply(primitive);
    }

    private Map<PointcutSupportedType, Function<PointcutSupportedType, Object>> getMethodMap(Class<?> classType,
            MethodInvocation proxy, MethodInvocation proxied) {
        Map<PointcutSupportedType, Function<PointcutSupportedType, Object>> methodMap = new HashMap<>();
        methodMap.put(PointcutSupportedType.TARGET, item -> proxied.getTarget());
        methodMap.put(PointcutSupportedType.THIS, item -> proxy.getTarget());
        Class<Annotation> annotationClass = ObjectUtils.cast(classType);
        methodMap.put(PointcutSupportedType.AT_TARGET, item -> {
            Class<?> declaringClass = proxied.getMethod().getDeclaringClass();
            AnnotationMetadata annotationMetadata =
                    AspectParameterInjectionHelper.getAnnotationMetadata(declaringClass);
            return annotationMetadata.getAnnotation(annotationClass);
        });
        methodMap.put(PointcutSupportedType.AT_ANNOTATION, item -> {
            Method method = proxied.getMethod();
            AnnotationMetadata annotationMetadata = AspectParameterInjectionHelper.getAnnotationMetadata(method);
            return annotationMetadata.getAnnotation(annotationClass);
        });
        methodMap.put(PointcutSupportedType.AT_ARGS, item -> {
            Class<?>[] parameterTypes = proxied.getMethod().getParameterTypes();
            return Arrays.stream(parameterTypes)
                    .map(AspectParameterInjectionHelper::getAnnotationMetadata)
                    .filter(annotationMetadata -> annotationMetadata.isAnnotationPresent(annotationClass))
                    .findFirst()
                    .map(annotationMetadata -> annotationMetadata.getAnnotation(annotationClass))
                    .orElse(null);
        });
        methodMap.put(PointcutSupportedType.AT_PARAMS, item -> this.getAtParams(proxied, annotationClass));
        // 获取本类或者父类的注解
        methodMap.put(PointcutSupportedType.AT_WITHIN, item -> {
            Class<?> declaringClass = proxied.getMethod().getDeclaringClass();
            AnnotationMetadata annotationMetadata =
                    AspectParameterInjectionHelper.getAnnotationMetadata(declaringClass);
            return annotationMetadata.getAnnotation(annotationClass);
        });
        return methodMap;
    }

    @Nullable
    private Annotation getAtParams(MethodInvocation proxied, Class<Annotation> annotationClass) {
        Method method = proxied.getMethod();
        List<Annotation> annotationList =
                Arrays.stream(method.getParameterAnnotations()).flatMap(Stream::of).collect(Collectors.toList());
        for (Annotation annotation : annotationList) {
            // 当前参数注解与期望相同
            Class<? extends Annotation> type = annotation.annotationType();
            if (type == annotationClass) {
                return annotation;
            }
            // 当前参数注解内部嵌套注解与期望相同
            AnnotationMetadata annotationMetadata = AspectParameterInjectionHelper.getAnnotationMetadata(type);
            if (annotationMetadata.isAnnotationPresent(ObjectUtils.cast(annotationClass))) {
                return annotationMetadata.getAnnotation(annotationClass);
            }
        }
        return null;
    }

    /**
     * {@link MatchResult} 的 Aspect 的实现。
     */
    public static class AspectMatchResult implements MatchResult {
        private final boolean isMatches;
        private final ShadowMatch shadowMatch;

        /**
         * 使用匹配的最终结果和 {@link ShadowMatch 匹配结果的详情} 来实例化 {@link AspectMatchResult}。
         *
         * @param isMatches 表示匹配结果的 {@code boolean}。
         * @param shadowMatch 表示匹配结果的详情的 {@link ShadowMatch}。
         */
        public AspectMatchResult(boolean isMatches, ShadowMatch shadowMatch) {
            this.isMatches = isMatches;
            this.shadowMatch = shadowMatch;
        }

        @Override
        public boolean matches() {
            return this.isMatches;
        }

        @Override
        public ShadowMatch getResult() {
            return this.shadowMatch;
        }
    }
}
