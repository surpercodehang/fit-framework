/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static modelengine.fitframework.util.ObjectUtils.mapIfNotNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.ApplicableScope;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Conditional;
import modelengine.fitframework.annotation.Default;
import modelengine.fitframework.annotation.DependsOn;
import modelengine.fitframework.annotation.Destroy;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.ImportConfigs;
import modelengine.fitframework.annotation.Initialize;
import modelengine.fitframework.annotation.Lazy;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.ScanPackages;
import modelengine.fitframework.annotation.Stereotype;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanDefinition;
import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.BeanResolvers;
import modelengine.fitframework.ioc.BeanSupplier;
import modelengine.fitframework.ioc.Condition;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyers;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializers;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjectors;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;
import modelengine.fitframework.type.ParameterizedTypeResolver;
import modelengine.fitframework.type.ParameterizedTypeResolvingResult;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link BeanResolver} 提供基于FIT的默认实现。
 *
 * @author 梁济时
 * @since 2022-05-17
 */
public class DefaultBeanResolver implements BeanResolver {
    @Override
    public Optional<BeanDefinition> bean(BeanContainer container, Class<?> clazz) {
        return definition(container, clazz, clazz);
    }

    @Override
    public Optional<BeanDefinition> bean(BeanContainer container, Method method) {
        return definition(container, method, method.getGenericReturnType());
    }

    private static Optional<BeanDefinition> definition(BeanContainer container, AnnotatedElement element, Type type) {
        AnnotationMetadata annotations = container.runtime().resolverOfAnnotations().resolve(element);
        Component component = annotations.getAnnotation(Component.class);
        if (component == null) {
            return Optional.empty();
        }
        Conditional conditional = annotations.getAnnotation(Conditional.class);
        if (conditional != null) {
            Class<? extends Condition>[] conditionClasses = conditional.value();
            for (Class<? extends Condition> conditionClass : conditionClasses) {
                Condition condition;
                try {
                    condition = conditionClass.newInstance();
                } catch (IllegalAccessException e) {
                    throw new BeanDefinitionException(StringUtils.format(
                            "Failed to access class of condition. [class={0}]",
                            conditionClass.getName()), e);
                } catch (InstantiationException e) {
                    throw new BeanDefinitionException(StringUtils.format(
                            "Failed to instantiate class of condition. [class={0}]",
                            conditionClass.getName()), e);
                }
                if (!condition.match(container, annotations)) {
                    return Optional.empty();
                }
            }
        }

        String stereotype = mapIfNotNull(annotations.getAnnotation(Stereotype.class), Stereotype::value);
        ApplicableScope applicable = annotations.getAnnotation(ApplicableScope.class);
        return Optional.of(BeanDefinition.custom()
                .name(component.name())
                .type(type)
                .aliases(aliases(annotations))
                .stereotype(stereotype)
                .preferred(annotations.isAnnotationPresent(Default.class))
                .lazy(annotations.isAnnotationPresent(Lazy.class))
                .dependencies(dependencies(annotations))
                .applicable(nullIf(mapIfNotNull(applicable, ApplicableScope::value), BeanApplicableScope.INSENSITIVE))
                .build());
    }

    private static Set<String> aliases(AnnotationMetadata annotations) {
        return Stream.of(annotations.getAnnotationsByType(Alias.class))
                .map(Alias::value)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
    }

    private static Set<String> dependencies(AnnotationMetadata annotations) {
        DependsOn annotation = annotations.getAnnotation(DependsOn.class);
        if (annotation == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(annotation.value()));
    }

    @Override
    public boolean preferred(BeanMetadata metadata, Constructor<?> constructor) {
        return constructor.isAnnotationPresent(Fit.class);
    }

    private static DependencyRequirement requirement(BeanMetadata source, AnnotationMetadata annotations) {
        Fit fit = annotations.getAnnotation(Fit.class);
        if (fit != null) {
            String alias = fit.alias();
            if (StringUtils.isBlank(alias)) {
                alias = mapIfNotNull(annotations.getAnnotation(Alias.class), Alias::value);
            }
            alias = StringUtils.trim(alias);
            return new BeanDependencyRequirement(source, alias);
        }
        Value value = annotations.getAnnotation(Value.class);
        if (value != null) {
            String expression = value.value();
            return new ConfigDependencyRequirement(source, expression);
        }
        return null;
    }

    @Override
    public Optional<ValueSupplier> parameter(BeanMetadata metadata, Parameter parameter) {
        AnnotationMetadata annotations = metadata.runtime().resolverOfAnnotations().resolve(parameter);
        return Optional.ofNullable(requirement(metadata, annotations))
                .map(requirement -> requirement.withType(parameter.getParameterizedType(), annotations));
    }

    @Override
    public Optional<BeanInjector> injector(BeanMetadata metadata, Field field) {
        AnnotationMetadata annotations = metadata.runtime().resolverOfAnnotations().resolve(field);
        return Optional.ofNullable(requirement(metadata, annotations))
                .map(requirement -> requirement.withType(field.getGenericType(), annotations))
                .map(supplier -> BeanInjectors.field(field, supplier));
    }

    @Override
    public Optional<BeanInjector> injector(BeanMetadata metadata, Method method) {
        AnnotationMetadata annotations = metadata.runtime().resolverOfAnnotations().resolve(method);
        DependencyRequirement requirement = requirement(metadata, annotations);
        if (requirement == null) {
            return Optional.empty();
        }
        Parameter[] parameters = method.getParameters();
        if (parameters.length != 1) {
            throw new BeanDefinitionException(StringUtils.format(
                    "The method used to inject must contain and only contain 1 parameter. [method={0}]",
                    ReflectionUtils.toString(method)));
        }
        ValueSupplier supplier = requirement.withType(parameters[0].getParameterizedType(), annotations);
        BeanInjector injector = BeanInjectors.method(method, supplier);
        return Optional.of(injector);
    }

    @Override
    public Optional<BeanInitializer> initializer(BeanMetadata metadata, Method method) {
        AnnotationMetadata annotations = metadata.runtime().resolverOfAnnotations().resolve(method);
        if (!annotations.isAnnotationPresent(Initialize.class)) {
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
        AnnotationMetadata annotations = metadata.runtime().resolverOfAnnotations().resolve(method);
        if (!annotations.isAnnotationPresent(Destroy.class)) {
            return Optional.empty();
        } else if (method.getParameterCount() > 0) {
            throw new BeanDefinitionException(StringUtils.format(
                    "The method used to destroy cannot contain any parameter. [method={0}]",
                    ReflectionUtils.toString(method)));
        } else {
            return Optional.of(BeanDestroyers.method(method));
        }
    }

    @Override
    public Optional<Integer> priority(BeanMetadata metadata) {
        return Optional.ofNullable(metadata.annotations().getAnnotation(Order.class)).map(Order::value);
    }

    @Override
    public Set<String> packages(BeanMetadata metadata) {
        return Optional.ofNullable(metadata.annotations().getAnnotation(ScanPackages.class))
                .map(ScanPackages::value)
                .map(Arrays::asList)
                .<Set<String>>map(HashSet::new)
                .orElse(Collections.emptySet());
    }

    @Override
    public Set<String> configurations(BeanMetadata metadata) {
        return Optional.ofNullable(metadata.annotations().getAnnotation(ImportConfigs.class))
                .map(ImportConfigs::value)
                .map(Arrays::asList)
                .<Set<String>>map(HashSet::new)
                .orElse(Collections.emptySet());
    }

    @Override
    public Optional<BeanResolver.Factory> factory(BeanMetadata metadata) {
        ParameterizedTypeResolvingResult result =
                ParameterizedTypeResolver.resolve(metadata.type(), BeanSupplier.class);
        if (result.resolved()) {
            return Optional.of(BeanResolvers.factory(result.parameters().get(0),
                    bean -> ((BeanSupplier<?>) bean).get()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * 为 {@link BeanResolver.Factory} 提供默认实现。
     *
     * @author 梁济时
     * @since 2022-07-06
     */
    public static class Factory implements BeanResolver.Factory {
        private final Type type;
        private final Function<Object, Object> mapper;

        /**
         * 使用工厂所创建Bean的类型和创建方法初始化 {@link Factory} 类的新实例。
         *
         * @param type 表示Bean的实际类型的 {@link Type}。
         * @param mapper 表示通过原始Bean创建目标Bean的方法的 {@link Function}。
         * @throws IllegalArgumentException {@code type} 或 {@code mapper} 为 {@code null}。
         */
        public Factory(Type type, Function<Object, Object> mapper) {
            this.type = Validation.notNull(type, "The type of a factory cannot be null.");
            this.mapper = Validation.notNull(mapper, "The mapper of a factory cannot be null.");
        }

        @Override
        public Type type() {
            return this.type;
        }

        @Override
        public Object create(Object bean) {
            return this.mapper.apply(bean);
        }
    }
}
