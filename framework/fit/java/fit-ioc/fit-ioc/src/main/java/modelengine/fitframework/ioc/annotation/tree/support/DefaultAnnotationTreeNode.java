/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree.support;

import modelengine.fitframework.ioc.annotation.AnnotationProperty;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTree;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNode;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNodeProperty;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNodePropertySource;
import modelengine.fitframework.util.convert.Converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为注解树中的节点提供定义。
 *
 * @author 梁济时
 * @since 2022-05-03
 */
class DefaultAnnotationTreeNode extends AbstractAnnotationTreeNodeContainer implements AnnotationTreeNode {
    private final AnnotationTree tree;
    private final AnnotationTreeNode parent;
    private final Annotation annotation;
    private final Map<String, AnnotationTreeNodeProperty> properties;
    private final Map<String, List<AnnotationTreeNodePropertySource>> sources;

    /**
     * 使用所在的注解树、父节点及注解信息初始化 {@link DefaultAnnotationTreeNode} 类的新实例。
     *
     * @param tree 表示所在的注解树的 {@link AnnotationTree}。
     * @param parent 表示父节点的 {@link AnnotationTreeNode}。
     * @param annotation 表示包含的注解信息的 {@link Annotation}。
     */
    DefaultAnnotationTreeNode(AnnotationTree tree, AnnotationTreeNode parent, Annotation annotation) {
        this.tree = tree;
        this.parent = parent;

        this.annotation = annotation;
        this.properties = Stream.of(this.annotation.annotationType().getDeclaredMethods())
                .map(this::value)
                .collect(Collectors.toMap(AnnotationTreeNodeProperty::name, Function.identity()));

        this.sources = new HashMap<>();
    }

    @Override
    public AnnotationTreeNode createNode(Annotation annotation) {
        return new DefaultAnnotationTreeNode(null, this, annotation);
    }

    @Override
    public AnnotationTree tree() {
        AnnotationTree annotationTree = this.tree;
        if (annotationTree == null) {
            annotationTree = this.parent().tree();
        }
        return annotationTree;
    }

    @Override
    public AnnotationTreeNode parent() {
        return this.parent;
    }

    /**
     * 获取节点包含注解的类型。
     *
     * @return 表示注解类型的 {@link Class}。
     */
    @Override
    public Class<? extends Annotation> type() {
        return this.annotation.annotationType();
    }

    /**
     * 获取注解节点包含属性的集合。
     *
     * @return 表示属性集合的 {@link Collection}{@code <}{@link AnnotationTreeNodeProperty}{@code >}。
     */
    @Override
    public Collection<AnnotationTreeNodeProperty> properties() {
        return this.properties.values();
    }

    /**
     * 获取指定名称的属性。
     *
     * @param name 表示属性名称的 {@link String}。
     * @return 若存在该名称的属性，则为表示该属性的 {@link Optional}{@code <}{@link AnnotationTreeNodeProperty}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    @Override
    public Optional<AnnotationTreeNodeProperty> property(String name) {
        return Optional.ofNullable(this.properties.get(name));
    }

    /**
     * 为指定名称的属性设置数据来源。
     *
     * @param property 表示属性名称的 {@link String}。
     * @param sourceNode 表示数据来源的注解节点的 {@link AnnotationTreeNode}。
     * @param sourceProperty 表示数据来源的属性名称的 {@link String}。
     * @param converter 表示数据类型转换器的 {@link Converter}。
     */
    @Override
    public void source(String property, AnnotationTreeNode sourceNode, String sourceProperty, Converter converter) {
        this.sources.computeIfAbsent(property, key -> new LinkedList<>())
                .add(new DefaultAnnotationTreeNodePropertySource(sourceNode, sourceProperty, converter));
    }

    /**
     * 列出指定名称的属性的数据来源。
     *
     * @param property 表示属性名称的 {@link String}。
     * @return 表示数据来源的集合的 {@link List}{@code <}{@link DefaultAnnotationTreeNodePropertySource}{@code >}。
     */
    @Override
    public List<AnnotationTreeNodePropertySource> sources(String property) {
        List<AnnotationTreeNodePropertySource> propertySources = this.sources.get(property);
        if (propertySources == null) {
            propertySources = Collections.emptyList();
        }
        return propertySources;
    }

    /**
     * 将指定名称的属性的值转发到指定的注解属性。
     *
     * @param property 表示待转发的属性的名称的 {@link String}。
     * @param target 表示转发到的目标属性的 {@link AnnotationProperty}。
     * @param converter 表示数据类型转换器的 {@link Converter}。
     */
    @Override
    public void forward(String property, AnnotationProperty target, Converter converter) {
        List<AnnotationTreeNode> nodes = new ArrayList<>();
        if (target.annotation() == this.type()) {
            nodes.add(this);
        }
        nodes.addAll(this.all(target.annotation()));
        nodes.forEach(node -> node.source(target.name(), this, property, converter));
    }

    /**
     * 为指定的方法创建注解节点的属性。
     *
     * @param method 表示注解属性的方法的 {@link Method}。
     * @return 表示从方法创建的注解属性的 {@link AnnotationTreeNodeProperty}。
     */
    private AnnotationTreeNodeProperty value(Method method) {
        method.setAccessible(true);
        Object value;
        try {
            value = method.invoke(this.annotation);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to access method to read annotation property. [method=%s.%s()]",
                    this.annotation.annotationType().getName(),
                    method.getName()), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to invoke method to read annotation property. [method=%s.%s()]",
                    this.annotation.annotationType().getName(),
                    method.getName()), e);
        }
        return new DefaultAnnotationTreeNodeProperty(this, method.getName(), method.getDefaultValue(), value);
    }
}
