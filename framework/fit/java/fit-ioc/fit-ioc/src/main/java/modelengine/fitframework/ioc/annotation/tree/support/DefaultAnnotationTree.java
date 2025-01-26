/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree.support;

import modelengine.fitframework.ioc.annotation.AnnotationDeclarationException;
import modelengine.fitframework.ioc.annotation.AnnotationDefinitionException;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTree;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNode;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNodeProperty;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.convert.Converter;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 为注解树提供定义。
 *
 * @author 梁济时
 * @since 2022-05-03
 */
public class DefaultAnnotationTree extends AbstractAnnotationTreeNodeContainer implements AnnotationTree {
    private static final Converter EMPTY_CONVERTER = value -> value;

    private final Map<Class<? extends Converter>, Converter> converters;

    /**
     * 创建一个新的注解树节点。
     */
    public DefaultAnnotationTree() {
        this.converters = new HashMap<>();
        this.converters.put(Converter.class, EMPTY_CONVERTER);
    }

    @Override
    public AnnotationTreeNode createNode(Annotation annotation) {
        return new DefaultAnnotationTreeNode(this, null, annotation);
    }

    /**
     * 合并指定属性节点的值。
     *
     * @param properties 表示待合并值的属性节点的集合的 {@link List}{@code <}{@link AnnotationTreeNodeProperty}{@code >}。
     * @return 表示合并后的值的 {@link Object}。
     * @throws AnnotationDeclarationException 在不同的数据来源中定义了不同的默认值。
     * @throws AnnotationDefinitionException 在不同的数据来源中定义了不同的实际值。
     */
    private Object merge(List<AnnotationTreeNodeProperty> properties) {
        List<AnnotationTreeNodeProperty> specified =
                properties.stream().filter(property -> !isDefault(property)).collect(Collectors.toList());
        if (specified.isEmpty()) {
            return this.merge(properties,
                    AnnotationTreeNodeProperty::defaultValue,
                    (property1, property2) -> new AnnotationDeclarationException(String.format(Locale.ROOT,
                            "The property of annotation have different default values declared in forwarding sources."
                                    + " [%s.%s()=%s, %s.%s()=%s]",
                            property1.node().type().getName(),
                            property1.name(),
                            property1.defaultValue(),
                            property2.node().type().getName(),
                            property2.name(),
                            property2.defaultValue())));
        } else {
            return this.merge(specified,
                    AnnotationTreeNodeProperty::value,
                    (property1, property2) -> new AnnotationDefinitionException(String.format(Locale.ROOT,
                            "The property of annotation received different values from forwarding sources. [%s.%s()"
                                    + "=%s, %s.%s()=%s]",
                            property1.node().type().getName(),
                            property1.name(),
                            property1.value(),
                            property2.node().type().getName(),
                            property2.name(),
                            property2.value())));
        }
    }

    private Object merge(List<AnnotationTreeNodeProperty> properties,
            Function<AnnotationTreeNodeProperty, Object> mapper,
            BiFunction<AnnotationTreeNodeProperty, AnnotationTreeNodeProperty, RuntimeException> exceptionSupplier) {
        Object value = mapper.apply(properties.get(0));
        for (int i = 1; i < properties.size(); i++) {
            int index = i;
            value = merge(value,
                    mapper.apply(properties.get(index)),
                    () -> exceptionSupplier.apply(properties.get(0), properties.get(index)));
        }
        return value;
    }

    private static Object merge(Object value1, Object value2, Supplier<RuntimeException> exceptionSupplier) {
        if (value1.getClass().isArray()) {
            int length1 = Array.getLength(value1);
            int length2 = Array.getLength(value2);
            int length = length1 + length2;
            Object array = Array.newInstance(value1.getClass().getComponentType(), length);
            int index = 0;
            for (int i = 0; i < length1; i++) {
                Object item = Array.get(value1, i);
                Array.set(array, index++, item);
            }
            for (int i = 0; i < length2; i++) {
                Object item = Array.get(value2, i);
                Array.set(array, index++, item);
            }
            return array;
        } else if (Objects.equals(value1, value2)) {
            return value1;
        } else {
            throw exceptionSupplier.get();
        }
    }

    private static boolean isDefault(AnnotationTreeNodeProperty property) {
        return Objects.equals(property.value(), property.defaultValue());
    }

    @Override
    public List<Annotation> toAnnotations() {
        List<Map<Class<? extends Annotation>, AnnotationSource>> sources =
                this.nodes().stream().map(this::sources).collect(Collectors.toList());
        Set<Class<? extends Annotation>> types =
                sources.stream().map(Map::keySet).flatMap(Collection::stream).collect(Collectors.toSet());
        List<Annotation> annotations = new LinkedList<>();
        for (Class<? extends Annotation> type : types) {
            Repeatable repeatable = type.getAnnotation(Repeatable.class);
            if (repeatable == null) {
                AnnotationSource source = new AnnotationSource(type);
                sources.stream().map(cache -> cache.get(type)).filter(Objects::nonNull).forEach(source::add);
                annotations.add(this.annotation(source));
            } else {
                annotations.addAll(sources.stream()
                        .map(cache -> cache.get(type))
                        .filter(Objects::nonNull)
                        .map(this::annotation)
                        .collect(Collectors.toList()));
            }
        }
        return annotations;
    }

    private Map<Class<? extends Annotation>, AnnotationSource> sources(AnnotationTreeNode root) {
        Map<Class<? extends Annotation>, AnnotationSource> sources = new HashMap<>();
        Queue<AnnotationTreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            AnnotationTreeNode node = queue.poll();
            queue.addAll(node.nodes());
            AnnotationSource source = sources.computeIfAbsent(node.type(), AnnotationSource::new);
            node.properties().forEach(property -> source.add(property.name(), property.sources()));
        }
        return sources;
    }

    private Annotation annotation(AnnotationSource source) {
        Map<String, Object> values = source.properties()
                .stream()
                .collect(Collectors.toMap(Function.identity(), property -> this.merge(source.sources(property))));
        return AnnotationInvocationHandler.proxy(source.type(), values);
    }

    @Override
    public Converter get(Class<? extends Converter> converterClass) {
        return this.converters.computeIfAbsent(converterClass, DefaultAnnotationTree::instantiateConverter);
    }

    private static Converter instantiateConverter(Class<? extends Converter> converterClass) {
        if (converterClass.isInterface()) {
            throw new AnnotationDeclarationException(StringUtils.format(
                    "The class of converter used to forward annotation property cannot be interface. [class={0}]",
                    converterClass.getName()));
        } else if (Modifier.isAbstract(converterClass.getModifiers())) {
            throw new AnnotationDeclarationException(StringUtils.format(
                    "The class of converter used to forward annotation property cannot be abstract. [class={0}]",
                    converterClass.getName()));
        } else {
            Constructor<? extends Converter> constructor;
            try {
                constructor = converterClass.getDeclaredConstructor();
            } catch (NoSuchMethodException ex) {
                throw new AnnotationDeclarationException(StringUtils.format(
                        "No default constructor found in class of converter. [class={0}]",
                        converterClass.getName()), ex);
            }
            try {
                return constructor.newInstance();
            } catch (IllegalAccessException ex) {
                throw new AnnotationDeclarationException(StringUtils.format(
                        "Failed to access default constructor to instantiate converter. [class={0}]",
                        constructor.getName()), ex);
            } catch (InstantiationException ex) {
                throw new AnnotationDeclarationException(StringUtils.format(
                        "Cannot instantiate converter with default constructor. [class={0}]",
                        converterClass.getName()), ex);
            } catch (InvocationTargetException ex) {
                throw new AnnotationDeclarationException(StringUtils.format(
                        "Failed to invoke default constructor to instantiate converter. [class={0}]",
                        converterClass.getName()), ex.getCause());
            }
        }
    }
}
