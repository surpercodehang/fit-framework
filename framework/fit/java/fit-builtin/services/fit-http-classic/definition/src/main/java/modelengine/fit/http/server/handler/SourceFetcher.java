/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fit.http.protocol.MessageHeaderNames;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;

/**
 * 表示数据来源获取器。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public interface SourceFetcher {
    /**
     * 判断来源数据的常用格式是否是数组。
     * <p>例如：
     * <ul>
     *     <li>来自查询参数、消息头参数以及 {@link
     *     MessageHeaderNames#CONTENT_TYPE} 为
     *     {@link MimeType#APPLICATION_X_WWW_FORM_URLENCODED}
     *     的消息体参数都是默认为数组的。</li>
     *     <li>来自 {@link MessageHeaderNames#CONTENT_TYPE} 为
     *     {@link MimeType#APPLICATION_JSON}
     *     的消息体参数虽然 Json 中有可能是一个数组，但我们认为其常用格式不是数组。</li>
     * </ul>
     * </p>
     *
     * @return 如果可以，返回 {@code true}，否则，返回 {@code false}。
     */
    default boolean isArrayAble() {
        return false;
    }

    /**
     * 从 Http 请求和响应中获取数据。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicServerRequest}。
     * @param response 表示 Http 响应的 {@link HttpClassicServerResponse}。
     * @return 表示获取的数据的 {@link Object}。
     */
    Object get(HttpClassicServerRequest request, HttpClassicServerResponse response);
}
