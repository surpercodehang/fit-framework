/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree.support;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * 为注解的动态代理提供拦截程序。
 *
 * @author 梁济时
 * @since 2022-05-03
 */
class AnnotationInvocationHandler implements InvocationHandler {
    private final Class<? extends Annotation> annotation;
    private final Map<String, Object> properties;

    /**
     * 使用待拦截注解的类型和属性集初始化 {@link AnnotationInvocationHandler} 类的新实例。
     *
     * @param annotation 表示注解类型的 {@link Class}。
     * @param properties 表示注解的属性集的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    AnnotationInvocationHandler(Class<? extends Annotation> annotation, Map<String, Object> properties) {
        this.annotation = annotation;
        this.properties = properties;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.equals(Annotation.class.getMethod("annotationType"))) {
            return this.annotation;
        }
        if (method.getDeclaringClass() == Object.class) {
            switch (method.getName()) {
                case "toString":
                    return StringUtils.format("{0}{1}", this.annotation.getName(), this.properties);
                case "hashCode":
                    return Arrays.hashCode(new Object[] {this.annotation, this.properties});
                case "equals":
                    return this.invokeEquals(method, args);
                default:
                    return method.invoke(properties, args);
            }
        } else {
            return properties.get(method.getName());
        }
    }

    private Object invokeEquals(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        if (this.annotation.isInstance(args[0])) {
            for (Method propertyMethod : this.annotation.getDeclaredMethods()) {
                if (!Objects.equals(propertyMethod.invoke(args[0]), this.properties.get(method.getName()))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    static <T extends Annotation> T proxy(Class<T> type, Map<String, Object> data) {
        return ObjectUtils.cast(Proxy.newProxyInstance(type.getClassLoader(),
                new Class<?>[] {type},
                new AnnotationInvocationHandler(type, data)));
    }
}
