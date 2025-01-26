/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.tree.support;

import modelengine.fitframework.model.tree.TreeNode;
import modelengine.fitframework.model.tree.TreeNodeCollection;
import modelengine.fitframework.util.support.MappedIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 为 {@link TreeNodeCollection} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-08-10
 */
final class DefaultTreeNodeCollection implements TreeNodeCollection {
    private final DefaultTree tree;
    private final DefaultTreeNode node;
    private final Map<String, DefaultTreeNode> nodes;
    private final List<String> names;

    /**
     * 使用所属的树或父节点初始化 {@link DefaultTreeNodeCollection} 类的新实例。
     *
     * @param tree 表示所属的树的 {@link DefaultTree}。
     * @param node 表示所属的节点的 {@link DefaultTreeNode}。
     */
    DefaultTreeNodeCollection(DefaultTree tree, DefaultTreeNode node) {
        this.tree = tree;
        this.node = node;
        this.nodes = new HashMap<>();
        this.names = new ArrayList<>();
    }

    /**
     * 获取所属的树。
     *
     * @return 表示所属树的 {@link DefaultTree}。
     */
    DefaultTree tree() {
        return this.tree;
    }

    /**
     * 获取所属的节点。
     *
     * @return 表示所属节点的 {@link DefaultTreeNode}。
     */
    DefaultTreeNode node() {
        return this.node;
    }

    @Override
    public int size() {
        return this.nodes.size();
    }

    @Override
    public TreeNode get(int index) {
        return this.nodes.get(this.names.get(index));
    }

    @Override
    public TreeNode get(String name) {
        return this.nodes.get(name);
    }

    @Override
    public TreeNode remove(int index) {
        DefaultTreeNode removeNode = this.nodes.remove(this.names.remove(index));
        removeNode.bind(null);
        return removeNode;
    }

    @Override
    public TreeNode remove(String name) {
        int index = this.names.indexOf(name);
        if (index < 0) {
            return null;
        } else {
            return this.remove(index);
        }
    }

    @Override
    public Stream<TreeNode> stream() {
        return this.names.stream().map(this.nodes::get);
    }

    @Override
    public TreeNode getOrCreate(String name) {
        return this.nodes.computeIfAbsent(name, key -> {
            DefaultTreeNode treeNode = new DefaultTreeNode(name);
            treeNode.bind(this);
            this.names.add(name);
            return treeNode;
        });
    }

    @Override
    public Iterator<TreeNode> iterator() {
        return new MappedIterator<>(this.names.iterator(), this.nodes::get);
    }
}
