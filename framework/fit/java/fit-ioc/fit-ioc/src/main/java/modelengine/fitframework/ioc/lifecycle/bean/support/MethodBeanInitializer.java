/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanCreationException;
import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 为 {@link BeanInitializer} 提供基于方法进行初始化的实现。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public class MethodBeanInitializer implements BeanInitializer {
    private final Method method;

    /**
     * 使用初始化方法初始化 {@link MethodBeanInitializer} 类的新实例。
     *
     * @param method 表示初始化方法的 {@link Method}。
     * @throws IllegalArgumentException {@code method} 为 {@code null}。
     * @throws BeanDefinitionException {@code method} 被 {@code static} 修饰。
     */
    public MethodBeanInitializer(Method method) {
        this.method = Validation.notNull(method, "The method to initialize bean cannot be null.");
        if (Modifier.isStatic(this.method.getModifiers())) {
            throw new BeanDefinitionException(StringUtils.format(
                    "Cannot initialize a bean with a static method. [method={0}]",
                    ReflectionUtils.toString(method)));
        } else if (this.method.getParameterCount() > 0) {
            throw new BeanDefinitionException(StringUtils.format(
                    "Cannot initialize a bean with a method contains parameters. [method={0}]",
                    ReflectionUtils.toString(method)));
        } else {
            this.method.setAccessible(true);
        }
    }

    @Override
    public void initialize(Object bean) {
        try {
            method.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new BeanCreationException(StringUtils.format("Failed to initialize bean. [method={0}, values={1}]",
                    ReflectionUtils.toString(this.method)), e);
        } catch (InvocationTargetException e) {
            throw new BeanCreationException(StringUtils.format("Failed to initialize bean. [method={0}, values={1}]",
                    ReflectionUtils.toString(this.method),
                    e.getCause()));
        }
    }
}
