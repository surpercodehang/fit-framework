/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.tree.support;

import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNode;
import modelengine.fitframework.ioc.annotation.tree.AnnotationTreeNodeContainer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * 为注解树节点提供容器。
 *
 * @author 梁济时
 * @since 2022-05-03
 */
public abstract class AbstractAnnotationTreeNodeContainer implements AnnotationTreeNodeContainer {
    private final Map<Class<? extends Annotation>, AnnotationTreeNode> nodes;

    /**
     * 初始化 {@link AbstractAnnotationTreeNodeContainer} 类的新实例。
     */
    AbstractAnnotationTreeNodeContainer() {
        this.nodes = new LinkedHashMap<>();
    }

    /**
     * 将指定的节点添加到容器中。
     *
     * @param node 表示待添加的节点的 {@link DefaultAnnotationTreeNode}。
     */
    @Override
    public void add(AnnotationTreeNode node) {
        this.nodes.put(node.type(), node);
    }

    /**
     * 获取包含的所有节点。
     *
     * @return 表示节点集合的 {@link Collection}{@code <}{@link DefaultAnnotationTreeNode}{@code >}。
     */
    @Override
    public Collection<AnnotationTreeNode> nodes() {
        return this.nodes.values();
    }

    /**
     * 递归查找所有指定注解类型的节点。
     *
     * @param type 表示注解类型的 {@link Class}。
     * @return 表示包含的所有该类型注解的节点的 {@link List}{@code <}{@link DefaultAnnotationTreeNode}{@code >}。
     */
    @Override
    public List<AnnotationTreeNode> all(Class<? extends Annotation> type) {
        List<AnnotationTreeNode> treeNodes = new ArrayList<>();
        Queue<AnnotationTreeNode> children = new LinkedList<>(this.nodes());
        while (!children.isEmpty()) {
            AnnotationTreeNode child = children.poll();
            if (Objects.equals(child.type(), type)) {
                treeNodes.add(child);
            }
            children.addAll(child.nodes());
        }
        return treeNodes;
    }
}
