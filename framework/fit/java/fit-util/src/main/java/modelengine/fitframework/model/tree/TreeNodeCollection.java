/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.tree;

import java.util.stream.Stream;

/**
 * 为树中的节点提供集合。
 *
 * @author 梁济时
 * @since 2022-08-10
 */
public interface TreeNodeCollection extends Iterable<TreeNode> {
    /**
     * 获取集合的大小。
     *
     * @return 表示集合大小的32位整数。
     */
    int size();

    /**
     * 获取一个值，该值指示集合是否是空的。
     *
     * @return 若集合是空的，则为 {@code true}；否则为 {@code false}。
     */
    default boolean empty() {
        return this.size() < 1;
    }

    /**
     * 获取集合中指定索引处的节点。
     *
     * @param index 表示集合中节点的索引的32位整数。
     * @return 表示该索引处的节点的 {@link TreeNode}。
     * @throws IndexOutOfBoundsException 索引超出限制。
     */
    TreeNode get(int index);

    /**
     * 获取指定名称的节点。
     *
     * @param name 表示节点名称的 {@link String}。
     * @return 若存在该名称的节点，则为表示该节点的 {@link TreeNode}；否则为 {@code null}。
     */
    TreeNode get(String name);

    /**
     * 从集合中移除指定索引处的索引。
     *
     * @param index 表示待移除的节点的索引的32位整数。
     * @return 表示移除的节点的 {@link TreeNode}。
     * @throws IndexOutOfBoundsException 索引超出限制。
     */
    TreeNode remove(int index);

    /**
     * 从集合中移除指定名称的索引。
     *
     * @param name 表示节点的名称的 {@link String}。
     * @return 若存在该节点，则为表示移除的节点的 {@link TreeNode}；否则为 {@code null}。
     */
    TreeNode remove(String name);

    /**
     * 返回一个流，用以操作集合中的所有节点。
     *
     * @return 表示用以操作集合中节点的流的 {@link Stream}{@code <}{@link TreeNode}{@code >}。
     */
    Stream<TreeNode> stream();

    /**
     * 获取或创建指定名称的节点。
     *
     * @param name 表示节点的名称的 {@link String}。
     * @return 若已存在该名称的节点，则直接返回表示节点的 {@link TreeNode}；否则创建后返回新创建节点的 {@link TreeNode}。
     */
    TreeNode getOrCreate(String name);
}
