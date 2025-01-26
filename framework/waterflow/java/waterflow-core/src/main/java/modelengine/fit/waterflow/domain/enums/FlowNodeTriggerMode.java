/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.enums;

import lombok.Getter;

/**
 * 流程节点触发类型
 *
 * @author 杨祥宇
 * @since 1.0
 */
@Getter
public enum FlowNodeTriggerMode {
    AUTO(true),
    MANUAL(false);

    private final boolean auto;

    FlowNodeTriggerMode(boolean auto) {
        this.auto = auto;
    }
}
