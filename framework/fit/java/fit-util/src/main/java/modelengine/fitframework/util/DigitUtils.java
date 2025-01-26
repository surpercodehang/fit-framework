/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import java.util.Locale;

/**
 * 表示数字的工具类。
 *
 * @author 季聿阶
 * @since 2023-01-03
 */
public class DigitUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private DigitUtils() {}

    /**
     * 将指定的长整型转换成二进制进行输出，同时补足前置缺省的 {@code '0'}。
     *
     * @param value 表示指定的长整型的 {@code long}。
     * @return 表示二进制输出后的字符串的 {@link String}。
     */
    public static String toBinary(long value) {
        String out = Long.toBinaryString(value);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64 - out.length(); i++) {
            sb.append('0');
        }
        sb.append(out);
        return sb.toString();
    }

    /**
     * 将指定二进制数组转换成 16 进制进行显示。
     *
     * @param value 表示指定二进制数组的 {@code byte[]}。
     * @return 表示转换后的 16 进制字符串的 {@link String}。
     */
    public static String toHex(byte[] value) {
        StringBuilder hexString = new StringBuilder();
        for (byte by : value) {
            String hex = Integer.toHexString(0xFF & by);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase(Locale.ROOT);
    }
}
