/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static modelengine.fitframework.util.ObjectUtils.mapIfNotNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.Stereotype;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 {@link BeanMetadata} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-04-26
 */
public class DefaultBeanMetadata implements BeanMetadata {
    private final BeanContainer container;
    private final String name;
    private final Set<String> aliases;
    private final Type type;
    private final String stereotype;
    private final boolean preferred;
    private final boolean lazy;
    private final Set<String> dependencies;
    private final BeanApplicableScope applicable;
    private final AnnotationMetadata annotations;
    private final Config config;

    /**
     * 使用 Bean 的名称和类型初始化 {@link DefaultBeanMetadata} 类的新实例。
     *
     * @param container 表示所属的 Bean 容器的 {@link BeanContainer}。
     * @param name 表示 Bean 的名称的 {@link String}。
     * @param aliases 表示 Bean 的别名的集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @param type 表示 Bean 的类型的 {@link Type}。
     * @param stereotype 表示 Bean 上应用的模式的 {@link String}。
     * @param preferred 若为 {@code true}，则 Bean 是首选的；否则不是首选的。
     * @param lazy 若为 {@code true}，则 Bean 在被使用时才会被加载，否则在预加载时即被加载。
     * @param dependencies 表示所依赖的 Bean 的集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @param applicable 表示 Bean 的可用范围的 {@link BeanApplicableScope}。
     * @param annotations 表示 Bean 所定义的注解的元数据的 {@link AnnotationMetadata}。
     * @param config 表示 Bean 的配置的 {@link Config}。
     */
    DefaultBeanMetadata(BeanContainer container, String name, Set<String> aliases, Type type, String stereotype,
            boolean preferred, boolean lazy, Set<String> dependencies, BeanApplicableScope applicable,
            AnnotationMetadata annotations, Config config) {
        this.container = Validation.notNull(container, "The owning bean container of a bean metadata cannot be null.");
        this.type = Validation.notNull(type, "The type of a bean metadata cannot be null.");
        this.name = StringUtils.trim(Validation.notBlank(name, "The name of a bean cannot be a blank string."));
        this.aliases = nullIf(aliases, Collections.<String>emptySet()).stream()
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
        this.stereotype = Optional.ofNullable(stereotype)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .orElse(Stereotype.SINGLETON);
        this.preferred = preferred;
        this.lazy = lazy;
        this.dependencies = nullIf(dependencies, Collections.emptySet());
        this.applicable = nullIf(applicable, BeanApplicableScope.INSENSITIVE);
        this.annotations = nullIf(annotations, AnnotationMetadata.empty());
        this.config = config;
    }

    @Nonnull
    @Override
    public BeanContainer container() {
        return this.container;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Set<String> aliases() {
        return this.aliases;
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public String stereotype() {
        return this.stereotype;
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
    public AnnotationMetadata annotations() {
        return this.annotations;
    }

    @Override
    public Config config() {
        return this.config;
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "[name={0}, aliases={1}, type={2}, stereotype={3}, preferred={4}, lazy={5} dependencies={6}, "
                        + "applicable={7}, annotations={8}]",
                this.name(),
                this.aliases(),
                mapIfNotNull(this.type(), Type::getTypeName),
                this.stereotype(),
                this.preferred(),
                this.lazy(),
                this.dependencies(),
                this.applicable(),
                this.annotations());
    }
}
