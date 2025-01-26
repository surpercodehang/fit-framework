/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree.support;

import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNodeProperty;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 为注解提供数据来源。
 *
 * @author 梁济时
 * @since 2022-07-07
 */
final class AnnotationSource {
    private final Class<? extends Annotation> type;
    private final Map<String, List<AnnotationTreeNodeProperty>> properties;

    /**
     * 使用注解类型创建 {@link AnnotationSource} 类的新实例。
     *
     * @param type 表示注解类型的 {@link Class}。
     */
    AnnotationSource(Class<? extends Annotation> type) {
        this.type = type;
        this.properties = new HashMap<>();
    }

    /**
     * 获取注解的类型。
     *
     * @return 表示注解类型的 {@link Class}。
     */
    Class<? extends Annotation> type() {
        return this.type;
    }

    /**
     * 为注解的指定属性添加数据来源。
     *
     * @param property 表示属性名称的 {@link String}。
     * @param sources 表示注解属性的数据来源的 {@link List}{@code <}{@link AnnotationTreeNodeProperty}{@code >}。
     */
    void add(String property, List<AnnotationTreeNodeProperty> sources) {
        this.properties.computeIfAbsent(property, key -> new LinkedList<>()).addAll(sources);
    }

    /**
     * 将指定数据来源添加到当前数据来源。
     *
     * @param another 表示待添加的数据来源的 {@link AnnotationSource}。
     */
    void add(AnnotationSource another) {
        another.properties.forEach(this::add);
    }

    /**
     * 获取注解中包含属性名称的集合。
     *
     * @return 表示属性名称集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> properties() {
        return this.properties.keySet();
    }

    /**
     * 获取指定名称属性的数据来源。
     *
     * @param property 表示属性名称的 {@link String}。
     * @return 表示注解属性的数据来源的 {@link List}{@code <}{@link AnnotationTreeNodeProperty}{@code >}。
     */
    List<AnnotationTreeNodeProperty> sources(String property) {
        List<AnnotationTreeNodeProperty> sources = this.properties.get(property);
        if (sources == null) {
            sources = Collections.emptyList();
        }
        return sources;
    }
}
