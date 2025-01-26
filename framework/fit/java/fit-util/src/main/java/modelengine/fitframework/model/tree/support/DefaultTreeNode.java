/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.tree.support;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.mapIfNotNull;

import modelengine.fitframework.model.tree.Tree;
import modelengine.fitframework.model.tree.TreeNode;
import modelengine.fitframework.model.tree.TreeNodeCollection;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 为 {@link TreeNode} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-08-10
 */
final class DefaultTreeNode implements TreeNode {
    private final String name;
    private Object data;
    private final DefaultTreeNodeCollection children;

    private DefaultTreeNodeCollection collection;

    /**
     * 使用节点的名称初始化 {@link DefaultTreeNode} 类的新实例。
     *
     * @param name 表示节点名称的 {@link String}。
     * @throws IllegalArgumentException {@code name} 为 {@code null} 或空字符串。
     */
    DefaultTreeNode(String name) {
        this.name = name;
        this.children = new DefaultTreeNodeCollection(null, this);
    }

    void bind(DefaultTreeNodeCollection collection) {
        this.collection = collection;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String path() {
        Tree tree = this.tree();
        if (tree == null) {
            return this.name();
        }
        char separator = tree.pathSeparator();
        List<String> names = new LinkedList<>();
        TreeNode current = this;
        while (current != null) {
            names.add(current.name());
            current = current.parent();
        }
        Collections.reverse(names);
        return StringUtils.join(separator, names);
    }

    @Override
    public TreeNode parent() {
        return mapIfNotNull(this.collection, DefaultTreeNodeCollection::node);
    }

    @Override
    public TreeNodeCollection children() {
        return this.children;
    }

    @Override
    public Tree tree() {
        TreeNode parent = this.parent();
        if (parent == null) {
            return mapIfNotNull(this.collection, DefaultTreeNodeCollection::tree);
        } else {
            return this.parent().tree();
        }
    }

    @Override
    public Object tag() {
        return cast(this.data);
    }

    @Override
    public void tag(Object data) {
        this.data = data;
    }

    @Override
    public void remove() {
        this.collection.remove(this.name);
    }

    @Override
    public boolean anyInPath(Predicate<TreeNode> predicate) {
        if (predicate == null) {
            return false;
        }
        TreeNode current = this;
        while (current != null) {
            if (predicate.test(current)) {
                return true;
            } else {
                current = current.parent();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.path();
    }
}
