/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.actuator.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表示地址信息
 *
 * @author 季聿阶
 * @since 2024-07-05
 */
@Data
public class AddressVo {
    private String workerId;
    private String host;
    private String environment;
    private List<FormatVo> formats;
    private List<EndpointVo> endpoints;
    private Map<String, String> extensions;
}
