/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import static modelengine.fitframework.inspection.Validation.isTrue;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanCreationException;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreator;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link BeanCreator} 提供基于构造方法的实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-04-28
 */
public class ConstructorBeanCreator extends AbstractBeanCreator {
    private final Constructor<?> constructor;

    /**
     * 使用 Bean 元数据及实例化 Bean 所使用的构造方法初始化 {@link ConstructorBeanCreator} 类的新实例。
     *
     * @param metadata 表示 Bean 元数据的 {@link BeanMetadata}。
     * @param constructor 表示实例化Bean所使用的构造方法的 {@link Constructor}。
     * @throws IllegalArgumentException 当 {@code constructor} 为 {@code null} 时。
     */
    public ConstructorBeanCreator(BeanMetadata metadata, Constructor<?> constructor) {
        super(metadata);
        this.constructor = Validation.notNull(constructor, "The constructor to create bean cannot be null.");
        this.constructor.setAccessible(true);
    }

    @Override
    public Object create(Object[] arguments) {
        Arguments resolvedArguments = this.arguments(this.constructor, arguments);
        Object[] actual = resolvedArguments.get();
        isTrue(resolvedArguments.isComplete(),
                () -> exception(this.constructor, actual, resolvedArguments.getMessage()));
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.constructor.getDeclaringClass().getClassLoader());
            return this.constructor.newInstance(actual);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanCreationException(StringUtils.format(
                    "Failed to instantiate bean with specific constructor. [class={0}, constructor={1}]",
                    this.constructor.getDeclaringClass().getName(),
                    this.constructor), e);
        } catch (InvocationTargetException e) {
            throw new BeanCreationException(StringUtils.format(
                    "Failed to instantiate bean with specific constructor. [class={0}, constructor={1}]",
                    this.constructor.getDeclaringClass().getName(),
                    this.constructor), e.getCause());
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    private static BeanCreationException exception(Constructor<?> constructor, Object[] arguments, String message) {
        String parameterTypes = Stream.of(constructor.getParameters())
                .map(parameter -> parameter.getParameterizedType().getTypeName())
                .collect(Collectors.joining(", "));
        String argumentValues = Stream.of(arguments).map(Objects::toString).collect(Collectors.joining(", ", "[", "]"));
        return new BeanCreationException(StringUtils.format(
                "Arguments mismatched: {0}. [constructor={1}({2}), arguments={3}]",
                message,
                constructor.getDeclaringClass().getName(),
                parameterTypes,
                argumentValues));
    }
}
