/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree;

import java.util.List;

/**
 * 为注解树的节点提供属性定义。
 *
 * @author 梁济时
 * @since 2022-07-07
 */
public interface AnnotationTreeNodeProperty {
    /**
     * 获取属性所属的节点。
     *
     * @return 表示注解节点的 {@link AnnotationTreeNode}。
     */
    AnnotationTreeNode node();

    /**
     * 获取属性名称。
     *
     * @return 表示属性名称的 {@link String}。
     */
    String name();

    /**
     * 获取属性的默认值。
     *
     * @return 表示属性默认值的 {@link Object}。
     */
    Object defaultValue();

    /**
     * 获取属性的实际值。
     *
     * @return 表示属性实际值的 {@link Object}。
     */
    Object value();

    /**
     * 获取属性的数据来源。
     *
     * @return 表示数据来源的列表的 {@link List}{@code <}{@link AnnotationTreeNodeProperty}{@code >}。
     */
    List<AnnotationTreeNodeProperty> sources();
}
