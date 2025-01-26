/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree.support;

import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNode;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNodePropertySource;
import modelengine.fitframework.util.convert.Converter;

/**
 * 为注解的属性提供数据来源定义。
 *
 * @author 梁济时
 * @since 2022-05-03
 */
class DefaultAnnotationTreeNodePropertySource implements AnnotationTreeNodePropertySource {
    private final AnnotationTreeNode node;
    private final String property;
    private final Converter converter;

    /**
     * 使用数据来源的注解节点和属性名称初始化 {@link DefaultAnnotationTreeNodePropertySource} 类的新实例。
     *
     * @param node 表示注解节点的 {@link AnnotationTreeNode}。
     * @param property 表示属性名称的 {@link String}。
     * @param converter 表示原始值的转换程序的 {@link Converter}。
     */
    DefaultAnnotationTreeNodePropertySource(AnnotationTreeNode node, String property, Converter converter) {
        this.node = node;
        this.property = property;
        this.converter = converter;
    }

    @Override
    public AnnotationTreeNode node() {
        return this.node;
    }

    @Override
    public String property() {
        return this.property;
    }

    @Override
    public Converter converter() {
        return this.converter;
    }
}
