/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fit.http.annotation.DocumentIgnored;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.annotation.Component;

/**
 * 表示全局默认的 {@link HttpRequestMethod#OPTIONS} 处理方法。
 *
 * @author 季聿阶
 * @since 2023-07-21
 */
@Component
public class OptionsHttpHandler {
    /**
     * 处理 Option 请求。
     */
    @DocumentIgnored
    @RequestMapping(method = HttpRequestMethod.OPTIONS, path = "/**")
    public void handleOptions() {}
}
