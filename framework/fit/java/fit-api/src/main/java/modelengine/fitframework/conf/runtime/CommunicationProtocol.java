/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

import java.util.Objects;

/**
 * 表示传输协议的枚举。
 *
 * @author 季聿阶
 * @since 2023-06-27
 */
public enum CommunicationProtocol {
    /** 表示未知的传输协议。 */
    UNKNOWN(-1),
    /** 表示 rsocket 的传输协议。 */
    RSOCKET(0),
    /** 表示 http 的传输协议。 */
    HTTP(2),
    /** 表示 grpc 的传输协议。 */
    GRPC(3),
    /** 表示 https 的传输协议。 */
    HTTPS(4);

    private final int code;

    CommunicationProtocol(int code) {
        this.code = code;
    }

    /**
     * 获取传输协议的编码。
     *
     * @return 表示传输协议的编码的 {@code int}。
     */
    public int code() {
        return this.code;
    }

    /**
     * 将编码转换为对应的传输协议。
     * <p>当无法解析传输协议编码时，统一返回 {@link CommunicationProtocol#UNKNOWN}。</p>
     *
     * @param code 表示传输协议的编码的 {@code int}。
     * @return 表示转换后的传输协议的 {@link CommunicationProtocol}。
     */
    @Nonnull
    public static CommunicationProtocol from(int code) {
        for (CommunicationProtocol protocol : values()) {
            if (code == protocol.code()) {
                return protocol;
            }
        }
        return CommunicationProtocol.UNKNOWN;
    }

    /**
     * 将传输协议名转换为对应的传输协议。
     * <p>当无法解析传输协议编码时，统一返回 {@link CommunicationProtocol#UNKNOWN}。</p>
     *
     * @param name 表示传输协议名的 {@link String}。
     * @return 表示转换后的传输协议的 {@link CommunicationProtocol}。
     */
    @Nonnull
    public static CommunicationProtocol from(String name) {
        String upperCaseName = StringUtils.toUpperCase(name);
        for (CommunicationProtocol protocol : values()) {
            if (Objects.equals(upperCaseName, protocol.name())) {
                return protocol;
            }
        }
        return CommunicationProtocol.UNKNOWN;
    }
}
