/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Stereotype;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigChain;
import modelengine.fitframework.conf.support.DefaultConfigChain;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanDefinition;
import modelengine.fitframework.ioc.BeanDefinitionException;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreator;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreators;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyers;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializers;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjectors;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycle;
import modelengine.fitframework.ioc.lifecycle.bean.support.ConfigBeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.support.DefaultBeanLifecycle;
import modelengine.fitframework.pattern.builder.BuilderFactory;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为Bean的工厂提供扫描程序。
 *
 * @author 梁济时
 * @since 2022-05-12
 */
public abstract class BeanFactoryResolver {
    /** 表示默认 Bean 名字的前缀。 */
    public static final String DEFAULT_BEAN_NAME_PREFIX = "$Fit$";

    private final BeanContainer container;

    BeanFactoryResolver(BeanContainer container) {
        this.container = notNull(container, "The bean container cannot be null.");
    }

    /**
     * 获取所属的 Bean 容器。
     *
     * @return 表示所属的 Bean 容器的 {@link BeanContainer}。
     */
    protected final BeanContainer container() {
        return this.container;
    }

    private static String name(String name, Class<?> objectClass) {
        String actual = StringUtils.trim(name);
        if (StringUtils.isEmpty(name)) {
            actual = objectClass.getSimpleName();
            actual = DEFAULT_BEAN_NAME_PREFIX + Character.toLowerCase(actual.charAt(0)) + actual.substring(1);
        }
        return actual;
    }

    private static String name(String name, Method method) {
        String actual = StringUtils.trim(name);
        if (StringUtils.isEmpty(actual)) {
            actual = DEFAULT_BEAN_NAME_PREFIX + method.getName();
        }
        return actual;
    }

    private static class ByClass extends BeanFactoryResolver {
        private final Class<?> objectClass;

        ByClass(BeanContainer container, Class<?> objectClass) {
            super(container);
            this.objectClass = objectClass;
        }

        @Override
        List<BeanFactory> resolve() {
            if (this.objectClass.isInterface() || Modifier.isAbstract(this.objectClass.getModifiers())) {
                return Collections.emptyList();
            }
            Optional<BeanDefinition> definition =
                    this.container().runtime().resolverOfBeans().bean(this.container(), this.objectClass);
            if (!definition.isPresent()) {
                return Collections.emptyList();
            }
            BeanMetadata metadata = new DefaultBeanMetadata(this.container(),
                    name(definition.get().name(), this.objectClass),
                    definition.get().aliases(),
                    this.objectClass,
                    definition.get().stereotype(),
                    definition.get().preferred(),
                    definition.get().lazy(),
                    definition.get().dependencies(),
                    definition.get().applicable(),
                    this.container().runtime().resolverOfAnnotations().resolve(this.objectClass),
                    configOf(this.container(), definition.get()));
            return this.scan(metadata, this.objectClass, creator(this.container(), metadata, this.objectClass));
        }
    }

    private static class ByBean extends BeanFactoryResolver {
        private final Object bean;
        private final String name;
        private final Type type;

        ByBean(BeanContainer container, Object bean, String name, Type type) {
            super(container);
            this.bean = bean;
            this.name = name;
            this.type = type;
        }

        @Override
        List<BeanFactory> resolve() {
            BeanMetadata metadata = this.container()
                    .runtime()
                    .resolverOfBeans()
                    .bean(this.container(), this.bean.getClass())
                    .map(this::toMetadata)
                    .orElseGet(this::forDefault);
            return this.scan(metadata, this.bean.getClass(), BeanCreators.direct(this.bean));
        }

        private BeanMetadata toMetadata(BeanDefinition definition) {
            return new DefaultBeanMetadata(this.container(),
                    name(definition.name(), this.bean.getClass()),
                    definition.aliases(),
                    nullIf(this.type, this.bean.getClass()),
                    definition.stereotype(),
                    definition.preferred(),
                    definition.lazy(),
                    definition.dependencies(),
                    definition.applicable(),
                    this.container().runtime().resolverOfAnnotations().resolve(this.bean.getClass()),
                    configOf(this.container(), definition));
        }

        private BeanMetadata forDefault() {
            return new DefaultBeanMetadata(this.container(),
                    name(this.name, this.bean.getClass()),
                    null,
                    nullIf(this.type, this.bean.getClass()),
                    Stereotype.SINGLETON,
                    false,
                    false,
                    Collections.emptySet(),
                    BeanApplicableScope.INSENSITIVE,
                    this.container().runtime().resolverOfAnnotations().resolve(this.bean.getClass()),
                    configOf(this.container(), (Map<String, Object>) null));
        }
    }

    private static class ByDefinition extends BeanFactoryResolver {
        private final BeanDefinition definition;
        private final Class<?> beanClass;

        ByDefinition(BeanContainer container, BeanDefinition definition) {
            super(container);
            this.definition = definition;
            this.beanClass = (Class<?>) definition.type();
        }

        @Override
        List<BeanFactory> resolve() {
            BeanMetadata metadata = new DefaultBeanMetadata(this.container(),
                    this.definition.name(),
                    this.definition.aliases(),
                    this.definition.type(),
                    this.definition.stereotype(),
                    this.definition.preferred(),
                    this.definition.lazy(),
                    this.definition.dependencies(),
                    this.definition.applicable(),
                    this.container().runtime().resolverOfAnnotations().resolve(this.beanClass),
                    configOf(this.container(), this.definition));
            return this.scan(metadata, this.beanClass, BeanCreators.byClass(metadata, this.beanClass));
        }
    }

    static BeanFactoryResolver byClass(BeanContainer container, Class<?> objectClass) {
        return new ByClass(container, objectClass);
    }

    static BeanFactoryResolver byBean(BeanContainer container, Object bean, String name, Type type) {
        return new ByBean(container, bean, name, type);
    }

    static BeanFactoryResolver byDefinition(BeanContainer container, BeanDefinition definition) {
        return new ByDefinition(container, definition);
    }

    abstract List<BeanFactory> resolve();

    private List<BeanFactory> scan(BeanMetadata parent, Method method, BeanDefinition current) {
        if (!parent.singleton()) {
            throw new BeanDefinitionException(StringUtils.format(
                    "The bean that creates sub beans by method must be singleton. [name={0}, type={1}]",
                    parent.name(),
                    parent.type()));
        }
        BeanMetadata metadata = new DefaultBeanMetadata(this.container,
                name(current.name(), method),
                current.aliases(),
                method.getGenericReturnType(),
                current.stereotype(),
                current.preferred(),
                current.lazy(),
                current.dependencies(),
                current.applicable(),
                this.container.runtime().resolverOfAnnotations().resolve(method),
                configOf(this.container, current));
        BeanCreator creator = BeanCreators.byMethod(parent, method);
        return this.scan(metadata, classOf(metadata.type()), creator);
    }

    private static Config configOf(BeanContainer container, BeanDefinition definition) {
        return configOf(container, definition.properties());
    }

    private static Config configOf(BeanContainer container, Map<String, Object> properties) {
        Config config = container.plugin().config();
        if (properties != null) {
            ConfigChain chain = new DefaultConfigChain(null);
            chain.addConfig(Config.fromReadonlyMap(null, properties));
            chain.addConfig(config);
            config = chain;
        }
        return config;
    }

    /**
     * 使用 Bean 的元数据、类型和创建程序创建 Bean 工厂的列表。
     *
     * @param metadata 表示 Bean 元数据的 {@link BeanMetadata}。
     * @param beanClass 表示 Bean 的类型的 {@link Class}{@code <?>}。
     * @param creator 表示 Bean 的创建程序的 {@link BeanCreator}。
     * @return 表示创建的 Bean 工厂列表的 {@link List}{@code <}{@link BeanFactory}{@code >}。
     * @throws BeanDefinitionException Bean中的方法被定义为不止一个含义（依赖注入、初始化、销毁）。
     */
    protected List<BeanFactory> scan(BeanMetadata metadata, Class<?> beanClass, BeanCreator creator) {
        List<BeanFactory> factories = new ArrayList<>();
        List<BeanInjector> injectors = this.scanFields(metadata, collect(beanClass, Class::getDeclaredFields));
        List<BeanInitializer> initializers = new ArrayList<>();
        List<BeanDestroyer> destroyers = new ArrayList<>();
        Lifecycles lifecycles =
                Lifecycles.custom().injectors(injectors).initializers(initializers).destroyers(destroyers).build();
        this.scanMethods(metadata, collect(beanClass, Class::getDeclaredMethods), factories, lifecycles);
        Optional<BeanResolver.Factory> factoryDefinition =
                metadata.container().runtime().resolverOfBeans().factory(metadata);
        if (factoryDefinition.isPresent()) {
            if (!metadata.singleton()) {
                throw new BeanDefinitionException(StringUtils.format(
                        "A bean used as a factory must be singleton. [name={0}, type={1}]",
                        metadata.name(),
                        metadata.type()));
            }

            BeanMetadata supplierMetadata = getSupplierMetadata(metadata);
            BeanLifecycle supplierLifecycle = getBeanLifecycle(creator, supplierMetadata, lifecycles);
            factories.add(new SingletonBeanFactory(supplierLifecycle));

            BeanMetadata beanMetadata = getBeanMetadata(metadata, factoryDefinition.get());
            BeanLifecycle beanLifecycle = lifecycle(beanMetadata, supplierMetadata.name(), factoryDefinition.get());
            factories.add(new SingletonBeanFactory(beanLifecycle));
        } else {
            BeanLifecycle lifecycle = getBeanLifecycle(creator, metadata, lifecycles);
            BeanFactory factory;
            if (metadata.singleton()) {
                factory = new SingletonBeanFactory(lifecycle);
            } else {
                factory = new PrototypeBeanFactory(lifecycle);
            }
            factories.add(factory);
        }
        return factories;
    }

    private static BeanLifecycle getBeanLifecycle(BeanCreator creator, BeanMetadata metadata, Lifecycles lifecycles) {
        return lifecycle(metadata,
                creator,
                BeanInjectors.combine(lifecycles.injectors().toArray(new BeanInjector[0])),
                BeanInitializers.combine(lifecycles.initializers().toArray(new BeanInitializer[0])),
                BeanDestroyers.combine(lifecycles.destroyers().toArray(new BeanDestroyer[0])));
    }

    private void scanMethods(BeanMetadata metadata, List<Method> methods, List<BeanFactory> factories,
            Lifecycles lifecycles) {
        for (Method method : methods) {
            List<String> usages = new ArrayList<>();
            this.container.runtime()
                    .resolverOfBeans()
                    .bean(this.container, method)
                    .ifPresent(sub -> factories.addAll(this.scan(metadata, method, sub)));
            this.container.runtime().resolverOfBeans().injector(metadata, method).ifPresent(injector -> {
                usages.add("injector");
                lifecycles.injectors().add(injector);
            });
            this.container.runtime().resolverOfBeans().initializer(metadata, method).ifPresent(initializer -> {
                usages.add("initializer");
                lifecycles.initializers().add(initializer);
            });
            this.container.runtime().resolverOfBeans().destroyer(metadata, method).ifPresent(destroyer -> {
                usages.add("destroyer");
                lifecycles.destroyers().add(destroyer);
            });
            if (usages.size() > 1) {
                throw new BeanDefinitionException(StringUtils.format(
                        "A method cannot have multiple uses. [method={0}, uses={1}]",
                        ReflectionUtils.toString(method),
                        usages));
            }
        }
    }

    private List<BeanInjector> scanFields(BeanMetadata metadata, List<Field> fields) {
        List<BeanInjector> injectors = new LinkedList<>();
        getConfigBeanInjector(metadata).ifPresent(injectors::add);
        injectors.addAll(getFieldsInjectors(metadata, fields));
        return injectors;
    }

    private static Optional<ConfigBeanInjector> getConfigBeanInjector(BeanMetadata metadata) {
        return Optional.ofNullable(metadata.annotations().getAnnotation(AcceptConfigValues.class))
                .map(annotation -> new ConfigBeanInjector(metadata.config(), annotation.value()));
    }

    private List<BeanInjector> getFieldsInjectors(BeanMetadata metadata, List<Field> fields) {
        return fields.stream()
                .map(field -> this.container.runtime().resolverOfBeans().injector(metadata, field))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private static BeanMetadata getSupplierMetadata(BeanMetadata metadata) {
        return new DefaultBeanMetadata(metadata.container(),
                metadata.name() + "$$SUPPLIER",
                Collections.emptySet(),
                metadata.type(),
                Stereotype.SINGLETON,
                false,
                false,
                metadata.dependencies(),
                metadata.applicable(),
                AnnotationMetadata.empty(),
                metadata.config());
    }

    private static BeanMetadata getBeanMetadata(BeanMetadata metadata, BeanResolver.Factory factoryDefinition) {
        return new DefaultBeanMetadata(metadata.container(),
                metadata.name(),
                metadata.aliases(),
                factoryDefinition.type(),
                Stereotype.SINGLETON,
                metadata.preferred(),
                metadata.lazy(),
                metadata.dependencies(),
                metadata.applicable(),
                metadata.annotations(),
                metadata.config());
    }

    private static BeanLifecycle lifecycle(BeanMetadata metadata, BeanCreator creator, BeanInjector injector,
            BeanInitializer initializer, BeanDestroyer destroyer) {
        return new DefaultBeanLifecycle(metadata, creator, null, injector, initializer, destroyer);
    }

    private static BeanLifecycle lifecycle(BeanMetadata metadata, String supplierName, BeanResolver.Factory factory) {
        BeanCreator creator = args -> {
            Object supplier = metadata.container().beans().get(supplierName);
            return factory.create(supplier);
        };
        return lifecycle(metadata, creator, null, null, null);
    }

    /**
     * 发掘Bean的类型。
     *
     * @param beanType 表示Bean类型的 {@link Type}。
     * @return 表示Bean的实际类型的 {@link Class}。
     * @throws IllegalArgumentException {@code beanType} 不是 {@link Class} 或 {@link ParameterizedType}。
     */
    private static Class<?> classOf(Type beanType) {
        if (beanType instanceof Class) {
            return (Class<?>) beanType;
        } else if (beanType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) beanType).getRawType();
        } else {
            throw new IllegalArgumentException(StringUtils.format(
                    "The bean type must be a class or parameterized type. [type={0}]",
                    beanType.getTypeName()));
        }
    }

    /**
     * 获取指定类型的 Bean 的创建程序。
     *
     * @param container 表示 Bean 的容器的 {@link BeanContainer}。
     * @param metadata 表示 Bean 的元数据的 {@link BeanMetadata}。
     * @param beanClass 表示 Bean 类型的 {@link Class}{@code <?>}。
     * @return 表示Bean的创建程序的 {@link BeanCreator}。
     * @throws BeanDefinitionException {@code beanClass} 中定义了多个首选构造方法。
     */
    private static BeanCreator creator(BeanContainer container, BeanMetadata metadata, Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        List<Constructor<?>> preferred = Stream.of(constructors)
                .filter(constructor -> container.runtime().resolverOfBeans().preferred(metadata, constructor))
                .collect(Collectors.toList());
        BeanCreator creator;
        if (preferred.isEmpty()) {
            if (constructors.length > 1) {
                return BeanCreators.byClass(metadata, beanClass);
            } else {
                return BeanCreators.byConstructor(metadata, constructors[0]);
            }
        } else if (preferred.size() > 1) {
            throw new BeanDefinitionException(StringUtils.format(
                    "A bean class cannot define multiple preferred constructors. [class={0}, constructors={1}]",
                    beanClass.getName(),
                    preferred));
        } else {
            creator = BeanCreators.byConstructor(metadata, preferred.get(0));
        }
        return creator;
    }

    /**
     * 使用从类型中获取元素的方法，收集指定类型及其所有父类、所实现接口中的元素。
     *
     * @param beanClass 表示待收集元素的类型的 {@link Class}。
     * @param mapper 表示用以从元素中获取元素的方法的 {@link Function}。
     * @param <T> 表示元素的具体类型。
     * @return 表示收集到的所有元素的列表的 {@link List}。
     */
    private static <T> List<T> collect(Class<?> beanClass, Function<Class<?>, T[]> mapper) {
        List<T> elements = new ArrayList<>();
        Stack<Class<?>> stacks = new Stack<>();
        stacks.add(beanClass);
        while (!stacks.isEmpty()) {
            Class<?> clazz = stacks.pop();
            elements.addAll(Arrays.asList(mapper.apply(clazz)));
            stacks.addAll(Arrays.asList(clazz.getInterfaces()));
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                stacks.add(superclass);
            }
        }
        return elements;
    }

    /**
     * 表示 Bean 生命周期的方法集合。
     */
    public interface Lifecycles {
        /**
         * 获取 Bean 注入器的列表。
         *
         * @return 表示 Bean 注入器列表的 {@link List}{@code <}{@link BeanInjector}{@code >}。
         */
        List<BeanInjector> injectors();

        /**
         * 获取 Bean 初始化器的列表。
         *
         * @return 表示 Bean 初始化器列表的 {@link List}{@code <}{@link BeanInitializer}{@code >}。
         */
        List<BeanInitializer> initializers();

        /**
         * 获取 Bean 销毁器的列表。
         *
         * @return 表示 Bean 销毁器列表的 {@link List}{@code <}{@link BeanDestroyer}{@code >}。
         */
        List<BeanDestroyer> destroyers();

        /**
         * 表示 {@link Lifecycles} 的构建器。
         */
        interface Builder {
            /**
             * 向当前构建器中设置 Bean 注入器的列表。
             *
             * @param injectors 表示待设置的 Bean 注入器列表的 {@link List}{@code <}{@link BeanInjector}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder injectors(List<BeanInjector> injectors);

            /**
             * 向当前构建器中设置 Bean 初始化器的列表。
             *
             * @param initializers 表示待设置的 Bean 初始化器列表的 {@link List}{@code <}{@link BeanInitializer}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder initializers(List<BeanInitializer> initializers);

            /**
             * 向当前构建器中设置 Bean 销毁器的列表。
             *
             * @param destroyers 表示待设置的 Bean 销毁器列表的 {@link List}{@code <}{@link BeanDestroyer}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder destroyers(List<BeanDestroyer> destroyers);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link Lifecycles}。
             */
            Lifecycles build();
        }

        /**
         * 获取 {@link Lifecycles} 的构建器。
         *
         * @return 表示 {@link Lifecycles} 的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return custom(null);
        }

        /**
         * 获取 {@link Lifecycles} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link Lifecycles}。
         * @return 表示 {@link Lifecycles} 的构建器的 {@link Builder}。
         */
        static Builder custom(Lifecycles value) {
            return BuilderFactory.get(Lifecycles.class, Builder.class).create(value);
        }
    }
}
