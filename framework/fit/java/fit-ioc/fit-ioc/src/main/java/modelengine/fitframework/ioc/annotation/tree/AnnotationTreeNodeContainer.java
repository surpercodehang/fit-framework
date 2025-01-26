/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

/**
 * 为注解树节点提供容器。
 *
 * @author 梁济时
 * @since 2022-07-07
 */
public interface AnnotationTreeNodeContainer {
    /**
     * 为指定的注解创建节点。
     *
     * @param annotation 表示待创建节点的注解的 {@link Annotation}。
     * @return 表示注解树的节点的 {@link AnnotationTreeNode}。
     */
    AnnotationTreeNode createNode(Annotation annotation);

    /**
     * 将指定的节点添加到容器中。
     *
     * @param node 表示待添加的节点的 {@link AnnotationTreeNode}。
     */
    void add(AnnotationTreeNode node);

    /**
     * 获取包含的所有节点。
     *
     * @return 表示节点集合的 {@link Collection}{@code <}{@link AnnotationTreeNode}{@code >}。
     */
    Collection<AnnotationTreeNode> nodes();

    /**
     * 递归查找所有指定注解类型的节点。
     *
     * @param type 表示注解类型的 {@link Class}。
     * @return 表示包含的所有该类型注解的节点的 {@link List}{@code <}{@link AnnotationTreeNode}{@code >}。
     */
    List<AnnotationTreeNode> all(Class<? extends Annotation> type);
}
