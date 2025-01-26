/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

/**
 * 为小端数据提供工具方法。
 *
 * @author 梁济时
 * @since 2022-12-22
 */
final class Bytes {
    private static final int U2_LENGTH = 2;
    private static final int U4_LENGTH = 4;
    private static final int U8_LENGTH = 8;

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Bytes() {}

    private static int intValue(byte[] bytes, int offset, int length) {
        int value = 0;
        for (int i = offset + length - 1; i >= offset; i--) {
            value = value << 8 | Byte.toUnsignedInt(bytes[i]);
        }
        return value;
    }

    private static long longValue(byte[] bytes, int offset, int length) {
        long value = 0L;
        for (int i = offset + length - 1; i >= offset; i--) {
            value = value << 8 | Byte.toUnsignedLong(bytes[i]);
        }
        return value;
    }

    /**
     * 从字节序的指定位置读取一个 16 位无符号数。
     *
     * @param bytes 表示包含数值信息的字节序。
     * @param position 表示待读取数值所在字节序中位置的 32 位整数。
     * @return 表示读取到的 16 位无符号数。
     */
    static int u2(byte[] bytes, int position) {
        return intValue(bytes, position, U2_LENGTH);
    }

    /**
     * 从字节序的指定位置读取一个 32 位有符号数。
     *
     * @param bytes 表示包含数值信息的字节序。
     * @param position 表示待读取数值所在字节序中位置的 32 位整数。
     * @return 表示读取到的 32 位有符号数。
     */
    static int s4(byte[] bytes, int position) {
        return intValue(bytes, position, U4_LENGTH);
    }

    /**
     * 从字节序的指定位置读取一个 32 位无符号数。
     *
     * @param bytes 表示包含数值信息的字节序。
     * @param position 表示待读取数值所在字节序中位置的 32 位整数。
     * @return 表示读取到的 32 位无符号数。
     */
    static long u4(byte[] bytes, int position) {
        return longValue(bytes, position, U4_LENGTH);
    }

    /**
     * 从字节序的指定位置读取一个 64 位有符号数。
     *
     * @param bytes 表示包含数值信息的字节序。
     * @param position 表示待读取数值所在字节序中位置的 32 位整数。
     * @return 表示读取到的 64 位有符号数。
     */
    static long s8(byte[] bytes, int position) {
        return longValue(bytes, position, U8_LENGTH);
    }

    private static char hex(int value) {
        if (value < 10) {
            return (char) ('0' + value);
        } else {
            return (char) ('a' + value - 10);
        }
    }

    /**
     * 返回一个字符串，用以通过 16 进制形式表现指定字节序的内容。
     *
     * @param bytes 表示待转为 16 进制字符串表示的字节序。
     * @return 表示字节序的 16 进制字符串表现形式的 {@link String}。
     */
    static String hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length << 1);
        for (byte aByte : bytes) {
            int value = Byte.toUnsignedInt(aByte);
            builder.append(hex((value & 0xf0) >> 4));
            builder.append(hex(value & 0xf));
        }
        return builder.toString();
    }
}
