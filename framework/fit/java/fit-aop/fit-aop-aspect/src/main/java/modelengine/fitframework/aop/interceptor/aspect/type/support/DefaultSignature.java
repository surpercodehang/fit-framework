/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.type.support;

import modelengine.fitframework.aop.MethodSignature;
import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 通过 {@link MethodJoinPoint} 适配实现 {@link MethodSignature}。
 *
 * @author 季聿阶
 * @since 2022-05-19
 */
public class DefaultSignature implements MethodSignature {
    private final MethodJoinPoint joinPoint;

    /**
     * 使用运行时的方法连接点来实例化 {@link DefaultSignature}。
     *
     * @param joinPoint 表示运行时的方法连接点的 {@link MethodJoinPoint}。
     */
    public DefaultSignature(MethodJoinPoint joinPoint) {
        this.joinPoint = Validation.notNull(joinPoint, "The method join point cannot be null.");
    }

    @Override
    public String getName() {
        return this.getMethod().getName();
    }

    @Override
    public int getModifiers() {
        return this.getMethod().getModifiers();
    }

    @Override
    public Class<?> getDeclaringType() {
        return this.getMethod().getDeclaringClass();
    }

    @Override
    public String getDeclaringTypeName() {
        return this.getDeclaringType().getName();
    }

    @Override
    public String toShortString() {
        return ReflectionUtils.toShortString(this.getMethod());
    }

    @Override
    public String toLongString() {
        return ReflectionUtils.toLongString(this.getMethod());
    }

    @Override
    public String toString() {
        return ReflectionUtils.toString(this.getMethod());
    }

    @Override
    public Class<?> getReturnType() {
        return this.getMethod().getReturnType();
    }

    @Override
    public Method getMethod() {
        return this.joinPoint.getProxiedInvocation().getMethod();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return this.getMethod().getParameterTypes();
    }

    @Override
    public String[] getParameterNames() {
        Parameter[] parameters = this.getMethod().getParameters();
        String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterNames[i] = parameters[i].getName();
        }
        return parameterNames;
    }

    @Override
    public Class<?>[] getExceptionTypes() {
        return this.getMethod().getExceptionTypes();
    }
}
