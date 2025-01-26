/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import java.util.zip.ZipEntry;

/**
 * 表示 JAR 中条目的压缩方法。
 *
 * @author 梁济时
 * @since 2022-09-16
 */
public enum CompressionMethod {
    /**
     * 表示 JAR 条目未被压缩。
     */
    NONE(ZipEntry.STORED, "Store"),

    /**
     * 表示使用 Deflate 算法进行压缩。
     */
    DEFLATED(ZipEntry.DEFLATED, "Deflate");

    private final Integer id;
    private final String code;

    CompressionMethod(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public int id() {
        return this.id;
    }

    public String code() {
        return this.code;
    }

    /**
     * 根据唯一标识获取对应的压缩方法。
     *
     * @param id 表示唯一标识的 {@code int}。
     * @return 表示压缩方法的 {@link CompressionMethod}。
     */
    public static CompressionMethod fromId(int id) {
        for (CompressionMethod method : values()) {
            if (method.id() == id) {
                return method;
            }
        }
        return null;
    }
}
