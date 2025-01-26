/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanCreationException;
import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * 为 {@link BeanInjector} 提供基于方法注入的实现。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public class MethodBeanInjector implements BeanInjector {
    private final Method method;
    private final Object[] arguments;

    /**
     * 使用待注入的方法和值初始化 {@link MethodBeanInjector} 类的新实例。
     *
     * @param method 表示待注入的方法的 {@link Method}。
     * @param arguments 表示待注入到方法中的指定 {@link Object}{@code []}。
     * @throws IllegalArgumentException {@code method} 为 {@code null}。
     * @throws BeanDefinitionException {@code method} 被 {@code static} 修饰。
     */
    public MethodBeanInjector(Method method, Object[] arguments) {
        this.method = Validation.notNull(method, "The method to inject cannot be null.");
        if (Modifier.isStatic(method.getModifiers())) {
            throw new BeanDefinitionException(StringUtils.format(
                    "Cannot inject a bean with a static method. [method={0}]",
                    ReflectionUtils.toString(method)));
        } else {
            this.method.setAccessible(true);
            this.arguments = ObjectUtils.nullIf(arguments, new Object[0]);
        }
    }

    @Override
    public void inject(Object bean) {
        Object[] actualArguments = this.arguments;
        for (int i = 0; i < actualArguments.length; i++) {
            actualArguments[i] = ValueSupplier.real(actualArguments[i]);
        }
        try {
            method.invoke(bean, actualArguments);
        } catch (IllegalAccessException e) {
            throw new BeanCreationException(StringUtils.format(
                    "Failed to inject value by method. [method={0}, values={1}]",
                    ReflectionUtils.toString(this.method),
                    Arrays.toString(actualArguments)), e);
        } catch (InvocationTargetException e) {
            throw new BeanCreationException(StringUtils.format(
                    "Failed to inject value by method. [method={0}, values={1}]",
                    ReflectionUtils.toString(this.method),
                    Arrays.toString(actualArguments)), e.getCause());
        }
    }
}
