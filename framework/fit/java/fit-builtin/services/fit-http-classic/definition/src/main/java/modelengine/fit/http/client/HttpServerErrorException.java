/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client;

/**
 * 表示 Http 响应的状态码为 5xx 的异常。
 *
 * @author 季聿阶
 * @since 2023-01-29
 */
public class HttpServerErrorException extends HttpClientResponseException {
    public HttpServerErrorException(HttpClassicClientResponse<?> response) {
        super(response);
    }

    public HttpServerErrorException(HttpClassicClientResponse<?> response, Throwable cause) {
        super(response, cause);
    }
}
