/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.tree;

import java.util.function.Predicate;

/**
 * 为 {@link Tree} 提供节点定义。
 *
 * @author 梁济时
 * @since 2022-08-10
 */
public interface TreeNode {
    /**
     * 获取节点的名称。
     *
     * @return 表示节点名称的 {@link String}。
     */
    String name();

    /**
     * 获取节点的路径。
     *
     * @return 表示节点路径的 {@link String}。
     */
    String path();

    /**
     * 获取父节点。
     *
     * @return 表示父节点的 {@link TreeNode}。
     */
    TreeNode parent();

    /**
     * 获取子节点的集合。
     *
     * @return 表示子节点集合的 {@link TreeNodeCollection}。
     */
    TreeNodeCollection children();

    /**
     * 获取所在的树。
     *
     * @return 表示所在的树的 {@link Tree}。
     */
    Tree tree();

    /**
     * 获取节点所表示的数据。
     *
     * @return 表示节点表示的数据的 {@link Object}。
     */
    Object tag();

    /**
     * 设置节点所表示的数据。
     *
     * @param tag 表示节点表示的数据的 {@link Object}。
     */
    void tag(Object tag);

    /**
     * 从树中移除当前节点。
     */
    void remove();

    /**
     * 检查从当前节点开始到根节点结束，是否有节点满足指定条件。
     *
     * @param predicate 表示节点的判定条件的 {@link Predicate}{@code <}{@link TreeNode}{@code >}。
     * @return 若有节点满足条件，则为 {@code true}；否则为 {@code false}。
     */
    boolean anyInPath(Predicate<TreeNode> predicate);
}
