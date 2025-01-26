/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.data.repository.entity;

import java.util.Arrays;

/**
 * 表示元数据的类型。
 *
 * @author 邬涨财
 * @since 2024-01-23
 */
public enum MetadataType {
    STRING((byte) 1, "str"),
    BYTES((byte) 2, "bytes"),
    UNKNOWN(Byte.MAX_VALUE, "unknown");

    private final byte id;
    private final String code;

    MetadataType(byte id, String code) {
        this.id = id;
        this.code = code;
    }

    /**
     * 获取元数据的类型名。
     *
     * @return 表示获取到的元数据的类型名的 {@link String}。
     */
    public String code() {
        return this.code;
    }

    /**
     * 获取元数据的 ID。
     *
     * @return 表示获取到的元数据ID 的 {@code byte}。
     */
    public byte id() {
        return this.id;
    }

    /**
     * 获取指定 id 的元数据类型。
     *
     * @param id 表示通信类型编号的 {@link byte}。
     * @return 若存在该编号的通信类型，则为该类型的{@link MetadataType}.
     */
    public static MetadataType fromId(byte id) {
        return Arrays.stream(MetadataType.values()).filter(v -> v.id() == id).findAny().orElse(MetadataType.UNKNOWN);
    }
}
