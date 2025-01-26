/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.BeanDependency;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyers;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializers;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjectors;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 为通过注解的 {@link BeanResolver} 提供基类。
 *
 * @author 梁济时
 * @since 2022-06-28
 */
public abstract class AbstractAnnotatedBeanResolver implements BeanResolver {
    private static AnnotationMetadata annotations(BeanContainer container, AnnotatedElement element) {
        return container.runtime().resolverOfAnnotations().resolve(element);
    }

    private static AnnotationMetadata annotations(BeanMetadata metadata, AnnotatedElement element) {
        return annotations(metadata.container(), element);
    }

    /**
     * 解析依赖。
     *
     * @param metadata 表示待解析依赖的Bean的元数据的 {@link BeanMetadata}。
     * @param annotations 表示注解元数据的 {@link AnnotationMetadata}。
     * @param type 表示所需依赖的类型的 {@link Type}。
     * @return 若存在依赖，则为表示依赖的 {@link Optional}{@code <}{@link BeanDependency}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    protected abstract Optional<BeanDependency> dependency(BeanMetadata metadata, AnnotationMetadata annotations,
            Type type);

    /**
     * 检查指定的方法是否用于注入。
     *
     * @param metadata 表示Bean的元数据的 {@link BeanMetadata}。
     * @param method 表示候选方法的 {@link Method}。
     * @return 若方法用于注入，则为 {@code true}；否则为 {@code false}。
     */
    protected abstract boolean isInjector(BeanMetadata metadata, Method method);

    /**
     * 检查指定的方法是否用于初始化。
     *
     * @param metadata 表示Bean的元数据的 {@link BeanMetadata}。
     * @param method 表示候选方法的 {@link Method}。
     * @return 若方法用于初始化，则为 {@code true}；否则为 {@code false}。
     */
    protected abstract boolean isInitializer(BeanMetadata metadata, Method method);

    /**
     * 检查指定的方法是否用于释放资源。
     *
     * @param metadata 表示Bean的元数据的 {@link BeanMetadata}。
     * @param method 表示候选方法的 {@link Method}。
     * @return 若方法用于释放资源，则为 {@code true}；否则为 {@code false}。
     */
    protected abstract boolean isDestroyer(BeanMetadata metadata, Method method);

    @Override
    public Optional<ValueSupplier> parameter(BeanMetadata metadata, Parameter parameter) {
        return this.dependency(metadata, annotations(metadata, parameter), parameter.getParameterizedType())
                .map(BeanDependencySupplier::new);
    }

    @Override
    public Optional<BeanInjector> injector(BeanMetadata metadata, Field field) {
        return this.dependency(metadata, annotations(metadata, field), field.getGenericType())
                .map(BeanDependencySupplier::new)
                .map(supplier -> BeanInjectors.field(field, supplier));
    }

    @Override
    public Optional<BeanInjector> injector(BeanMetadata metadata, Method method) {
        if (!this.isInjector(metadata, method)) {
            return Optional.empty();
        }
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 1) {
            throw new BeanDefinitionException(StringUtils.format(
                    "The method used to inject must contain and only contain 1 parameter. [method={0}]",
                    ReflectionUtils.toString(method)));
        }
        return this.dependency(metadata, annotations(metadata, method), parameters[0].getParameterizedType())
                .map(BeanDependencySupplier::new)
                .map(supplier -> BeanInjectors.method(method, supplier));
    }

    @Override
    public Optional<BeanInitializer> initializer(BeanMetadata metadata, Method method) {
        if (!this.isInitializer(metadata, method)) {
            return Optional.empty();
        } else if (method.getParameterCount() > 0) {
            throw new BeanDefinitionException(StringUtils.format(
                    "The method used to initialize cannot contain any parameter. [method={0}]",
                    ReflectionUtils.toString(method)));
        } else {
            return Optional.of(BeanInitializers.method(method));
        }
    }

    @Override
    public Optional<BeanDestroyer> destroyer(BeanMetadata metadata, Method method) {
        if (!this.isDestroyer(metadata, method)) {
            return Optional.empty();
        } else if (method.getParameterCount() > 0) {
            throw new BeanDefinitionException(StringUtils.format(
                    "The method used to destroy cannot contain any parameter. [method={0}]",
                    ReflectionUtils.toString(method)));
        } else {
            return Optional.of(BeanDestroyers.method(method));
        }
    }
}
