/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import modelengine.fitframework.resource.support.DefaultResourcePath;
import modelengine.fitframework.util.wildcard.Pattern;

import java.util.LinkedList;
import java.util.List;

/**
 * 为资源提供路径。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
public interface ResourcePath {
    /**
     * 获取资源路径的第一个节点。
     *
     * @return 表示资源路径第一个节点的 {@link Node}。
     */
    Node head();

    /**
     * 获取一个值，该值指示资源路径是否为空。
     *
     * @return 若资源路径为空，则为 {@code true}；否则为 {@code false}。
     */
    default boolean empty() {
        return this.head() == null;
    }

    /**
     * 获取路径中包含的所有节点标签。
     *
     * @return 表示节点标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    default List<String> labels() {
        List<String> labels = new LinkedList<>();
        ResourcePath.Node node = this.head();
        while (node != null) {
            labels.add(node.label());
            node = node.next();
        }
        return labels;
    }

    /**
     * 将当前的资源路径转为一个用以匹配的样式。
     *
     * @return 表示当前资源路径的样式的 {@link Pattern}。
     */
    Pattern<String> asPattern();

    /**
     * 为资源路径提供节点。
     *
     * @author 梁济时
     * @since 2022-07-27
     */
    interface Node {
        /**
         * 获取路径节点的标签。
         *
         * @return 表示节点标签的 {@link String}。
         */
        String label();

        /**
         * 获取前一个节点。
         *
         * @return 若存在前一个节点，则为表示前一个节点的 {@link Node}；否则为 {@code null}。
         */
        Node previous();

        /**
         * 获取后一个节点。
         *
         * @return 若存在后一个节点，则为表示后一个节点的 {@link Node}；否则为 {@code null}。
         */
        Node next();
    }

    /**
     * 解析资源路径。
     *
     * @param path 表示资源路径的 {@link ResourcePath}。
     * @return 表示样式描述的资源路径的 {@link ResourcePath}。
     */
    static ResourcePath parse(String path) {
        return DefaultResourcePath.parse(path);
    }
}
