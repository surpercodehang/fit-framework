/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.support;

import static modelengine.fit.serialization.http.Constants.FIT_ASYNC_TASK_PATH_PATTERN;

import modelengine.fit.client.Request;
import modelengine.fit.http.protocol.Protocol;

/**
 * 表示 Http 链接的构建器。
 *
 * @author 季聿阶
 * @since 2023-09-10
 */
public class HttpConnectionBuilder extends AbstractConnectionBuilder {
    /**
     * 构建长轮询链接。
     *
     * @param request 表示请求的 {@link Request}。
     * @return 表示构建出来的长轮询链接的 {@link String}。
     */
    public String buildLongPollingUrl(Request request) {
        return this.buildBaseUrl(request).append(FIT_ASYNC_TASK_PATH_PATTERN).toString();
    }

    @Override
    public Protocol protocol() {
        return Protocol.HTTP;
    }
}
