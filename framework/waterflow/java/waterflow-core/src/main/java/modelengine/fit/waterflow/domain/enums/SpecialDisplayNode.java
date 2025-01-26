/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.enums;

import lombok.Getter;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * 特殊节点的展示名称。
 *
 * @author 刘信宏
 * @since 2024-05-13
 */
@Getter
public enum SpecialDisplayNode {
    /**
     * 条件节点展示名称。
     */
    CONDITION("{?}"),

    /**
     * 条件分支节点展示名称。
     */
    BRANCH(StringUtils.EMPTY),

    /**
     * 条件结束节点展示名称
     */
    OTHERS("([+])"),

    /**
     * 汇聚节点展示名称。
     */
    JOIN("([+])"),

    /**
     * 平行节点展示名称。
     */
    PARALLEL("{{=}}");

    private final String displayName;

    SpecialDisplayNode(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 通过节点名称获取 {@link SpecialDisplayNode}。
     *
     * @param name 表示节点名称的 {@link String}。
     * @return 表示特殊节点展示名称的 {@link SpecialDisplayNode}。
     */
    public static Optional<SpecialDisplayNode> fromName(String name) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
