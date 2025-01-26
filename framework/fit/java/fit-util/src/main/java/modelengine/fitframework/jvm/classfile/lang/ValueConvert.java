/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.lang;

/**
 * 为值转换提供工具方法。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
final class ValueConvert {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ValueConvert() {}

    static byte byteValue(short value) {
        if (value < 0 || value > 0xff) {
            throw new ValueOverflowException(Integer.toString(Short.toUnsignedInt(value)),
                    Integer.toString(0),
                    Integer.toString(0xff));
        } else {
            return (byte) value;
        }
    }

    static byte byteValue(int value) {
        if (value < 0 || value > 0xff) {
            throw new ValueOverflowException(Integer.toUnsignedString(value),
                    Integer.toString(0),
                    Integer.toString(0xff));
        } else {
            return (byte) value;
        }
    }

    static byte byteValue(long value) {
        if (value < 0 || value > 0xff) {
            throw new ValueOverflowException(Long.toUnsignedString(value), Integer.toString(0), Integer.toString(0xff));
        } else {
            return (byte) value;
        }
    }

    static short shortValue(byte value) {
        return (short) Byte.toUnsignedInt(value);
    }

    static short shortValue(int value) {
        if (value < 0 || value > 0xffff) {
            throw new ValueOverflowException(Integer.toUnsignedString(value),
                    Integer.toString(0),
                    Integer.toString(0xffff));
        } else {
            return (short) value;
        }
    }

    static short shortValue(long value) {
        if (value < 0 || value > 0xffff) {
            throw new ValueOverflowException(Long.toUnsignedString(value),
                    Integer.toString(0),
                    Integer.toString(0xffff));
        } else {
            return (short) value;
        }
    }

    static int intValue(byte value) {
        return Byte.toUnsignedInt(value);
    }

    static int intValue(short value) {
        return Short.toUnsignedInt(value);
    }

    static int intValue(long value) {
        if (value < 0 || value > 0xffffffffL) {
            throw new ValueOverflowException(Long.toUnsignedString(value),
                    Integer.toString(0),
                    Long.toString(0xffffffffL));
        } else {
            return (int) value;
        }
    }

    static long longValue(byte value) {
        return Byte.toUnsignedLong(value);
    }

    static long longValue(short value) {
        return Short.toUnsignedLong(value);
    }

    static long longValue(int value) {
        return Integer.toUnsignedLong(value);
    }
}
