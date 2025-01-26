/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

/**
 * Http 常用消息头的值。
 *
 * @author 季聿阶
 * @since 2022-09-04
 */
public class MessageHeaderValues {
    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-3.6.1">RFC 2616</a> */
    public static final String CHUNKED = "chunked";

    /** 用于 {@link MessageHeaderNames#CONTENT_DISPOSITION}，表示附件需要下载。 */
    public static final String ATTACHMENT = "attachment";

    /** 用于 {@link MessageHeaderNames#CONTENT_DISPOSITION}，表示附件需要预览。 */
    public static final String INLINE = "inline";

    /** 用于 {@link MessageHeaderNames#CONNECTION}，表示后续 Http 协议需要升级。 */
    public static final String UPGRADE = "upgrade";

    /** 用于 {@link MessageHeaderNames#UPGRADE}，表示后续 Http 协议需要使用 WebSocket 协议。 */
    public static final String WEBSOCKET = "websocket";

    /** 用于 {@link MessageHeaderNames#CACHE_CONTROL}，表示后续 Http 协议不需要使用缓存。 */
    public static final String NO_CACHE = "no-cache";

    /** 用于 {@link MessageHeaderNames#CONNECTION}，表示后续 Http 协议需要保持连接。 */
    public static final String KEEP_ALIVE = "keep-alive";
}
