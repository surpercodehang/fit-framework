/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.lifecycle.bean.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanCreationException;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreator;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 为 {@link BeanCreator} 提供指定类型的Bean的创建程序。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-05-31
 */
public class ClassBeanCreator extends AbstractBeanCreator {
    private final Class<?> beanClass;

    /**
     * 使用 Bean 的元数据和 Bean 的类型创建 {@link ClassBeanCreator} 类的新实例。
     *
     * @param metadata 表示 Bean 的元数据的 {@link BeanMetadata}。
     * @param beanClass 表示 Bean 类型的 {@link Class}。
     * @throws IllegalArgumentException 当 {@code beanClass} 为 {@code null} 或是一个接口时。
     */
    public ClassBeanCreator(BeanMetadata metadata, Class<?> beanClass) {
        super(metadata);
        Validation.notNull(beanClass, "The class of bean to create cannot be null.");
        if (beanClass.isInterface()) {
            throw new IllegalArgumentException("The class of bean to create cannot be an interface.");
        } else if (beanClass.isArray()) {
            throw new IllegalArgumentException("The class of bean to create cannot be an array.");
        } else {
            this.beanClass = beanClass;
        }
    }

    @Override
    public Object create(Object[] arguments) {
        return Stream.of(this.beanClass.getDeclaredConstructors())
                .map(constructor -> this.create(constructor, arguments))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new BeanCreationException(StringUtils.format(
                        "No matched constructor to create bean. [type={0}, argumentClasses={1}]",
                        this.beanClass.getName(),
                        Arrays.toString(arguments))));
    }

    private Optional<Object> create(Constructor<?> constructor, Object[] arguments) {
        Arguments resolvedArguments = this.arguments(constructor, arguments);
        if (!resolvedArguments.isComplete()) {
            return Optional.empty();
        }
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(constructor.getDeclaringClass().getClassLoader());
            return Optional.of(constructor.newInstance(resolvedArguments.get()));
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanCreationException(StringUtils.format("Failed to instantiate bean. [error={0}]",
                    e.getMessage()), e);
        } catch (InvocationTargetException e) {
            throw new BeanCreationException(StringUtils.format("Failed to instantiate bean. [error={0}]",
                    e.getMessage()), e.getCause());
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
}
