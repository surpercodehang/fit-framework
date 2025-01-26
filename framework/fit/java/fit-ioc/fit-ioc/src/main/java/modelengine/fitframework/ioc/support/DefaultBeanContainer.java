/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.AmbiguousBeanException;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanDefinition;
import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanFactoryOrderComparator;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanNotFoundException;
import modelengine.fitframework.ioc.BeanRegisteredObserver;
import modelengine.fitframework.ioc.BeanRegistry;
import modelengine.fitframework.ioc.lifecycle.container.BeanContainerInitializedObserver;
import modelengine.fitframework.ioc.lifecycle.container.BeanContainerStartedObserver;
import modelengine.fitframework.ioc.lifecycle.container.BeanContainerStoppedObserver;
import modelengine.fitframework.ioc.lifecycle.container.BeanContainerTerminatingObserver;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginKey;
import modelengine.fitframework.type.TypeMatcher;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.Disposable;
import modelengine.fitframework.util.LockUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.support.AbstractDisposable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 为 {@link BeanContainer} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public final class DefaultBeanContainer extends AbstractDisposable implements BeanContainer, BeanRegistry {
    private final Plugin plugin;

    private final Beans beans;

    private final List<BeanFactory> factories;
    private final ReadWriteLock monitor;

    private final List<BeanRegisteredObserver> beanRegisteredObservers;

    public DefaultBeanContainer(Plugin plugin) {
        this.plugin = notNull(plugin, "The owning plugin of a bean container cannot be null.");

        this.beans = this.new Beans();

        this.factories = new LinkedList<>();
        this.monitor = LockUtils.newReentrantReadWriteLock();

        this.beanRegisteredObservers = new LinkedList<>();
    }

    private BeanMetadata register(BeanFactory factory) {
        notNull(factory, "The factory of bean to register cannot be null.");
        LockUtils.synchronize(this.monitor.writeLock(), () -> {
            Set<String> existing = this.factories.stream()
                    .map(current -> CollectionUtils.intersect(names(current), names(factory)))
                    .filter(CollectionUtils::isNotEmpty)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            if (existing.isEmpty()) {
                this.factories.add(factory);
            } else {
                throw new BeanDefinitionException(StringUtils.format(
                        "Beans with the same name already exists. [names={0}]",
                        existing));
            }
        });
        BeanMetadata metadata = factory.metadata();
        this.beanRegisteredObservers.forEach(observer -> observer.onBeanRegistered(metadata));
        return metadata;
    }

    private static Set<String> names(BeanFactory factory) {
        Set<String> names = new HashSet<>();
        names.add(factory.metadata().name());
        names.addAll(factory.metadata().aliases());
        return names;
    }

    @Override
    public List<BeanMetadata> register(Class<?> beanClass) {
        List<BeanMetadata> metadata = new LinkedList<>();
        List<BeanFactory> resolvedFactories = BeanFactoryResolver.byClass(this, beanClass).resolve();
        resolvedFactories.forEach(factory -> metadata.add(this.register(factory)));
        return metadata;
    }

    @Override
    public List<BeanMetadata> register(Object bean) {
        return this.register(bean, null, null);
    }

    @Override
    public List<BeanMetadata> register(Object bean, String name) {
        return this.register(bean, name, null);
    }

    @Override
    public List<BeanMetadata> register(Object bean, Type type) {
        return this.register(bean, null, type);
    }

    private List<BeanMetadata> register(Object bean, String name, Type type) {
        List<BeanMetadata> metadata = new LinkedList<>();
        List<BeanFactory> resolvedFactories = BeanFactoryResolver.byBean(this, bean, name, type).resolve();
        resolvedFactories.forEach(factory -> metadata.add(this.register(factory)));
        return metadata;
    }

    @Override
    public List<BeanMetadata> register(BeanDefinition definition) {
        notNull(definition, "The definition of bean to register cannot be null.");
        Type type = definition.type();
        notNull(type, "Type of bean to register cannot be null.");
        if (!(type instanceof Class)) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The type of bean to register must be a class. [type={0}]",
                    type.getTypeName()));
        }
        List<BeanFactory> resolvedFactories = BeanFactoryResolver.byDefinition(this, definition).resolve();
        List<BeanMetadata> metadata = new ArrayList<>(resolvedFactories.size());
        resolvedFactories.forEach(factory -> metadata.add(this.register(factory)));
        return metadata;
    }

    @Override
    public void subscribe(BeanRegisteredObserver observer) {
        if (observer != null) {
            this.beanRegisteredObservers.add(observer);
        }
    }

    @Override
    public void unsubscribe(BeanRegisteredObserver observer) {
        if (observer != null) {
            this.beanRegisteredObservers.remove(observer);
        }
    }

    @Nonnull
    @Override
    public String name() {
        return PluginKey.identify(this.plugin.metadata());
    }

    @Nonnull
    @Override
    public Plugin plugin() {
        return this.plugin;
    }

    @Nonnull
    @Override
    public BeanRegistry registry() {
        return this;
    }

    @Override
    public BeanContainer.Beans beans() {
        return this.beans;
    }

    @Override
    public void destroySingleton(String beanName) {
        Optional<BeanFactory> factory = this.factory(beanName);
        if (factory.isPresent() && factory.get().metadata().singleton()) {
            factory.get().dispose();
        }
    }

    @Override
    public void removeBean(String beanName) {
        synchronized (this.monitor) {
            this.factories.removeIf(beanFactory -> names(beanFactory).contains(beanName));
        }
    }

    @Override
    public Optional<BeanFactory> factory(String name) {
        return LockUtils.synchronize(this.monitor.readLock(),
                () -> this.factories.stream().filter(factory -> names(factory).contains(name)).findAny());
    }

    @Override
    public Optional<BeanFactory> factory(Type type) {
        return LockUtils.synchronize(this.monitor.readLock(), () -> {
            List<BeanFactory> filteredFactories = this.factories(type);
            if (filteredFactories.isEmpty()) {
                return Optional.empty();
            } else if (filteredFactories.size() > 1) {
                List<BeanFactory> preferred = filteredFactories.stream()
                        .filter(factory -> factory.metadata().preferred())
                        .collect(Collectors.toList());
                if (preferred.isEmpty()) {
                    throw new AmbiguousBeanException(StringUtils.format(
                            "Ambiguous bean of specific type found. [type={0}, beans={1}]",
                            type.getTypeName(),
                            filteredFactories.stream()
                                    .map(factory -> factory.metadata().type())
                                    .map(Type::getTypeName)
                                    .collect(Collectors.joining(", ", "[", "]"))));
                } else if (preferred.size() > 1) {
                    throw new AmbiguousBeanException(StringUtils.format(
                            "Ambiguous preferred bean of specific type found. [type={0}, beans={1}]",
                            type.getTypeName(),
                            preferred.stream()
                                    .map(factory -> factory.metadata().type())
                                    .map(Type::getTypeName)
                                    .collect(Collectors.joining(", ", "[", "]"))));
                } else {
                    return Optional.of(preferred.get(0));
                }
            } else {
                return Optional.of(filteredFactories.get(0));
            }
        });
    }

    @Override
    public List<BeanFactory> factories(Type type) {
        return LockUtils.synchronize(this.monitor.readLock(),
                () -> this.factories.stream()
                        .filter(factory -> TypeMatcher.match(factory.metadata().type(), type))
                        .collect(Collectors.toList()));
    }

    @Override
    public List<BeanFactory> factories() {
        return LockUtils.synchronize(this.monitor.readLock(), () -> new ArrayList<>(this.factories));
    }

    @Override
    public Optional<BeanFactory> lookup(String name) {
        return this.lookup(container -> container.factory(name));
    }

    @Override
    public Optional<BeanFactory> lookup(Type type) {
        return this.lookup(container -> container.factory(type));
    }

    private Optional<BeanFactory> lookup(Function<BeanContainer, Optional<BeanFactory>> mapper) {
        return LockUtils.synchronize(this.monitor.readLock(), () -> {
            Iterator<BeanContainer> iterator = new LocalPreferredBeanContainerIterator(this);
            while (iterator.hasNext()) {
                BeanContainer container = iterator.next();
                Optional<BeanFactory> factory = mapper.apply(container);
                if (factory.isPresent() && this.applicable(container, factory.get().metadata().applicable())) {
                    return factory;
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public List<BeanFactory> all(Type type) {
        return LockUtils.synchronize(this.monitor.readLock(),
                () -> this.all()
                        .stream()
                        .filter(factory -> TypeMatcher.match(factory.metadata().type(), type))
                        .collect(Collectors.toList()));
    }

    @Override
    public List<BeanFactory> all() {
        return LockUtils.synchronize(this.monitor.readLock(), () -> {
            List<BeanFactory> all = new ArrayList<>();
            Iterator<BeanContainer> iterator = new LocalPreferredBeanContainerIterator(this);
            while (iterator.hasNext()) {
                BeanContainer current = iterator.next();
                List<BeanFactory> filteredFactories = current.factories()
                        .stream()
                        .filter(factory -> this.applicable(current, factory.metadata().applicable()))
                        .collect(Collectors.toList());
                all.addAll(filteredFactories);
            }
            return all.stream().sorted(BeanFactoryOrderComparator.INSTANCE).collect(Collectors.toList());
        });
    }

    @Override
    public void start() {
        BeanContainerInitializedObserver.notify(this);
        this.start0();
        BeanContainerStartedObserver.notify(this);
    }

    private void start0() {
        for (BeanFactory factory : this.factories) {
            if (factory.metadata().singleton() && !factory.metadata().lazy()) {
                factory.get();
            }
        }
    }

    @Override
    public void stop() {
        BeanContainerTerminatingObserver.notify(this);
        this.dispose();
        BeanContainerStoppedObserver.notify(this);
    }

    @Override
    public String toString() {
        return this.name();
    }

    private final class Beans implements BeanContainer.Beans {
        @Override
        public <T> T get(Class<T> beanClass, Object... initialArguments) {
            return cast(DefaultBeanContainer.this.factory(beanClass)
                    .map(factory -> factory.get(initialArguments))
                    .orElseThrow(() -> new BeanNotFoundException(StringUtils.format(
                            "Required bean not found. [container={0}, bean={1}]",
                            DefaultBeanContainer.this.name(),
                            beanClass.getTypeName()))));
        }

        @Override
        public <T> T get(Type beanType, Object... initialArguments) {
            return cast(DefaultBeanContainer.this.factory(beanType)
                    .map(factory -> factory.get(initialArguments))
                    .orElseThrow(() -> new BeanNotFoundException(StringUtils.format(
                            "Required bean not found. [container={0}, bean={0}]",
                            DefaultBeanContainer.this.name(),
                            beanType.getTypeName()))));
        }

        @Override
        public <T> T get(String beanName, Object... initialArguments) {
            return cast(DefaultBeanContainer.this.factory(beanName)
                    .map(factory -> factory.get(initialArguments))
                    .orElseThrow(() -> new BeanNotFoundException(StringUtils.format(
                            "Required bean not found. [container={0}, bean={0}]",
                            DefaultBeanContainer.this.name(),
                            beanName))));
        }

        @Override
        public <T> T lookup(Class<T> beanClass, Object... initialArguments) {
            return cast(DefaultBeanContainer.this.lookup(beanClass)
                    .map(factory -> factory.get(initialArguments))
                    .orElseThrow(() -> new BeanNotFoundException(StringUtils.format(
                            "Required bean not found. [container={0}, bean={1}]",
                            DefaultBeanContainer.this.name(),
                            beanClass.getTypeName()))));
        }

        @Override
        public <T> T lookup(Type beanType, Object... initialArguments) {
            return cast(DefaultBeanContainer.this.lookup(beanType)
                    .map(factory -> factory.get(initialArguments))
                    .orElseThrow(() -> new BeanNotFoundException(StringUtils.format(
                            "Required bean not found. [container={0}, bean={0}]",
                            DefaultBeanContainer.this.name(),
                            beanType.getTypeName()))));
        }

        @Override
        public <T> T lookup(String beanName, Object... initialArguments) {
            return cast(DefaultBeanContainer.this.lookup(beanName)
                    .map(factory -> factory.get(initialArguments))
                    .orElseThrow(() -> new BeanNotFoundException(StringUtils.format(
                            "Required bean not found. [container={0}, bean={0}]",
                            DefaultBeanContainer.this.name(),
                            beanName))));
        }

        @Override
        public <T> Map<String, T> list(Class<T> beanClass) {
            return DefaultBeanContainer.this.factories(beanClass)
                    .stream()
                    .collect(Collectors.toMap(factory -> factory.metadata().name(), BeanFactory::<T>get));
        }

        @Override
        public <T> Map<String, T> list(Type beanType) {
            return DefaultBeanContainer.this.factories(beanType)
                    .stream()
                    .collect(Collectors.toMap(factory -> factory.metadata().name(), BeanFactory::<T>get));
        }

        @Override
        public <T> Map<String, T> all(Class<T> beanClass) {
            return DefaultBeanContainer.this.all(beanClass)
                    .stream()
                    .collect(Collectors.toMap(factory -> factory.metadata().name(), BeanFactory::<T>get));
        }

        @Override
        public <T> Map<String, T> all(Type beanType) {
            return DefaultBeanContainer.this.all(beanType)
                    .stream()
                    .collect(Collectors.toMap(factory -> factory.metadata().name(), BeanFactory::<T>get));
        }
    }

    @Override
    protected void dispose0() {
        LockUtils.synchronize(this.monitor.writeLock(), () -> {
            this.factories.forEach(Disposable::safeDispose);
            this.factories.clear();
        });
    }

    private boolean applicable(BeanContainer container, BeanApplicableScope scope) {
        BeanApplicableScope actual = nullIf(scope, BeanApplicableScope.ANYWHERE);
        switch (actual) {
            case INSENSITIVE:
            case ANYWHERE:
                return true;
            case CURRENT:
                return container == this;
            case CHILDREN:
                BeanContainer current = container;
                while (current != null) {
                    if (current == this) {
                        return true;
                    } else {
                        current = Optional.of(current)
                                .map(BeanContainer::plugin)
                                .map(Plugin::parent)
                                .map(Plugin::container)
                                .orElse(null);
                    }
                }
                return false;
            default:
                throw new IllegalStateException("Unknown applicable scope: " + scope);
        }
    }
}
