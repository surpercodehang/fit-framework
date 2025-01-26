/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree;

import modelengine.fitframework.util.convert.Converter;

/**
 * 为注解的属性提供数据来源定义。
 *
 * @author 梁济时
 * @since 2022-07-07
 */
public interface AnnotationTreeNodePropertySource {
    /**
     * 获取数据来源的注解节点。
     *
     * @return 表示注解节点的 {@link AnnotationTreeNode}。
     */
    AnnotationTreeNode node();

    /**
     * 获取数据来源的属性名称。
     *
     * @return 表示属性名称的 {@link String}。
     */
    String property();

    /**
     * 获取值转换程序。
     *
     * @return 表示值转换程序的 {@link Converter}。
     */
    Converter converter();
}
