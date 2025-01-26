/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.as;

import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.resource.ResourceTree;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.wildcard.Pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 为资源树提供基类。
 *
 * @author 梁济时
 * @since 2023-02-10
 */
abstract class AbstractResourceTree implements ResourceTree {
    private final ResourceNodeCollection roots;

    AbstractResourceTree() {
        this.roots = new ResourceNodeCollection(this);
    }

    @Override
    public final ResourceNodeCollection roots() {
        return this.roots;
    }

    @Override
    public void traverse(Consumer<FileNode> consumer) {
        this.roots.traverse(null, consumer);
    }

    @Override
    public void traverse(Predicate<FileNode> filter, Consumer<FileNode> consumer) {
        this.roots.traverse(filter, consumer);
    }

    @Nullable
    @Override
    public Node nodeAt(String path) {
        notNull(path, "The path to retrieve node cannot be null.");
        String[] parts = StringUtils.split(path, JarEntryLocation.ENTRY_PATH_SEPARATOR);
        NodeCollection collection = this.roots();
        int index = parts.length - 1;
        for (int i = 0; i < index && collection != null; i++) {
            Node node = collection.get(parts[i]);
            DirectoryNode directory = as(node, DirectoryNode.class);
            if (directory == null) {
                collection = null;
            } else {
                collection = directory.children();
            }
        }
        if (collection == null) {
            return null;
        } else {
            return collection.get(parts[index]);
        }
    }

    @Override
    public List<FileNode> match(Pattern<String> pattern) {
        notNull(pattern, "The pattern to match file nodes cannot be null.");
        List<Node> nodes = pattern.match(listOf(this.roots()), AbstractResourceTree::children, Node::name);
        return nodes.stream()
                .filter(FileNode.class::isInstance)
                .map(FileNode.class::cast)
                .collect(Collectors.toList());
    }

    private static List<Node> listOf(NodeCollection nodes) {
        List<Node> list = new ArrayList<>(nodes.count());
        for (Node node : nodes) {
            list.add(node);
        }
        return list;
    }

    private static List<Node> children(Node node) {
        DirectoryNode directory = as(node, DirectoryNode.class);
        if (directory == null) {
            return Collections.emptyList();
        } else {
            return listOf(directory.children());
        }
    }
}
