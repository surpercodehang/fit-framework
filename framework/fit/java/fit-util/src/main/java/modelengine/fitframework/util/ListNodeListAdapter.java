/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * 为 {@link List} 提供 {@link NodeList} 的适配器。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
class ListNodeListAdapter implements NodeList {
    private final List<Node> nodes;

    /**
     * 使用待适配的列表初始化 {@link ListNodeListAdapter} 类的新实例。
     *
     * @param nodes 表示待适配的节点列表的 {@link List}。
     */
    ListNodeListAdapter(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Node item(int index) {
        return this.nodes.get(index);
    }

    @Override
    public int getLength() {
        return this.nodes.size();
    }

    @Override
    public String toString() {
        return this.nodes.toString();
    }
}
