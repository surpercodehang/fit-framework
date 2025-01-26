/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

/**
 * Http 常用消息头名字。
 * <p><a href="https://datatracker.ietf.org/doc/html/rfc2076">RFC 2076</a> 列出了很多通用的消息头的定义来源。</p>
 *
 * @author 季聿阶
 * @since 2022-07-14
 */
public class MessageHeaderNames {
    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.1">RFC 2616</a> */
    public static final String ACCEPT = "Accept";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.2">RFC 2616</a> */
    public static final String ACCEPT_CHARSET = "Accept-Charset";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.3">RFC 2616</a> */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.4">RFC 2616</a> */
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.5">RFC 2616</a> */
    public static final String ACCEPT_RANGES = "Accept-Ranges";

    /** @see <a href="https://www.rfc-editor.org/rfc/rfc5789#section-3.1">RFC 5789</a> */
    public static final String ACCEPT_PATCH = "Accept-Patch";

    /**
     * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Credentials">MDN
     * Web Docs</a>
     */
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    /**
     * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Headers">MDN Web
     * Docs</a>
     */
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    /**
     * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Methods">MDN Web
     * Docs</a>
     */
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    /**
     * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Origin">MDN Web
     * Docs</a>
     */
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    /**
     * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Expose-Headers">MDN
     * Web Docs</a>
     */
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    /**
     * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Max-Age">MDN Web Docs</a>
     */
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    /**
     * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Request-Headers">MDN
     * Web Docs</a>
     */
    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";

    /**
     * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Request-Method">MDN
     * Web Docs</a>
     */
    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.6">RFC 2616</a> */
    public static final String AGE = "Age";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.7">RFC 2616</a> */
    public static final String ALLOW = "Allow";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.8">RFC 2616</a> */
    public static final String AUTHORIZATION = "Authorization";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.9">RFC 2616</a> */
    public static final String CACHE_CONTROL = "Cache-Control";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.10">RFC 2616</a> */
    public static final String CONNECTION = "Connection";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2183#section-2">RFC 2183</a> */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.11">RFC 2616</a> */
    public static final String CONTENT_ENCODING = "Content-Encoding";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.12">RFC 2616</a> */
    public static final String CONTENT_LANGUAGE = "Content-Language";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.13">RFC 2616</a> */
    public static final String CONTENT_LENGTH = "Content-Length";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.14">RFC 2616</a> */
    public static final String CONTENT_LOCATION = "Content-Location";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc1521#section-5">RFC 1521</a> */
    public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.15">RFC 2616</a> */
    public static final String CONTENT_MD5 = "Content-MD5";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.16">RFC 2616</a> */
    public static final String CONTENT_RANGE = "Content-Range";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.17">RFC 2616</a> */
    public static final String CONTENT_TYPE = "Content-Type";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc6265#section-4.2">RFC 6265</a> */
    public static final String COOKIE = "Cookie";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.18">RFC 2616</a> */
    public static final String DATE = "Date";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.19">RFC 2616</a> */
    public static final String ETAG = "ETag";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.20">RFC 2616</a> */
    public static final String EXPECT = "Expect";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.21">RFC 2616</a> */
    public static final String EXPIRES = "Expires";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.22">RFC 2616</a> */
    public static final String FROM = "From";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.23">RFC 2616</a> */
    public static final String HOST = "Host";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.24">RFC 2616</a> */
    public static final String IF_MATCH = "If-Match";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.25">RFC 2616</a> */
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.26">RFC 2616</a> */
    public static final String IF_NONE_MATCH = "If-None-Match";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.27">RFC 2616</a> */
    public static final String IF_RANGE = "If-Range";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.28">RFC 2616</a> */
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.29">RFC 2616</a> */
    public static final String LAST_MODIFIED = "Last-Modified";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.30">RFC 2616</a> */
    public static final String LOCATION = "Location";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.31">RFC 2616</a> */
    public static final String MAX_FORWARDS = "Max-Forwards";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc6454#section-7">RFC 6454</a> */
    public static final String ORIGIN = "Origin";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.32">RFC 2616</a> */
    public static final String PRAGMA = "Pragma";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.33">RFC 2616</a> */
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.34">RFC 2616</a> */
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.35">RFC 2616</a> */
    public static final String RANGE = "Range";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.36">RFC 2616</a> */
    public static final String REFERER = "Referer";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.37">RFC 2616</a> */
    public static final String RETRY_AFTER = "Retry-After";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc6455#section-11.3.3">RFC 6455</a> */
    public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc6455#section-11.3.1">RFC 6455</a> */
    public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc6455#section-11.3.4">RFC 6455</a> */
    public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc6455#section-11.3.5">RFC 6455</a> */
    public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.38">RFC 2616</a> */
    public static final String SERVER = "Server";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc6265#section-4.1">RFC 6265</a> */
    public static final String SET_COOKIE = "Set-Cookie";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.39">RFC 2616</a> */
    public static final String TE = "TE";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.40">RFC 2616</a> */
    public static final String TRAILER = "Trailer";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.41">RFC 2616</a> */
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.42">RFC 2616</a> */
    public static final String UPGRADE = "Upgrade";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.43">RFC 2616</a> */
    public static final String USER_AGENT = "User-Agent";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.44">RFC 2616</a> */
    public static final String VARY = "Vary";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.45">RFC 2616</a> */
    public static final String VIA = "Via";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.46">RFC 2616</a> */
    public static final String WARNING = "Warning";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-14.47">RFC 2616</a> */
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    /** @see <a href="https://datatracker.ietf.org/doc/html/rfc7034#section-2">RFC 7034</a> */
    public static final String X_FRAME_OPTIONS = "X-Frame-Options";

    /**
     * 表示 Http 方法应该更新为新的值。
     */
    public static final String X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";
}
