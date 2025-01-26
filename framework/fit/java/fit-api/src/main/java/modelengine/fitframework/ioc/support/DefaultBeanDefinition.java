/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.Stereotype;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanDefinition;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 为 {@link BeanDefinition} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-05-16
 */
public class DefaultBeanDefinition implements BeanDefinition {
    private final String name;
    private final Type type;
    private final Set<String> aliases;
    private final String stereotype;
    private final AnnotationMetadata annotations;
    private final boolean preferred;
    private final boolean lazy;
    private final Set<String> dependencies;
    private final BeanApplicableScope applicable;
    private final Map<String, Object> properties;

    /**
     * 使用 Bean 的名称和一个指示是否为单例的值初始化 {@link DefaultBeanDefinition} 类的新实例。
     *
     * @param name 表示 Bean 的名称的 {@link Set}{@code <}{@link String}{@code >}。
     * @param type 表示 Bean 的类型的 {@link Type}。
     * @param aliases 表示 Bean 的别名的 {@link Set}{@code <}{@link String}{@code >}。
     * @param stereotype 表示 Bean 上应用的模式的 {@link String}。
     * @param annotations 表示 Bean 上定义的注解元数据的 {@link AnnotationMetadata}。
     * @param preferred 若为 {@code true}，则只是当前 Bean 是首选的，否则不是首选的。
     * @param lazy 若为 {@code true}，则指示在需要时才加载 Bean，否则在启动容器时加载。
     * @param dependencies 表示所依赖的其他 Bean 的名称的集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @param applicable 表示 Bean 的可用范围的 {@link BeanApplicableScope}。
     * @param properties 表示 Bean 的属性值的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public DefaultBeanDefinition(String name, Type type, Set<String> aliases, String stereotype,
            AnnotationMetadata annotations, boolean preferred, boolean lazy, Set<String> dependencies,
            BeanApplicableScope applicable, Map<String, Object> properties) {
        this.name = nullIf(name, StringUtils.EMPTY);
        this.type = notNull(type, "The type of a bean cannot be null.");
        this.aliases = nullIf(aliases, Collections.emptySet());
        this.stereotype = nullIf(stereotype, Stereotype.SINGLETON);
        this.annotations = nullIf(annotations, AnnotationMetadata.empty());
        this.preferred = preferred;
        this.lazy = lazy;
        this.dependencies = nullIf(dependencies, Collections.emptySet());
        this.applicable = nullIf(applicable, BeanApplicableScope.INSENSITIVE);
        this.properties = nullIf(properties, Collections.emptyMap());
    }

    @Override
    public String name() {
        return this.name;
    }

    @Nonnull
    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public Set<String> aliases() {
        return this.aliases;
    }

    @Override
    public String stereotype() {
        return this.stereotype;
    }

    @Override
    public AnnotationMetadata annotations() {
        return this.annotations;
    }

    @Override
    public boolean preferred() {
        return this.preferred;
    }

    @Override
    public boolean lazy() {
        return this.lazy;
    }

    @Override
    public Set<String> dependencies() {
        return this.dependencies;
    }

    @Override
    public BeanApplicableScope applicable() {
        return this.applicable;
    }

    @Override
    public Map<String, Object> properties() {
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "[name={0}, aliases={1}, singleton={2}, preferred={3}, lazy={4}, dependencies={5}, applicable={6}]",
                this.name(),
                this.aliases(),
                this.singleton(),
                this.preferred(),
                this.lazy(),
                this.dependencies(),
                this.applicable());
    }

    /**
     * 为 {@link BeanDefinition.Builder} 提供默认实现。
     *
     * @author 梁济时
     * @since 2022-05-16
     */
    public static class Builder implements BeanDefinition.Builder {
        private String name;
        private Type type;
        private Set<String> aliases;
        private String stereotype;
        private AnnotationMetadata annotations;
        private boolean preferred;
        private boolean lazy;
        private Set<String> dependencies;
        private BeanApplicableScope applicable;
        private final Map<String, Object> config;

        /**
         * 使用作为初始值的 Bean 定义初始化 {@link Builder} 类的新实例。
         *
         * @param definition 表示作为初始值的 Bean 定义的 {@link BeanDefinition}。
         */
        public Builder(BeanDefinition definition) {
            this.config = new HashMap<>();
            if (definition != null) {
                this.name = definition.name();
                this.type = definition.type();
                this.aliases = definition.aliases();
                this.stereotype = definition.stereotype();
                this.annotations = definition.annotations();
                this.preferred = definition.preferred();
                this.lazy = definition.lazy();
                this.dependencies = definition.dependencies();
                this.applicable = definition.applicable();
                Optional.ofNullable(definition.properties()).ifPresent(this.config::putAll);
            }
        }

        @Override
        public BeanDefinition.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public BeanDefinition.Builder type(Type type) {
            this.type = type;
            return this;
        }

        @Override
        public BeanDefinition.Builder aliases(Set<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        @Override
        public BeanDefinition.Builder stereotype(String stereotype) {
            this.stereotype = stereotype;
            return this;
        }

        @Override
        public BeanDefinition.Builder annotations(AnnotationMetadata annotations) {
            this.annotations = annotations;
            return this;
        }

        @Override
        public BeanDefinition.Builder preferred(boolean preferred) {
            this.preferred = preferred;
            return this;
        }

        @Override
        public BeanDefinition.Builder lazy(boolean lazy) {
            this.lazy = lazy;
            return this;
        }

        @Override
        public BeanDefinition.Builder dependencies(Set<String> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        @Override
        public BeanDefinition.Builder applicable(BeanApplicableScope applicable) {
            this.applicable = applicable;
            return this;
        }

        @Override
        public BeanDefinition.Builder set(String key, Object value) {
            this.config.put(key, value);
            return this;
        }

        @Override
        public BeanDefinition build() {
            return new DefaultBeanDefinition(this.name,
                    this.type,
                    this.aliases,
                    this.stereotype,
                    this.annotations,
                    this.preferred,
                    this.lazy,
                    this.dependencies,
                    this.applicable,
                    this.config);
        }
    }
}
