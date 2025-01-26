/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import modelengine.fitframework.annotation.Stereotype;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanDefinition;
import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.ValueSupplier;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 为 {@link BeanResolver} 提供组合。
 *
 * @author 梁济时
 * @since 2022-05-09
 */
public class BeanResolverComposite implements BeanResolver {
    private final List<BeanResolver> resolvers;

    /**
     * 使用待组合的Bean解析程序的集合初始化 {@link BeanResolverComposite} 类的新实例。
     *
     * @throws IllegalArgumentException {@code resolver1} 或 {@code resolver2} 为 {@code null}。
     */
    public BeanResolverComposite() {
        this.resolvers = new LinkedList<>();
    }

    /**
     * 将解析程序集合全部添加到组合中。
     *
     * @param resolvers 表示解析程序集合的 {@link Iterable}{@code <}{@link BeanResolver}{@code >}。
     */
    public void addAll(Iterable<BeanResolver> resolvers) {
        if (resolvers != null) {
            resolvers.forEach(this::add);
        }
    }

    /**
     * 将解析程序添加到组合中。
     *
     * @param resolver 表示解析程序的 {@link BeanResolver}。
     */
    public void add(BeanResolver resolver) {
        if (resolver == null) {
            return;
        }
        if (resolver instanceof BeanResolverComposite) {
            this.resolvers.addAll(((BeanResolverComposite) resolver).resolvers);
        } else {
            this.resolvers.add(resolver);
        }
    }

    /**
     * 从组合中移除所有指定的解析程序集合。
     *
     * @param resolvers 表示解析程序的 {@link Iterable}{@code <}{@link BeanResolver}{@code >}。
     */
    public void removeAll(Iterable<BeanResolver> resolvers) {
        if (resolvers != null) {
            resolvers.forEach(this::remove);
        }
    }

    /**
     * 从组合中移除指定的解析程序。
     *
     * @param resolver 表示解析程序的 {@link BeanResolver}。
     */
    public void remove(BeanResolver resolver) {
        if (resolver == null) {
            return;
        }
        if (resolver instanceof BeanResolverComposite) {
            this.resolvers.removeAll(((BeanResolverComposite) resolver).resolvers);
        } else {
            this.resolvers.remove(resolver);
        }
    }

    /**
     * 获取组合的解析程序的数量。
     *
     * @return 表示数量的 {@code int}。
     */
    public int size() {
        return this.resolvers.size();
    }

    /**
     * 检查组合的解析程序是否为空。
     *
     * @return 表示解析程序是否为空的结果的 {@code boolean}。
     */
    public boolean empty() {
        return this.size() < 1;
    }

    /**
     * 获取指定索引处的解析程序。
     *
     * @param index 表示索引的 {@code int}。
     * @return 表示解析程序的 {@link BeanResolver}。
     */
    public BeanResolver get(int index) {
        return this.resolvers.get(index);
    }

    private <T> Optional<T> resolve(Function<BeanResolver, Optional<T>> mapper) {
        for (BeanResolver resolver : this.resolvers) {
            Optional<T> result = mapper.apply(resolver);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    private Optional<BeanDefinition> bean(Function<BeanResolver, Optional<BeanDefinition>> mapper) {
        List<BeanDefinition> definitions = this.resolvers.stream()
                .map(mapper)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if (definitions.isEmpty()) {
            return Optional.empty();
        }
        List<String> names = definitions.stream().map(BeanDefinition::name).distinct().collect(Collectors.toList());
        if (names.size() > 1) {
            throw new BeanDefinitionException(StringUtils.format("Bean has more than one name defined. [names={0}]",
                    names.stream().collect(Collectors.joining(", ", "[", "]"))));
        }
        List<Type> types = definitions.stream().map(BeanDefinition::type).distinct().collect(Collectors.toList());
        if (types.size() > 1) {
            throw new BeanDefinitionException(StringUtils.format("Bean has more than one type defined. [types={0}]",
                    types.stream().map(Type::getTypeName).collect(Collectors.joining(", ", "[", "]"))));
        }
        return Optional.of(BeanDefinition.custom()
                .name(CollectionUtils.firstOrDefault(names))
                .type(CollectionUtils.firstOrDefault(types))
                .aliases(definitions.stream()
                        .map(BeanDefinition::aliases)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .filter(Objects::nonNull)
                        .map(StringUtils::trim)
                        .collect(Collectors.toSet()))
                .stereotype(stereotype(definitions))
                .preferred(definitions.stream().anyMatch(BeanDefinition::preferred))
                .lazy(definitions.stream().anyMatch(BeanDefinition::lazy))
                .dependencies(definitions.stream()
                        .map(BeanDefinition::dependencies)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()))
                .applicable(applicable(definitions))
                .build());
    }

    private static BeanApplicableScope applicable(List<BeanDefinition> definitions) {
        OptionalInt optional =
                definitions.stream().map(BeanDefinition::applicable).mapToInt(BeanApplicableScope::value).max();
        if (optional.isPresent()) {
            int value = optional.getAsInt();
            EnumSet<BeanApplicableScope> scopes = EnumSet.allOf(BeanApplicableScope.class);
            for (BeanApplicableScope scope : scopes) {
                if (scope.value() == value) {
                    return scope;
                }
            }
        }
        return BeanApplicableScope.INSENSITIVE;
    }

    private static String stereotype(List<BeanDefinition> definitions) {
        for (BeanDefinition definition : definitions) {
            String actual = StringUtils.trim(definition.stereotype());
            if (StringUtils.isEmpty(actual) || !StringUtils.equalsIgnoreCase(actual, Stereotype.SINGLETON)) {
                return actual;
            }
        }
        return Stereotype.SINGLETON;
    }

    @Override
    public Optional<BeanDefinition> bean(BeanContainer container, Class<?> clazz) {
        return this.bean(resolver -> resolver.bean(container, clazz));
    }

    @Override
    public Optional<BeanDefinition> bean(BeanContainer container, Method method) {
        return this.bean(resolver -> resolver.bean(container, method));
    }

    @Override
    public boolean preferred(BeanMetadata metadata, Constructor<?> constructor) {
        for (BeanResolver resolver : this.resolvers) {
            if (resolver.preferred(metadata, constructor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<ValueSupplier> parameter(BeanMetadata metadata, Parameter parameter) {
        return this.resolve(resolver -> resolver.parameter(metadata, parameter));
    }

    @Override
    public Optional<BeanInjector> injector(BeanMetadata metadata, Field field) {
        return this.resolve(resolver -> resolver.injector(metadata, field));
    }

    @Override
    public Optional<BeanInjector> injector(BeanMetadata metadata, Method method) {
        return this.resolve(resolver -> resolver.injector(metadata, method));
    }

    @Override
    public Optional<BeanInitializer> initializer(BeanMetadata metadata, Method method) {
        return this.resolve(resolver -> resolver.initializer(metadata, method));
    }

    @Override
    public Optional<BeanDestroyer> destroyer(BeanMetadata metadata, Method method) {
        return this.resolve(resolver -> resolver.destroyer(metadata, method));
    }

    @Override
    public Optional<Integer> priority(BeanMetadata metadata) {
        return this.resolve(resolver -> resolver.priority(metadata));
    }

    @Override
    public Set<String> packages(BeanMetadata metadata) {
        return this.resolvers.stream()
                .map(resolver -> resolver.packages(metadata))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> configurations(BeanMetadata metadata) {
        return this.resolvers.stream()
                .map(resolver -> resolver.configurations(metadata))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Factory> factory(BeanMetadata metadata) {
        return this.resolve(resolver -> resolver.factory(metadata));
    }
}
