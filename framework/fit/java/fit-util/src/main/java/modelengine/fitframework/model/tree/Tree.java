/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.tree;

import modelengine.fitframework.model.tree.support.DefaultTrees;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * 表示树形结构定义。
 *
 * @author 梁济时
 * @since 2022-08-10
 */
public interface Tree {
    /**
     * 表示默认的路径分隔符。
     */
    char DEFAULT_PATH_SEPARATOR = '.';

    /**
     * 获取路径分隔符。
     *
     * @return 表示路径分隔符的字符。
     */
    char pathSeparator();

    /**
     * 获取树中根节点的集合。
     *
     * @return 表示根节点集合的 {@link TreeNodeCollection}。
     */
    TreeNodeCollection roots();

    /**
     * 获取指定路径的节点。
     *
     * @param path 表示节点的路径的 {@link String}。
     * @return 若存在该节点，则为表示节点的 {@link Optional}{@code <}{@link TreeNode}{@code >}；否则为 {@link Optional#empty()}。
     */
    TreeNode get(String path);

    /**
     * 获取或创建指定路径的节点。
     *
     * @param path 表示节点的路径的 {@link String}。
     * @return 表示该路径表示的节点的 {@link TreeNode}。
     * @throws IllegalArgumentException {@code path} 为 {@code null} 或包含为空字符串的节点名称。
     */
    TreeNode getOrCreate(String path);

    /**
     * 移除指定路径的节点。
     *
     * @param path 表示待移除的节点的路径的 {@link String}。
     * @return 若存在该节点，则为表示已移除的节点的 {@link Optional}{@code <}{@link TreeNode}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    TreeNode remove(String path);

    /**
     * 广度优先搜索。
     *
     * @param consumer 表示节点的消费程序的 {@link Consumer}{@code <}{@link TreeNode}{@code >}。
     */
    void bfs(Consumer<TreeNode> consumer);

    /**
     * 深度优先搜索。
     *
     * @param consumer 表示节点的消费程序的 {@link Consumer}{@code <}{@link TreeNode}{@code >}。
     */
    void dfs(Consumer<TreeNode> consumer);

    /**
     * 使用{@link #DEFAULT_PATH_SEPARATOR 默认的路径分隔符}创建树。
     *
     * @return 表示新创建的树的 {@link Tree}。
     */
    static Tree create() {
        return create(DEFAULT_PATH_SEPARATOR);
    }

    /**
     * 使用指定的路径分隔符创建树。
     *
     * @param pathSeparator 表示路径分隔符的字符。
     * @return 表示新创建的树的 {@link Tree}。
     */
    static Tree create(char pathSeparator) {
        return DefaultTrees.create(pathSeparator);
    }
}
