/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.support;

import modelengine.fit.http.protocol.Protocol;

/**
 * 表示 Https 链接的构建器。
 *
 * @author 季聿阶
 * @since 2023-09-10
 */
public class HttpsConnectionBuilder extends HttpConnectionBuilder {
    @Override
    public Protocol protocol() {
        return Protocol.HTTPS;
    }
}
