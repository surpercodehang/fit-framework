/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示 Http 响应的状态。
 *
 * @author 季聿阶
 * @since 2022-07-21
 */
public enum HttpResponseStatus {
    /**
     * 表示 100 Continue。
     */
    CONTINUE(100, "Continue"),

    /**
     * 表示 101 Switching Protocols。
     */
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),

    /**
     * 表示 102 Processing (WebDAV, RFC2518)。
     */
    PROCESSING(102, "Processing"),

    /**
     * 表示 200 OK。
     */
    OK(200, "OK"),

    /**
     * 表示 201 Created。
     */
    CREATED(201, "Created"),

    /**
     * 表示 202 Accepted。
     */
    ACCEPTED(202, "Accepted"),

    /**
     * 表示 203 Non-Authoritative Information (since HTTP/1.1)。
     */
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),

    /**
     * 表示 204 No Content。
     */
    NO_CONTENT(204, "No Content"),

    /**
     * 表示 205 Reset Content。
     */
    RESET_CONTENT(205, "Reset Content"),

    /**
     * 表示 206 Partial Content。
     */
    PARTIAL_CONTENT(206, "Partial Content"),

    /**
     * 表示 207 Multi-Status (WebDAV, RFC2518)。
     */
    MULTI_STATUS(207, "Multi-Status"),

    /**
     * 表示 300 Multiple Choices。
     */
    MULTIPLE_CHOICES(300, "Multiple Choices"),

    /**
     * 表示 301 Moved Permanently。
     */
    MOVED_PERMANENTLY(301, "Moved Permanently"),

    /**
     * 表示 302 Found。
     */
    FOUND(302, "Found"),

    /**
     * 表示 303 See Other (since HTTP/1.1)。
     */
    SEE_OTHER(303, "See Other"),

    /**
     * 表示 304 Not Modified。
     */
    NOT_MODIFIED(304, "Not Modified"),

    /**
     * 表示 305 Use Proxy (since HTTP/1.1)。
     */
    USE_PROXY(305, "Use Proxy"),

    /**
     * 表示 307 Temporary Redirect (since HTTP/1.1)。
     */
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),

    /**
     * 表示 308 Permanent Redirect (RFC7538)。
     */
    PERMANENT_REDIRECT(308, "Permanent Redirect"),

    /**
     * 表示 400 Bad Request。
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * 表示 401 Unauthorized。
     */
    UNAUTHORIZED(401, "Unauthorized"),

    /**
     * 表示 402 Payment Required。
     */
    PAYMENT_REQUIRED(402, "Payment Required"),

    /**
     * 表示 403 Forbidden。
     */
    FORBIDDEN(403, "Forbidden"),

    /**
     * 表示 404 Not Found。
     */
    NOT_FOUND(404, "Not Found"),

    /**
     * 表示 405 Method Not Allowed。
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    /**
     * 表示 406 Not Acceptable。
     */
    NOT_ACCEPTABLE(406, "Not Acceptable"),

    /**
     * 表示 407 Proxy Authentication Required。
     */
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),

    /**
     * 表示 408 Request Timeout。
     */
    REQUEST_TIMEOUT(408, "Request Timeout"),

    /**
     * 表示 409 Conflict。
     */
    CONFLICT(409, "Conflict"),

    /**
     * 表示 410 Gone。
     */
    GONE(410, "Gone"),

    /**
     * 表示 411 Length Required。
     */
    LENGTH_REQUIRED(411, "Length Required"),

    /**
     * 表示 412 Precondition Failed。
     */
    PRECONDITION_FAILED(412, "Precondition Failed"),

    /**
     * 表示 413 Request Entity Too Large。
     */
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),

    /**
     * 表示 414 Request-URI Too Long。
     */
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),

    /**
     * 表示 415 Unsupported Media Type。
     */
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

    /**
     * 表示 416 Requested Range Not Satisfiable。
     */
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),

    /**
     * 表示 417 Expectation Failed。
     */
    EXPECTATION_FAILED(417, "Expectation Failed"),

    /**
     * 表示 421 Misdirected Request。
     *
     * @see <a href="https://tools.ietf.org/html/rfc7540#section-9.1.2">421 (Misdirected Request) Status Code</a>
     */
    MISDIRECTED_REQUEST(421, "Misdirected Request"),

    /**
     * 表示 422 Unprocessable Entity (WebDAV, RFC4918)。
     */
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),

    /**
     * 表示 423 Locked (WebDAV, RFC4918)。
     */
    LOCKED(423, "Locked"),

    /**
     * 表示 424 Failed Dependency (WebDAV, RFC4918)。
     */
    FAILED_DEPENDENCY(424, "Failed Dependency"),

    /**
     * 表示 425 Unordered Collection (WebDAV, RFC3648)。
     */
    UNORDERED_COLLECTION(425, "Unordered Collection"),

    /**
     * 表示 426 Upgrade Required (RFC2817)。
     */
    UPGRADE_REQUIRED(426, "Upgrade Required"),

    /**
     * 表示 428 Precondition Required (RFC6585)。
     */
    PRECONDITION_REQUIRED(428, "Precondition Required"),

    /**
     * 表示 429 Too Many Requests (RFC6585)。
     */
    TOO_MANY_REQUESTS(429, "Too Many Requests"),

    /**
     * 表示 431 Request Header Fields Too Large (RFC6585)。
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),

    /**
     * 表示 500 Internal Server Error。
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    /**
     * 表示 501 Not Implemented。
     */
    NOT_IMPLEMENTED(501, "Not Implemented"),

    /**
     * 表示 502 Bad Gateway。
     */
    BAD_GATEWAY(502, "Bad Gateway"),

    /**
     * 表示 503 Service Unavailable。
     */
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),

    /**
     * 表示 504 Gateway Timeout。
     */
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),

    /**
     * 表示 505 HTTP Version Not Supported。
     */
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),

    /**
     * 表示 506 Variant Also Negotiates (RFC2295)。
     */
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),

    /**
     * 表示 507 Insufficient Storage (WebDAV, RFC4918)。
     */
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),

    /**
     * 表示 510 Not Extended (RFC2774)。
     */
    NOT_EXTENDED(510, "Not Extended"),

    /**
     * 表示 511 Network Authentication Required (RFC6585)。
     */
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");

    private static final Map<Integer, HttpResponseStatus> STATUSES = new HashMap<>();

    static {
        for (HttpResponseStatus status : HttpResponseStatus.values()) {
            STATUSES.put(status.statusCode, status);
        }
    }

    private final int statusCode;
    private final String reasonPhrase;

    HttpResponseStatus(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * 获取响应的状态码。
     *
     * @return 表示响应状态码的 {@code int}。
     */
    public int statusCode() {
        return this.statusCode;
    }

    /**
     * 获取响应的状态短语。
     *
     * @return 表示响应的状态短语的 {@link String}。
     */
    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    /**
     * 根据指定的响应状态码，获取响应的状态。
     *
     * @param statusCode 表示指定的响应状态码的 {@code int}。
     * @return 表示响应的状态的 {@link HttpResponseStatus}。
     */
    public static HttpResponseStatus from(int statusCode) {
        return STATUSES.get(statusCode);
    }
}
