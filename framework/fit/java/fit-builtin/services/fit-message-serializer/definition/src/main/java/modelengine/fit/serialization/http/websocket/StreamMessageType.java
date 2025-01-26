/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.http.websocket;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示流式调用中通信消息体的类型。
 *
 * @author 何天放
 * @since 2024-04-15
 */
public enum StreamMessageType {
    /** 表示未知的消息体类型。 */
    UNKNOWN(-1),

    /** 表示数据消费下行消息。 */
    CONSUME(0),

    /** 表示正常终结信号下行消息。 */
    COMPLETE(1),

    /** 表示异常终结信号下行消息。 */
    FAIL(2),

    /** 表示请求元素上行消息。 */
    REQUEST_ELEMENT(10),

    /** 表示取消订阅上行消息。 */
    CANCEL(11),

    /** 表示客户端向服务端发起调用请求的消息。 */
    REQUEST(20),

    /** 表示服务端向客户端返回调用结果的消息。 */
    RESPONSE(21);

    private final int code;

    StreamMessageType(int code) {
        this.code = code;
    }

    /**
     * 获取信号类型的编码。
     *
     * @return 表示信号类型的编码的 {@code int}。
     */
    public int code() {
        return this.code;
    }

    /**
     * 将表示消息体类型的名字的 {@link String} 转换为对应的消息体类型枚举值。
     * <p>当无法解析消息体类型的名字时，统一返回 {@link StreamMessageType#UNKNOWN}。</p>
     *
     * @param name 表示消息体类型的名字的 {@link String}。
     * @return 表示转换后的消息体类型的 {@link StreamMessageType}。
     */
    @Nonnull
    public static StreamMessageType fromName(String name) {
        for (StreamMessageType type : values()) {
            if (StringUtils.equalsIgnoreCase(name, type.name())) {
                return type;
            }
        }
        return StreamMessageType.UNKNOWN;
    }

    /**
     * 将表示消息体类型的编码的 {@code int} 转换为对应的消息体类型枚举值。
     * <p>当无法解析消息体类型的编码时，统一返回 {@link StreamMessageType#UNKNOWN}。</p>
     *
     * @param code 表示消息体类型的编码的 {@code int}。
     * @return 表示转换后的消息体类型的 {@link StreamMessageType}。
     */
    @Nonnull
    public static StreamMessageType fromCode(int code) {
        for (StreamMessageType type : values()) {
            if (type.code() == code) {
                return type;
            }
        }
        return StreamMessageType.UNKNOWN;
    }
}
