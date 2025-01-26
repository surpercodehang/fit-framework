/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree;

import modelengine.fitframework.ioc.annotation.AnnotationProperty;
import modelengine.fitframework.util.convert.Converter;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 为注解树提供节点定义。
 *
 * @author 梁济时
 * @since 2022-07-07
 */
public interface AnnotationTreeNode extends AnnotationTreeNodeContainer {
    /**
     * 获取节点所在的注解树。
     *
     * @return 表示注解树的 {@link AnnotationTree}。
     */
    AnnotationTree tree();

    /**
     * 获取上级节点。
     *
     * @return 若存在上级节点，则为表示上级节点的 {@link AnnotationTreeNode}；否则为 {@code null}。
     */
    AnnotationTreeNode parent();

    /**
     * 获取节点包含注解的类型。
     *
     * @return 表示注解类型的 {@link Class}。
     */
    Class<? extends Annotation> type();

    /**
     * 获取注解节点包含属性的集合。
     *
     * @return 表示属性集合的 {@link Collection}{@code <}{@link AnnotationTreeNodeProperty}{@code >}。
     */
    Collection<AnnotationTreeNodeProperty> properties();

    /**
     * 获取指定名称的属性。
     *
     * @param name 表示属性名称的 {@link String}。
     * @return 若存在该名称的属性，则为表示该属性的 {@link Optional}{@code <}{@link AnnotationTreeNodeProperty}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<AnnotationTreeNodeProperty> property(String name);

    /**
     * 为指定名称的属性设置数据来源。
     *
     * @param property 表示属性名称的 {@link String}。
     * @param sourceNode 表示数据来源的注解节点的 {@link AnnotationTreeNode}。
     * @param sourceProperty 表示数据来源的属性名称的 {@link String}。
     * @param converter 表示应用源属性的值时使用的转换程序的 {@link Converter}。
     */
    void source(String property, AnnotationTreeNode sourceNode, String sourceProperty, Converter converter);

    /**
     * 列出指定名称的属性的数据来源。
     *
     * @param property 表示属性名称的 {@link String}。
     * @return 表示数据来源的集合的 {@link List}{@code <}{@link AnnotationTreeNodePropertySource}{@code >}。
     */
    List<AnnotationTreeNodePropertySource> sources(String property);

    /**
     * 将指定名称的属性的值转发到指定的注解属性。
     *
     * @param property 表示待转发的属性的名称的 {@link String}。
     * @param target 表示转发到的目标属性的 {@link AnnotationProperty}。
     * @param converter 表示转发时应用的值转换程序的 {@link Converter}。
     */
    void forward(String property, AnnotationProperty target, Converter converter);
}
