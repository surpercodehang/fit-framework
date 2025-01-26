/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import modelengine.fit.http.protocol.support.DefaultHttpResponse;

/**
 * 表示 Http 的响应。
 *
 * @author 季聿阶
 * @since 2023-11-28
 */
public interface HttpResponse {
    /**
     * 获取 Http 响应的状态。
     *
     * @return 表示 Http 响应的状态的 {@link HttpResponseStatus}。
     */
    HttpResponseStatus status();

    /**
     * 获取 Http 响应的消息体内容。
     *
     * @return 表示 Http 响应的消息体内容的 {@link Object}。
     */
    Object entity();

    /**
     * 创建 Http 的响应。
     *
     * @param status 表示 Http 响应的状态的 {@link HttpResponseStatus}。
     * @param entity 表示 Http 响应消息体内容的 {@link Object}。
     * @return 表示 Http 的响应的 {@link HttpResponse}。
     */
    static HttpResponse create(HttpResponseStatus status, Object entity) {
        return new DefaultHttpResponse(status, entity);
    }

    /**
     * 创建 Http 状态为 {@link HttpResponseStatus#OK} 的响应。
     *
     * @param entity 表示 Http 响应消息体内容的 {@link Object}。
     * @return 表示 Http 状态为 {@link HttpResponseStatus#OK} 的响应的 {@link HttpResponse}。
     */
    static HttpResponse ok(Object entity) {
        return new DefaultHttpResponse(HttpResponseStatus.OK, entity);
    }

    /**
     * 创建 Http 状态为 {@link HttpResponseStatus#CREATED} 的响应。
     *
     * @param entity 表示 Http 响应消息体内容的 {@link Object}。
     * @return 表示 Http 状态为 {@link HttpResponseStatus#CREATED} 的响应的 {@link HttpResponse}。
     */
    static HttpResponse created(Object entity) {
        return new DefaultHttpResponse(HttpResponseStatus.CREATED, entity);
    }

    /**
     * 创建 Http 状态为 {@link HttpResponseStatus#ACCEPTED} 的响应。
     *
     * @param entity 表示 Http 响应消息体内容的 {@link Object}。
     * @return 表示 Http 状态为 {@link HttpResponseStatus#ACCEPTED} 的响应的 {@link HttpResponse}。
     */
    static HttpResponse accepted(Object entity) {
        return new DefaultHttpResponse(HttpResponseStatus.ACCEPTED, entity);
    }

    /**
     * 创建 Http 状态为 {@link HttpResponseStatus#NO_CONTENT} 的响应。
     *
     * @return 表示 Http 状态为 {@link HttpResponseStatus#NO_CONTENT} 的响应的 {@link HttpResponse}。
     */
    static HttpResponse noContent() {
        return new DefaultHttpResponse(HttpResponseStatus.NO_CONTENT, null);
    }

    /**
     * 创建 Http 状态为 {@link HttpResponseStatus#BAD_REQUEST} 的响应。
     *
     * @param entity 表示 Http 响应消息体内容的 {@link Object}。
     * @return 表示 Http 状态为 {@link HttpResponseStatus#BAD_REQUEST} 的响应的 {@link HttpResponse}。
     */
    static HttpResponse badRequest(Object entity) {
        return new DefaultHttpResponse(HttpResponseStatus.BAD_REQUEST, entity);
    }

    /**
     * 创建 Http 状态为 {@link HttpResponseStatus#UNAUTHORIZED} 的响应。
     *
     * @param entity 表示 Http 响应消息体内容的 {@link Object}。
     * @return 表示 Http 状态为 {@link HttpResponseStatus#UNAUTHORIZED} 的响应的 {@link HttpResponse}。
     */
    static HttpResponse unauthorized(Object entity) {
        return new DefaultHttpResponse(HttpResponseStatus.UNAUTHORIZED, entity);
    }

    /**
     * 创建 Http 状态为 {@link HttpResponseStatus#FORBIDDEN} 的响应。
     *
     * @param entity 表示 Http 响应消息体内容的 {@link Object}。
     * @return 表示 Http 状态为 {@link HttpResponseStatus#FORBIDDEN} 的响应的 {@link HttpResponse}。
     */
    static HttpResponse forbidden(Object entity) {
        return new DefaultHttpResponse(HttpResponseStatus.FORBIDDEN, entity);
    }

    /**
     * 创建 Http 状态为 {@link HttpResponseStatus#NOT_FOUND} 的响应。
     *
     * @param entity 表示 Http 响应消息体内容的 {@link Object}。
     * @return 表示 Http 状态为 {@link HttpResponseStatus#NOT_FOUND} 的响应的 {@link HttpResponse}。
     */
    static HttpResponse notFound(Object entity) {
        return new DefaultHttpResponse(HttpResponseStatus.NOT_FOUND, entity);
    }

    /**
     * 创建 Http 状态为 {@link HttpResponseStatus#INTERNAL_SERVER_ERROR} 的响应。
     *
     * @param entity 表示 Http 响应消息体内容的 {@link Object}。
     * @return 表示 Http 状态为 {@link HttpResponseStatus#INTERNAL_SERVER_ERROR} 的响应的 {@link HttpResponse}。
     */
    static HttpResponse internalServerError(Object entity) {
        return new DefaultHttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, entity);
    }
}
