/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.wildcard.SymbolTree;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 为 {@link SymbolTree} 提供默认实现。
 *
 * @param <V> 表示元数据的类型。
 * @param <S> 表示符号的类型。
 * @author 梁济时
 * @since 2022-08-01
 */
public class DefaultSymbolTree<V, S> implements SymbolTree<V, S> {
    private final Function<V, List<V>> childrenMapper;
    private final Function<V, S> symbolMapper;
    private final NodeCollection roots;

    /**
     * 使用获取子符号的方法和根符号的列表初始化 {@link DefaultSymbolTree} 类的新实例。
     *
     * @param source 表示作为数据源的 {@link List}。
     * @param childrenMapper 表示用以通过符号获取子符号的方法的 {@link Function}。
     * @param symbolMapper 表示用以获取符号的方法的 {@link Function}。
     * @throws IllegalArgumentException {@code childrenMapper} 或 {@code roots} 为 {@code null}。
     */
    public DefaultSymbolTree(List<V> source, Function<V, List<V>> childrenMapper, Function<V, S> symbolMapper) {
        this.childrenMapper = notNull(childrenMapper, "The mapper to fetch children cannot be null.");
        this.symbolMapper = notNull(symbolMapper, "The mapper to fetch symbol cannot be null.");
        this.roots = this.new NodeCollection(source);
    }

    @Override
    public SymbolTree.NodeCollection<V, S> roots() {
        return this.roots;
    }

    @Override
    public String toString() {
        return StringUtils.format("[roots={0}]", this.roots());
    }

    private final class Node implements SymbolTree.Node<V, S> {
        private final V value;
        private final S symbol;
        private final NodeCollection children;

        private Node(V value) {
            this.value = value;
            this.symbol = DefaultSymbolTree.this.symbolMapper.apply(value);
            List<V> nodes = DefaultSymbolTree.this.childrenMapper.apply(value);
            this.children = DefaultSymbolTree.this.new NodeCollection(nodes);
        }

        @Override
        public S symbol() {
            return this.symbol;
        }

        @Override
        public V value() {
            return this.value;
        }

        @Override
        public NodeCollection children() {
            return this.children;
        }

        @Override
        public String toString() {
            return StringUtils.format("[symbol={0}, children={1}]", this.symbol(), this.children());
        }
    }

    private final class NodeCollection implements SymbolTree.NodeCollection<V, S> {
        private final List<Node> children;

        private NodeCollection(List<V> children) {
            this.children =
                    children.stream().map(child -> DefaultSymbolTree.this.new Node(child)).collect(Collectors.toList());
        }

        @Override
        public int size() {
            return this.children.size();
        }

        @Override
        public SymbolTree.Node<V, S> get(int index) {
            return this.children.get(index);
        }

        @Override
        public String toString() {
            return this.children.toString();
        }
    }
}
