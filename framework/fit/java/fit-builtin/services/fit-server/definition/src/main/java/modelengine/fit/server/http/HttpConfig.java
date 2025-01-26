/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http;

import modelengine.fitframework.conf.runtime.ServerConfig;

/**
 * 表示运行时 {@code 'server.http.'} 前缀的配置项。
 *
 * @author 季聿阶
 * @since 2023-09-10
 */
public interface HttpConfig extends ServerConfig {
    /**
     * 获取巨大消息体的阈值。
     *
     * @return 表示巨大消息体的阈值的 {@code long}。
     */
    long largeBodySize();
}
