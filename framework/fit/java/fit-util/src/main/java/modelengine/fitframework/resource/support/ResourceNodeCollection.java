/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.as;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.resource.ResourceTree;
import modelengine.fitframework.util.FunctionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为资源树提供节点集合的实现。
 *
 * @author 梁济时
 * @since 2023-02-10
 */
final class ResourceNodeCollection implements ResourceTree.NodeCollection {
    private final ResourceTree tree;
    private final ResourceTree.DirectoryNode directory;

    private final Map<String, ResourceTree.Node> nodes;
    private final List<String> names;

    ResourceNodeCollection(ResourceTree tree) {
        this(tree, null);
    }

    ResourceNodeCollection(ResourceTree.DirectoryNode directory) {
        this(null, directory);
    }

    private ResourceNodeCollection(ResourceTree tree, ResourceTree.DirectoryNode directory) {
        this.tree = tree;
        this.directory = directory;

        this.nodes = new HashMap<>();
        this.names = new ArrayList<>();
    }

    final ResourceTree tree() {
        ResourceTree resourceTree = this.tree;
        if (resourceTree == null) {
            resourceTree = this.directory.tree();
        }
        return resourceTree;
    }

    final ResourceTree.DirectoryNode directory() {
        return this.directory;
    }

    @Override
    public int count() {
        return this.nodes.size();
    }

    @Override
    public ResourceTree.Node get(String name) {
        return this.nodes.get(name);
    }

    @Override
    public ResourceTree.Node get(int index) {
        return this.nodes.get(this.names.get(index));
    }

    @Override
    public Stream<ResourceTree.Node> stream() {
        return this.names.stream().map(this.nodes::get);
    }

    @Override
    public List<ResourceTree.Node> toList() {
        return this.stream().collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public Iterator<ResourceTree.Node> iterator() {
        return this.stream().iterator();
    }

    ResourceTreeDirectoryNode mkdirs(String name) {
        ResourceTree.Node node = this.get(name);
        if (node == null) {
            ResourceTreeDirectoryNode directoryNode = new ResourceTreeDirectoryNode(this, name);
            this.add(directoryNode);
            return directoryNode;
        }
        if (node instanceof ResourceTreeDirectoryNode) {
            return (ResourceTreeDirectoryNode) node;
        } else {
            throw new IllegalStateException(StringUtils.format(
                    "A node with the same name already exists but not a directory. [path={0}, type={1}]",
                    node.path(),
                    node.getClass().getName()));
        }
    }

    void add(ResourceTree.Node node) {
        String name = node.name();
        if (this.nodes.containsKey(name)) {
            throw new IllegalStateException(StringUtils.format("A node with the same name already exists. [name={0}]",
                    node));
        }
        this.nodes.put(name, node);
        this.names.add(name);
    }

    void traverse(Predicate<ResourceTree.FileNode> filter, Consumer<ResourceTree.FileNode> consumer) {
        notNull(consumer, "The consumer to accept file nodes cannot be null.");
        Stack<ResourceTree.Node> stack = new Stack<>();
        pushAll(stack, this);
        while (!stack.empty()) {
            ResourceTree.Node node = stack.pop();
            ResourceTree.DirectoryNode directoryNode = as(node, ResourceTree.DirectoryNode.class);
            if (directoryNode != null) {
                pushAll(stack, directoryNode.children());
                continue;
            }
            ResourceTree.FileNode file = cast(node);
            if (FunctionUtils.test(filter, file, true)) {
                consumer.accept(file);
            }
        }
    }

    private static void pushAll(Stack<ResourceTree.Node> stack, ResourceTree.NodeCollection nodes) {
        for (int i = nodes.count() - 1; i >= 0; i--) {
            stack.push(nodes.get(i));
        }
    }
}
