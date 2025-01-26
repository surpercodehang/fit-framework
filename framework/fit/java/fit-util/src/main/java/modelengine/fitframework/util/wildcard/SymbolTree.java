/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

import modelengine.fitframework.util.wildcard.support.DefaultSymbolTree;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * 为符号提供树形结构。
 *
 * @param <V> 表示符号的类型。
 * @author 梁济时
 * @since 2022-08-01
 */
public interface SymbolTree<V, S> {
    /**
     * 获取根节点的集合。
     *
     * @return 表示根节点集合的 {@link NodeCollection}。
     */
    NodeCollection<V, S> roots();

    /**
     * 为符号树提供节点。
     *
     * @param <V> 表示符号的类型。
     * @author 梁济时
     * @since 2022-08-01
     */
    interface Node<V, S> {
        /**
         * 获取节点对应的符号。
         *
         * @return 表示符号的 {@link Object}。
         */
        S symbol();

        /**
         * 获取节点对应的值。
         *
         * @return 表示节点的值的 {@link Object}。
         */
        V value();

        /**
         * 获取子节点的集合。
         *
         * @return 表示子节点集合的 {@link NodeCollection}。
         */
        NodeCollection<V, S> children();
    }

    /**
     * 为树中的节点提供集合。
     *
     * @param <V> 表示符号的类型。
     * @author 梁济时
     * @since 2022-08-01
     */
    interface NodeCollection<V, S> extends Iterable<Node<V, S>> {
        /**
         * 获取集合中节点的数量。
         *
         * @return 表示节点数量的32位整数。
         */
        int size();

        /**
         * 获取一个值，该值指示集合中是否不包含任何节点。
         *
         * @return 若不包含任何节点，则为 {@code true}；否则为 {@code false}。
         */
        default boolean empty() {
            return this.size() < 1;
        }

        /**
         * 获取指定索引处的节点。
         *
         * @param index 表示节点在集合中的索引的32位整数。
         * @return 表示该索引处的节点的 {@link Node}。
         * @throws IndexOutOfBoundsException 索引超出限制。
         */
        Node<V, S> get(int index);

        @Override
        default Iterator<Node<V, S>> iterator() {
            return Wildcards.iterator(this);
        }
    }

    /**
     * 使用用以获取子符号的方法，和根符号的列表创建符号树的新实例。
     *
     * @param roots 表示根符号的列表的 {@link List}。
     * @param childrenMapper 表示用以通过符号获取子符号的方法的 {@link Function}。
     * @param symbolMapper 表示符号的获取方法的 {@link Function}。
     * @param <V> 表示元数据的类型。
     * @param <S> 表示符号的类型。
     * @return 表示新创建的符号树的 {@link SymbolTree}。
     */
    static <V, S> SymbolTree<V, S> create(List<V> roots, Function<V, List<V>> childrenMapper,
            Function<V, S> symbolMapper) {
        return new DefaultSymbolTree<>(roots, childrenMapper, symbolMapper);
    }
}
