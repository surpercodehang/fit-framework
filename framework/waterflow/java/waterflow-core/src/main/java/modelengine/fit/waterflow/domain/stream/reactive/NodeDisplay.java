/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.stream.reactive;

import lombok.Getter;
import lombok.Setter;
import modelengine.fit.waterflow.domain.enums.SpecialDisplayNode;
import modelengine.fit.waterflow.domain.flow.Flow;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

/**
 * 节点展示信息。
 *
 * @author 刘信宏
 * @since 2024-05-11
 */
@Getter
public class NodeDisplay {
    private String name;

    @Setter
    private Flow<?> flow;

    @Setter
    private String nodeId;

    /**
     * 使用节点名称及关联的子流程信息初始化 {@link NodeDisplay}。
     *
     * @param name 表示节点名称的 {@link String}。
     * @param flow 表示节点关联子流程的 {@link Flow}{@code <?>}。
     * @param nodeId 表示节点关联子流程指定节点的 {@link String}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public NodeDisplay(String name, Flow<?> flow, String nodeId) {
        this.name = this.buildDisplayName(name);
        this.flow = flow;
        this.nodeId = nodeId;
    }

    /**
     * 设置节点展示名称。
     *
     * @param name 表示节点名称的 {@link String}。
     * @throws IllegalArgumentException 当 {@code name} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    public void setName(String name) {
        this.name = this.buildDisplayName(name);
    }

    private String buildDisplayName(String name) {
        Validation.notBlank(name, "Node name can not be blank.");
        return SpecialDisplayNode.fromName(name)
                .map(SpecialDisplayNode::getDisplayName)
                .orElseGet(() -> StringUtils.format("({0})", name));
    }
}
