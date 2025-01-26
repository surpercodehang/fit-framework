/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.util;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.serialization.ByteSerializer;
import modelengine.fitframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 为序列化与反序列化提供对变长数字的处理。
 * <p>数据中的每一个字节的最高位为 1，表示后续字节还有数据，最高位为 0，表示当前字节是最后一个数据字节。</p>
 * <p>1 个字节最多表示的数字范围是 {@code 0 - 127}，即最大 {@code 0x7F}。</p>
 * <p>2 个字节最多表示的数字范围是 {@code 128 - 16383}，即最大 {@code 0x3FFF}。</p>
 *
 * @author 梁济时
 * @since 2020-11-12
 */
public class VaryingNumber extends Number implements Comparable<VaryingNumber> {
    private static final int BITS_PER_BYTE = 7;
    private static final byte BYTE_DATA_MASK = 0x7F;
    private static final byte BYTE_MB_MASK = (byte) (~Byte.toUnsignedInt(BYTE_DATA_MASK));

    private final byte[] bytes;

    private VaryingNumber(byte[] bytes) {
        this.bytes = Validation.notNull(bytes, "Bytes is null.");
        Validation.greaterThanOrEquals(this.bytes.length, 1, "Bytes is not enough.");
    }

    /**
     * 获取一个序列化程序，用以对可变长数字进行序列化与反序列化处理。
     *
     * @return 表示可变长数字的序列化程序的 {@link ByteSerializer}。
     */
    public static ByteSerializer<VaryingNumber> serializer() {
        return Serializer.INSTANCE;
    }

    /**
     * 获取可变长数字中包含的数据。
     *
     * @return 表示可变长数字的数据的 {@code byte[]}。
     */
    public byte[] bytes() {
        return this.bytes;
    }

    @Override
    public byte byteValue() {
        Validation.isTrue(this.isBitNumValid(1), "Data truncation for byte. [bytes length={0}]", this.bytes.length);
        return (byte) this.parseLong();
    }

    @Override
    public short shortValue() {
        Validation.isTrue(this.isBitNumValid(2), "Data truncation for short. [bytes length={0}]", this.bytes.length);
        return (short) this.parseLong();
    }

    @Override
    public int intValue() {
        Validation.isTrue(this.isBitNumValid(4), "Data truncation for int. [bytes length={0}]", this.bytes.length);
        return (int) this.parseLong();
    }

    @Override
    public long longValue() {
        Validation.isTrue(this.isBitNumValid(8), "Data truncation for long. [bytes length={0}]", this.bytes.length);
        return this.parseLong();
    }

    private long parseLong() {
        long value = 0L;
        int index = 0;
        while (index < this.bytes.length) {
            byte item = this.bytes[index];
            value = value << BITS_PER_BYTE;
            value |= (Byte.toUnsignedInt(item) & BYTE_DATA_MASK);
            index++;
        }
        return value;
    }

    @Override
    public float floatValue() {
        Validation.isTrue(this.isBitNumValid(4), "Data truncation for float. [bytes length={0}]", this.bytes.length);
        return (float) this.parseLong();
    }

    @Override
    public double doubleValue() {
        Validation.isTrue(this.isBitNumValid(8), "Data truncation for double. [bytes length={0}]", this.bytes.length);
        return (double) this.parseLong();
    }

    private boolean isBitNumValid(int byteNum) {
        byte firstByte = this.bytes[0];
        int actualValidBitNum = this.calValidBits(firstByte) + (this.bytes.length - 1) * BITS_PER_BYTE;
        return actualValidBitNum <= byteNum * 8;
    }

    private int calValidBits(byte value) {
        int actualByte = value & BYTE_DATA_MASK;
        int validNum = 0;
        while (actualByte != 0) {
            validNum++;
            actualByte = actualByte >> 1;
        }
        return validNum;
    }

    private static boolean hasMoreBytes(byte value) {
        return !noMoreBytes(value);
    }

    private static boolean noMoreBytes(byte value) {
        return (value & BYTE_MB_MASK) == 0;
    }

    private static int measure(int bits) {
        int length = bits / BITS_PER_BYTE;
        if (length % BITS_PER_BYTE > 0) {
            length++;
        }
        return length;
    }

    /**
     * 通过可变长数字的数据，创建一个可变长数字的新实例。
     *
     * @param bytes 表示包含可变长数字的数据的 {@code byte[]}。
     * @return 表示可变长数字的 {@link VaryingNumber}。
     * @throws IllegalVaryingNumberException 数据中未包含有效的可变长数字。
     */
    public static VaryingNumber valueOf(byte[] bytes) {
        if (bytes == null || bytes.length < 1) {
            throw new IllegalVaryingNumberException("The bytes of a varying number cannot be null or empty.");
        }
        for (int i = 0; i < bytes.length - 1; i++) {
            if (noMoreBytes(bytes[i])) {
                throw new IllegalVaryingNumberException(StringUtils.format(
                        "The bytes of varying number ends at {0} but there are {1} bytes totally.",
                        i,
                        bytes.length));
            }
        }
        if (hasMoreBytes(bytes[bytes.length - 1])) {
            throw new IllegalVaryingNumberException("The bytes of varying number is incomplete.");
        }
        return new VaryingNumber(bytes);
    }

    /**
     * 为指定的字节信息创建可变长数字。
     *
     * @param value 表示字节信息的 {@code byte}。
     * @return 表示可变长数字的 {@link VaryingNumber}。
     */
    public static VaryingNumber valueOf(byte value) {
        return new VaryingNumber(parse(new Int8ByteIterator(value)));
    }

    /**
     * 为指定的16位证书创建可变长数字。
     *
     * @param value 表示可变长数字的内容的16位整数。
     * @return 表示可变长数字的 {@link VaryingNumber}。
     */
    public static VaryingNumber valueOf(short value) {
        return new VaryingNumber(parse(new Int16ByteIterator(value)));
    }

    /**
     * 为指定的32位整数创建可变长数字。
     *
     * @param value 表示可变长数字的内容的32位整数。
     * @return 表示可变长数字的 {@link VaryingNumber}。
     */
    public static VaryingNumber valueOf(int value) {
        return new VaryingNumber(parse(new Int32ByteIterator(value)));
    }

    /**
     * 为指定的64位整数创建可变长数字。
     *
     * @param value 表示可变长数字的内容的64位整数。
     * @return 表示可变长数字的 {@link VaryingNumber}。
     */
    public static VaryingNumber valueOf(long value) {
        return new VaryingNumber(parse(new Int64ByteIterator(value)));
    }

    private static byte[] parse(ByteIterator iterator) {
        if (!iterator.hasNext()) {
            return new byte[] {0};
        }
        int length = measure(iterator.bits());
        byte[] tempBytes = new byte[length];
        int index = tempBytes.length - 1;
        tempBytes[index--] = iterator.next();
        while (iterator.hasNext()) {
            tempBytes[index--] = (byte) (Byte.toUnsignedInt(iterator.next()) | BYTE_MB_MASK);
        }
        int startIndex = Math.min(index + 1, tempBytes.length - 1);
        byte[] actualBytes = new byte[tempBytes.length - startIndex];
        System.arraycopy(tempBytes, startIndex, actualBytes, 0, actualBytes.length);
        return actualBytes;
    }

    @Override
    public int compareTo(VaryingNumber another) {
        int result = Integer.compare(this.bytes.length, another.bytes.length);
        if (result == 0) {
            for (int i = 0; i < this.bytes.length && result == 0; i++) {
                result = Byte.compare(this.bytes[i], another.bytes[i]);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VaryingNumber) {
            VaryingNumber another = (VaryingNumber) obj;
            return this.compareTo(another) == 0;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.bytes.length << 1);
        for (byte aByte : this.bytes) {
            String text = Integer.toHexString(Byte.toUnsignedInt(aByte));
            if (text.length() == 1) {
                builder.append('0');
            }
            builder.append(text);
        }
        return builder.toString();
    }

    private interface ByteIterator {
        /**
         * 获取有效数据的总位数。
         *
         * @return 表示有效数据总位数的32位整数。
         */
        int bits();

        /**
         * 获取一个值，该值指示是否还包含其他有效数据。
         *
         * @return 若还包含有效数据，则为 {@code true}；否则为 {@code false}。
         */
        boolean hasNext();

        /**
         * 获取下一个有效数据的字节信息。
         * <p>字节信息中仅 {@link VaryingNumber#BITS_PER_BYTE} 位是有效的。</p>
         *
         * @return 表示有效数据的字节信息。
         */
        byte next();
    }

    private static class Int8ByteIterator implements ByteIterator {
        private byte value;

        private Int8ByteIterator(byte value) {
            this.value = value;
        }

        @Override
        public int bits() {
            return 8;
        }

        @Override
        public boolean hasNext() {
            return this.value != 0;
        }

        @Override
        public byte next() {
            byte next = (byte) (this.value & BYTE_DATA_MASK);
            this.value = (byte) (Byte.toUnsignedInt(this.value) >>> BITS_PER_BYTE);
            return next;
        }
    }

    private static class Int16ByteIterator implements ByteIterator {
        private short value;

        private Int16ByteIterator(short value) {
            this.value = value;
        }

        @Override
        public int bits() {
            return 16;
        }

        @Override
        public boolean hasNext() {
            return this.value != 0;
        }

        @Override
        public byte next() {
            byte next = (byte) (this.value & BYTE_DATA_MASK);
            this.value = (short) (Short.toUnsignedInt(this.value) >>> BITS_PER_BYTE);
            return next;
        }
    }

    private static class Int32ByteIterator implements ByteIterator {
        private int value;

        private Int32ByteIterator(int value) {
            this.value = value;
        }

        @Override
        public int bits() {
            return 32;
        }

        @Override
        public boolean hasNext() {
            return this.value != 0;
        }

        @Override
        public byte next() {
            byte next = (byte) (this.value & BYTE_DATA_MASK);
            this.value >>>= BITS_PER_BYTE;
            return next;
        }
    }

    private static class Int64ByteIterator implements ByteIterator {
        private long value;

        private Int64ByteIterator(long value) {
            this.value = value;
        }

        @Override
        public int bits() {
            return 64;
        }

        @Override
        public boolean hasNext() {
            return this.value != 0L;
        }

        @Override
        public byte next() {
            byte next = (byte) (this.value & BYTE_DATA_MASK);
            this.value >>>= BITS_PER_BYTE;
            return next;
        }
    }

    private static class Serializer implements ByteSerializer<VaryingNumber> {
        private static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(VaryingNumber num, OutputStream out) throws IOException {
            out.write(num.bytes());
        }

        @Override
        public VaryingNumber deserialize(InputStream in) throws IOException {
            byte[] actual;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                int value;
                while ((value = in.read()) >= 0) {
                    out.write(value);
                    if (noMoreBytes((byte) value)) {
                        break;
                    }
                }
                actual = out.toByteArray();
            }
            if (actual.length > 0) {
                return VaryingNumber.valueOf(actual);
            }
            return null;
        }
    }
}
