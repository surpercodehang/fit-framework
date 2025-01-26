/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.actuator.entity;

import lombok.Data;

/**
 * 表示连接端点的信息。
 *
 * @author 季聿阶
 * @since 2024-07-05
 */
@Data
public class EndpointVo {
    private String protocol;
    private int code;
    private int port;
}
