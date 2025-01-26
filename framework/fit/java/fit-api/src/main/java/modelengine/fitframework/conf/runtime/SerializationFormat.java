/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.runtime;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示序列化方式的枚举。
 *
 * @author 季聿阶
 * @since 2023-06-27
 */
public enum SerializationFormat {
    /** 表示未知的序列化方式。 */
    UNKNOWN(-1),
    /** 表示 protobuf 的序列化方式。 */
    PROTOBUF(0),
    /** 表示 json 的序列化方式。 */
    JSON(1),
    /** 表示 CBOR 的序列化方式。 */
    CBOR(2);

    private final int code;

    SerializationFormat(int code) {
        this.code = code;
    }

    /**
     * 获取序列化方式的编码。
     *
     * @return 表示序列化方式编码的 {@code int}。
     */
    public int code() {
        return this.code;
    }

    /**
     * 将编码转换为对应的序列化方式。
     * <p>当无法解析序列化方式编码时，统一返回 {@link SerializationFormat#UNKNOWN}。</p>
     *
     * @param code 表示序列化方式的编码的 {@code int}。
     * @return 表示转换后的序列化方式的 {@link SerializationFormat}。
     */
    @Nonnull
    public static SerializationFormat from(int code) {
        for (SerializationFormat format : values()) {
            if (code == format.code()) {
                return format;
            }
        }
        return SerializationFormat.UNKNOWN;
    }

    /**
     * 将序列化名字转换为对应的序列化方式。
     * <p>当无法解析序列化方式名字时，统一返回 {@link SerializationFormat#UNKNOWN}。</p>
     *
     * @param name 表示序列化方式的名字的 {@link String}。
     * @return 表示转换后的序列化方式的 {@link SerializationFormat}。
     */
    @Nonnull
    public static SerializationFormat from(String name) {
        for (SerializationFormat format : values()) {
            if (StringUtils.equalsIgnoreCase(name, format.name())) {
                return format;
            }
        }
        return SerializationFormat.UNKNOWN;
    }
}
