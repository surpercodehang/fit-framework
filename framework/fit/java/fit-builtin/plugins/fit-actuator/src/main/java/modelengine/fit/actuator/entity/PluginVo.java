/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.actuator.entity;

import lombok.Data;

/**
 * 表示插件信息。
 *
 * @author 季聿阶
 * @since 2024-07-05
 */
@Data
public class PluginVo {
    private String group;
    private String name;
    private String version;
    private String category;
    private int level;
}
