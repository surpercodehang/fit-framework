/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.interceptor;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.aop.interceptor.AdviceMethodInterceptor;
import modelengine.fitframework.aop.interceptor.MethodInterceptorFactory;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.inject.AspectParameterInjectionHelper;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;
import modelengine.fitframework.aop.interceptor.aspect.parser.support.DefaultPointcutParameter;
import modelengine.fitframework.aop.interceptor.support.AbstractAnnotatedInterceptorFactory;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 将带有 Aspect 特定注解的方法包装成 {@link AdviceMethodInterceptor}。
 *
 * @author 季聿阶
 * @author 郭龙飞
 * @since 2023-03-08
 */
public abstract class AbstractAspectInterceptorFactory extends AbstractAnnotatedInterceptorFactory
        implements MethodInterceptorFactory {
    private static final PointcutParameter[] EMPTY_PARAMETERS = new PointcutParameter[0];
    private static final String[] EMPTY_STRING = new String[0];

    protected AbstractAspectInterceptorFactory(Class<? extends Annotation> annotationClass) {
        super(annotationClass);
    }

    @Override
    public AdviceMethodInterceptor create(BeanFactory factory, @Nonnull Method interceptMethod) {
        isTrue(this.isInterceptMethod(interceptMethod), "The method is not an intercept method.");
        AdviceMethodInterceptor methodInterceptor = this.createConcreteMethodInterceptor(factory, interceptMethod);
        methodInterceptor.getPointCut()
                .matchers()
                .add(new AspectMethodMatcher(notBlank(this.getExpression(interceptMethod),
                        "The expression value cannot be blank.[method={0}]",
                        interceptMethod.getName()),
                        TypeUtils.toClass(factory.metadata().type()),
                        this.getPointcutParameters(interceptMethod)));
        return methodInterceptor;
    }

    /**
     * 根据指定方法以及其所在的 Bean 的工厂，创建一个具体的方法拦截器。
     *
     * @param aspectFactory 表示调用指定方法的对象的工厂的 {@link BeanFactory}。
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示包装后的具体方法拦截器的 {@link AdviceMethodInterceptor}。
     */
    protected abstract AdviceMethodInterceptor createConcreteMethodInterceptor(BeanFactory aspectFactory,
            Method method);

    /**
     * 获取指定方法上面定义的拦截表达式。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示指定方法上面定义的拦截表达式的 {@link String}。
     */
    protected abstract String getExpression(@Nonnull Method method);

    /**
     * 获取指定方法上面定义的参数名字及其类型。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示指定方法上面的参数名字及其类型的数组的 {@link PointcutParameter}{@code []}。
     */
    private PointcutParameter[] getPointcutParameters(@Nonnull Method method) {
        String rawArgNames = this.getArgNames(method);
        String[] argNames = StringUtils.isBlank(rawArgNames)
                ? EMPTY_STRING
                : AspectParameterInjectionHelper.toArgNames(rawArgNames);
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1 && AspectParameterInjectionHelper.isSpecialType(parameterTypes[0])) {
            Validation.lessThanOrEquals(argNames.length,
                    1,
                    "The argsName num should less or equals 1. [method={0}]",
                    method.getName());
        } else {
            Validation.equals(argNames.length,
                    method.getParameterCount(),
                    "The argsName num does not match the pointcut parameters count. [method={0}]",
                    method.getName());
        }
        List<PointcutParameter> pointcutParameters = new ArrayList<>();
        for (int i = 0; i < argNames.length; i++) {
            if (i == 0 && AspectParameterInjectionHelper.isSpecialType(parameterTypes[i])) {
                // 约束：只有第一个参数有可能是内置参数
                continue;
            }
            if (this.shouldIgnore(method, argNames[i])) {
                continue;
            }
            PointcutParameter pointcutParameter = new DefaultPointcutParameter(argNames[i], parameterTypes[i]);
            pointcutParameters.add(pointcutParameter);
        }
        return pointcutParameters.toArray(EMPTY_PARAMETERS);
    }

    /**
     * 获取指定方法上面定义的参数名字列表。
     * <p>参数名字列表是一个以半角逗号分隔的字符串。</p>
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示指定方法上面定义的参数名字列表的 {@link String}。
     */
    protected abstract String getArgNames(@Nonnull Method method);

    /**
     * 判断指定参数名字的参数是否可以忽略。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @param argName 表示指定参数名字的 {@link String}。
     * @return 如果应该忽略，返回 {@code true}，否则，返回 {@code false}。
     */
    protected boolean shouldIgnore(@Nonnull Method method, String argName) {
        return false;
    }
}
