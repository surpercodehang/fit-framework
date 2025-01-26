/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.lang;

import modelengine.fitframework.util.Convert;
import modelengine.fitframework.util.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 为JVM提供1字节的数据。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class U1 implements Comparable<U1> {
    private final byte value;

    private U1(byte value) {
        this.value = value;
    }

    /**
     * 获取字节数据表现形式。
     *
     * @return 表示数据的字节表现形式。
     */
    public byte byteValue() {
        return this.value;
    }

    /**
     * 获取16位整数数据表现形式。
     *
     * @return 表示数据的16位整数表现形式。
     */
    public short shortValue() {
        return ValueConvert.shortValue(this.value);
    }

    /**
     * 获取32位整数数据表现形式。
     *
     * @return 表示数据的32位整数表现形式。
     */
    public int intValue() {
        return ValueConvert.intValue(this.value);
    }

    /**
     * 获取64位整数数据表现形式。
     *
     * @return 表示数据的64位整数表现形式。
     */
    public long longValue() {
        return ValueConvert.longValue(this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof U1) {
            U1 another = (U1) obj;
            return another.value == this.value;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {U1.class, this.value});
    }

    @Override
    public String toString() {
        return Integer.toString(this.intValue());
    }

    /**
     * 返回一个16进制字符串，用以表示当前的数据。
     *
     * @return 表示当前数据的16进制字符串的 {@link String}。
     */
    public String toHexString() {
        return String.format("%02x", this.value);
    }

    @Override
    public int compareTo(U1 another) {
        return Integer.compare(this.intValue(), another.intValue());
    }

    /**
     * 使用字节数据创建实例。
     *
     * @param value 表示包含数据的字节。
     * @return 表示1字节数据的 {@link U1}。
     */
    public static U1 of(byte value) {
        return new U1(value);
    }

    /**
     * 使用16位整数数据创建实例。
     *
     * @param value 表示包含数据的16位整数。
     * @return 表示1字节数据的 {@link U1}。
     * @throws ValueOverflowException 数据超出表示范围。
     */
    public static U1 of(short value) {
        return new U1(ValueConvert.byteValue(value));
    }

    /**
     * 使用32位整数数据创建实例。
     *
     * @param value 表示包含数据的32位整数。
     * @return 表示1字节数据的 {@link U1}。
     * @throws ValueOverflowException 数据超出表示范围。
     */
    public static U1 of(int value) {
        return new U1(ValueConvert.byteValue(value));
    }

    /**
     * 使用64位整数数据创建实例。
     *
     * @param value 表示包含数据的64位整数。
     * @return 表示1字节数据的 {@link U1}。
     * @throws ValueOverflowException 数据超出表示范围。
     */
    public static U1 of(long value) {
        return new U1(ValueConvert.byteValue(value));
    }

    /**
     * 从输入流中读取1字节数据。
     *
     * @param in 表示包含数据的输入流的 {@link InputStream}。
     * @return 表示1字节数据的 {@link U1}。
     * @throws IOException 读取数据过程发生输入输出异常。
     */
    public static U1 read(InputStream in) throws IOException {
        return of(Convert.toByte(IoUtils.read(in, 1)));
    }

    /**
     * 将数据写入到输出流中。
     *
     * @param out 表示待将数据写入到的输出流的 {@link OutputStream}。
     * @throws IOException 写入数据过程发生输入输出异常。
     */
    public void write(OutputStream out) throws IOException {
        out.write(Convert.toBytes(this.value));
    }
}
