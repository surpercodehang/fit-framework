/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.dispatch.support;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.dispatch.MappingTree;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@link MappingTree} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-07-26
 */
public class DefaultMappingTree<T> implements MappingTree<T> {
    /** 表示路径分隔符的 {@link String}。 */
    public static final String PATH_SEPARATOR = "/";

    private static final String WILD_KEY = "*";

    private final Map<String, MappingTreeNode<T>> nodes = new ConcurrentHashMap<>();

    /**
     * 将指定路径按照路径分隔符进行切分。
     *
     * @param path 表示待切分的完整路径的 {@link String}。
     * @return 表示切分后路径的 {@link Deque}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当 {@code path} 为 {@code null} 时。
     */
    public static Deque<String> splitPath(String path) {
        notNull(path, "The path cannot be null.");
        return StringUtils.split(path, PATH_SEPARATOR, LinkedList::new, StringUtils::isNotBlank);
    }

    @Override
    public Optional<T> register(String pathPattern, T handler) {
        notBlank(pathPattern, "The path pattern to register cannot be blank.");
        notNull(handler, "The http handler to register cannot be null.");
        String actualPathPattern = pathPattern.trim();
        isTrue(actualPathPattern.startsWith(PATH_SEPARATOR),
                "The path pattern is not start with '/'. [pathPattern={0}]",
                pathPattern);
        Queue<String> pathFragments = splitPath(actualPathPattern);
        isTrue(CollectionUtils.isNotEmpty(pathFragments),
                "The path pattern cannot only contain '/'. [pathPattern={0}]",
                pathPattern);
        return this.nodes.computeIfAbsent(pathFragments.remove(), fragment -> new MappingTreeNode<>())
                .register(pathFragments, handler);
    }

    @Override
    public void unregister(String pathPattern) {
        notBlank(pathPattern, "The path pattern to unregister cannot be blank.");
        String actualPathPattern = pathPattern.trim();
        isTrue(actualPathPattern.startsWith(PATH_SEPARATOR),
                "The path pattern is not start with '/'. [pathPattern={0}]",
                pathPattern);
        Queue<String> pathFragments = splitPath(actualPathPattern);
        isTrue(CollectionUtils.isNotEmpty(pathFragments),
                "The path pattern cannot only contain '/'. [pathPattern={0}]",
                pathPattern);
        String removed = pathFragments.remove();
        if (!this.nodes.containsKey(removed)) {
            return;
        }
        MappingTreeNode<T> treeNode = this.nodes.get(removed);
        boolean isEmpty = treeNode.unregister(pathFragments);
        if (isEmpty) {
            this.nodes.remove(removed);
        }
    }

    @Override
    public Optional<T> search(String path) {
        notBlank(path, "The search path cannot be blank.");
        String actualPath = path.trim();
        isTrue(actualPath.startsWith(PATH_SEPARATOR), "The search path is not start with '/'. [path={0}]", path);
        Deque<String> pathFragments = splitPath(actualPath);
        isTrue(CollectionUtils.isNotEmpty(pathFragments),
                "The search path cannot only contain '/'. [pathPattern={0}]",
                path);
        MappingTreeNode<T> childNode = this.nodes.get(pathFragments.removeFirst());
        Optional<T> httpHandler = Optional.ofNullable(childNode).flatMap(node -> node.search(pathFragments));
        if (httpHandler.isPresent()) {
            return httpHandler;
        }
        MappingTreeNode<T> wildChildNode = this.nodes.get(WILD_KEY);
        return Optional.ofNullable(wildChildNode).flatMap(node -> node.search(pathFragments));
    }

    @Override
    public List<T> getAllHandlers() {
        Collection<MappingTreeNode<T>> values = this.nodes.values();
        return values.stream()
                .filter(Objects::nonNull)
                .flatMap(value -> this.getNodeHandlers(value).stream())
                .collect(Collectors.toList());
    }

    private List<T> getNodeHandlers(MappingTreeNode<T> node) {
        List<T> handlers = new ArrayList<>();
        if (node.handler != null) {
            handlers.add(node.handler);
        }
        if (!node.children.isEmpty()) {
            List<T> childrenHandlers = node.children.values()
                    .stream()
                    .filter(Objects::nonNull)
                    .flatMap(child -> this.getNodeHandlers(child).stream())
                    .collect(Collectors.toList());
            handlers.addAll(childrenHandlers);
        }
        return handlers;
    }

    private static class MappingTreeNode<T> {
        private final Map<String, MappingTreeNode<T>> children = new ConcurrentHashMap<>();
        private T handler;

        private Optional<T> register(Queue<String> pathFragments, T handler) {
            if (pathFragments.isEmpty()) {
                T pre = this.handler;
                this.handler = handler;
                return Optional.ofNullable(pre);
            }
            return this.children.computeIfAbsent(pathFragments.remove(), fragment -> new MappingTreeNode<>())
                    .register(pathFragments, handler);
        }

        private boolean unregister(Queue<String> pathFragments) {
            if (pathFragments.isEmpty()) {
                this.handler = null;
                return this.children.isEmpty();
            }
            String removed = pathFragments.remove();
            if (!this.children.containsKey(removed)) {
                return this.children.isEmpty();
            }
            boolean isEmpty = this.children.get(removed).unregister(pathFragments);
            if (isEmpty) {
                this.children.remove(removed);
            }
            return this.children.isEmpty();
        }

        private Optional<T> search(Deque<String> pathFragments) {
            if (pathFragments.isEmpty()) {
                return Optional.ofNullable(this.handler);
            }
            String removed = pathFragments.removeFirst();
            MappingTreeNode<T> child = this.children.get(removed);
            Optional<T> httpHandler = Optional.ofNullable(child).flatMap(node -> node.search(pathFragments));
            if (httpHandler.isPresent()) {
                return httpHandler;
            }
            MappingTreeNode<T> wildChild = this.children.get(WILD_KEY);
            httpHandler = Optional.ofNullable(wildChild).flatMap(node -> node.search(pathFragments));
            if (httpHandler.isPresent()) {
                return httpHandler;
            }
            pathFragments.addFirst(removed);
            return Optional.empty();
        }
    }
}
