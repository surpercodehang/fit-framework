/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket;

import static modelengine.fitframework.inspection.Validation.between;
import static modelengine.fitframework.inspection.Validation.notBlank;

/**
 * 表示 WebSocket 关闭时的状态码和原因。
 *
 * @author 季聿阶
 * @since 2023-12-08
 */
public enum CloseReason {
    /**
     * 表示正常关闭，意味着建立连接的目的已经实现。
     */
    NORMAL_CLOSURE(1000, "Normal Closure"),

    /**
     * 表示端点“即将离开”，例如服务器关闭或浏览器离开了某个页面。
     */
    GOING_AWAY(1001, "Going Away"),

    /**
     * 表示端点由于协议错误而终止连接。
     */
    PROTOCOL_ERROR(1002, "Protocol Error"),

    /**
     * 表示端点由于收到了无法接受的数据类型而终止连接。
     * <p>例如，只能理解文本数据的端点可能会在收到二进制消息时发送此状态码。</p>
     */
    CANNOT_ACCEPT(1003, "Unsupported Frame"),

    /**
     * 保留。具体含义可能在未来定义。
     */
    RESERVED(1004, "Reserved"),

    /**
     * 是一个保留值，终端在关闭控制帧中不得设置为状态码。
     * <p>它用于应用程序期望状态码表明实际上没有状态码存在的情况。</p>
     */
    NO_STATUS_CODE(1005, "No Status"),

    /**
     * 是一个保留值，终端在关闭控制帧中不得设置为状态码。
     * <p>它用于应用程序期望状态码表明连接是异常关闭的，例如，没有发送或接收关闭控制帧。</p>
     */
    CLOSED_ABNORMALLY(1006, "Abnormal Closure"),

    /**
     * 表示端点因收到的消息中的数据与消息的类型不一致而终止连接。
     * <p>例如，在文本消息中包含非 UTF-8 数据。</p>
     */
    NOT_CONSISTENT(1007, "Unsupported Payload"),

    /**
     * 表示端点因收到违反其策略的消息而终止连接。
     * <p>这是一个通用状态码，可以在没有更适合的状态码（例如 {@link #CANNOT_ACCEPT 1003} 或 {@link #TOO_BIG
     * 1009}）或需要隐藏有关策略的具体细节时返回。</p>
     */
    VIOLATED_POLICY(1008, "Policy Violation"),

    /**
     * 表示端点因收到对其处理能力过大的消息而终止连接。
     */
    TOO_BIG(1009, "Message Too Big"),

    /**
     * 表示端点（客户端）因为期望服务器协商一个或多个扩展，但服务器在 WebSocket 握手的响应消息中没有返回它们而终止连接。
     * <p>需要的扩展列表应该出现在关闭帧的 reason 部分。</p>
     * <p>注意这个状态码不会被服务器使用，因为服务器可以在 WebSocket 握手失败时使用。</p>
     */
    NO_EXTENSION(1010, "Mandatory Extension"),

    /**
     * 表示服务器因遇到无法完成请求的意外情况而终止连接。
     */
    UNEXPECTED_CONDITION(1011, "Server Error"),

    /**
     * 表示服务将被重启。
     */
    SERVICE_RESTART(1012, "Service Restart"),

    /**
     * 表示服务正在经历过载。
     */
    TRY_AGAIN_LATER(1013, "Try Again Later"),

    /**
     * 表示由网关或代理服务器返回，表示下游服务器在尝试处理连接时遇到错误。
     * <p>它不是由终端直接使用的状态码，而是用于代理错误情况。</p>
     */
    BAD_GATEWAY(1014, "Bad Gateway"),

    /**
     * 是一个保留值，终端在关闭控制帧中不得设置为状态码。
     * <p>它用于应用程序期望状态码表明连接由于无法执行 TLS 握手（例如，无法验证服务器证书）而关闭的情况。</p>
     */
    TLS_HANDSHAKE_FAILURE(1015, "TLS Handshake Failed");

    private final int code;
    private final String reason;

    CloseReason(int code, String reason) {
        this.code = between(code, 1000, 1999, "The standard close code is out of range. [code={0}]", code);
        this.reason = notBlank(reason, "The standard close reason cannot be blank.");
    }

    /**
     * 获取关闭状态码。
     *
     * @return 表示关闭状态码的 {@code int}。
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取关闭原因。
     *
     * @return 表示关闭原因的 {@link String}。
     */
    public String getReason() {
        return this.reason;
    }
}
