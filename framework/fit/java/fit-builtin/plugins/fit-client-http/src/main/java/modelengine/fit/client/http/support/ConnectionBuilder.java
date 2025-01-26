/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.support;

import modelengine.fit.client.Request;
import modelengine.fit.http.protocol.Protocol;

/**
 * 表示通信链接的构建器。
 *
 * @author 季聿阶
 * @since 2023-09-10
 */
public interface ConnectionBuilder {
    /**
     * 构建一个链接。
     *
     * @param request 表示请求的 {@link Request}。
     * @return 表示构建出来的链接的 {@link String}。
     */
    String buildUrl(Request request);

    /**
     * 获取构建器的类型。
     *
     * @return 表示构建器类型的 {@link Protocol}。
     */
    Protocol protocol();
}
