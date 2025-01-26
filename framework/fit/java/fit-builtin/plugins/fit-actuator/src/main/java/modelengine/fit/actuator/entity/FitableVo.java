/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.actuator.entity;

import lombok.Data;

import java.util.Set;

/**
 * 表示服务实现信息。
 *
 * @author 季聿阶
 * @since 2024-07-05
 */
@Data
public class FitableVo {
    private String id;
    private String version;
    private Set<String> aliases;
    private Set<String> tags;
    private String degradation;
}
