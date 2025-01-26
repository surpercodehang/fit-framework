/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.cbor;

import static modelengine.fitframework.inspection.Validation.between;
import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.toJavaObject;

import modelengine.fitframework.util.Convert;
import modelengine.fitframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 表示 CBOR 的编码器。
 *
 * @author 季聿阶
 * @since 2024-01-25
 */
public class CborEncoder {
    /**
     * 将指定的数据进行编码。
     *
     * @param data 表示待编码的数据的 {@link Object}。
     * @return 表示编码后的二进制数组的 {@code byte[]}。
     * @throws IOException 当编码过程中发生错误时。
     */
    public byte[] encode(Object data) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            this.encode(data, out);
            return out.toByteArray();
        }
    }

    /**
     * 将指定的数据进行编码，编码后的二进制数组写入指定的输出字节流。
     *
     * @param data 表示待编码的数据的 {@link Object}。
     * @param out 表示待写入的指定输出字节流的 {@link OutputStream}。
     * @throws IOException 当编码过程中发生错误时。
     */
    public void encode(Object data, OutputStream out) throws IOException {
        Object actual = toJavaObject(data);
        if (actual == null) {
            this.encodeNull(out);
        } else if (actual instanceof Boolean) {
            this.encodeBoolean((boolean) actual, out);
        } else if (actual instanceof Number) {
            this.encodeNumber((Number) actual, out);
        } else if (actual instanceof byte[]) {
            this.encodeBytes((byte[]) actual, out);
        } else if (actual instanceof String) {
            this.encodeString((String) actual, out);
        } else if (actual instanceof List) {
            this.encodeList(cast(actual), out);
        } else if (actual instanceof Map) {
            this.encodeObject(cast(actual), out);
        } else {
            throw new IllegalArgumentException(StringUtils.format("Unsupported data type to encode by CBOR. [type={0}]",
                    actual.getClass().getName()));
        }
    }

    private void encodeNull(OutputStream out) throws IOException {
        out.write(CborConstant.NULL);
    }

    private void encodeBoolean(boolean data, OutputStream out) throws IOException {
        if (data) {
            out.write(CborConstant.TRUE);
        } else {
            out.write(CborConstant.FALSE);
        }
    }

    private void encodeNumber(Number data, OutputStream out) throws IOException {
        if (this.isInteger(data)) {
            this.encodeInteger(data.longValue(), out);
        } else if (data instanceof Double || data instanceof BigDecimal) {
            this.encodeDouble(data.doubleValue(), out);
        } else if (data instanceof Float) {
            this.encodeFloat((float) data, out);
        } else {
            throw new IllegalArgumentException(StringUtils.format("Unsupported number type. [type={0}]",
                    data.getClass().getName()));
        }
    }

    private boolean isInteger(Number number) {
        return number instanceof Integer || number instanceof Long || number instanceof Byte || number instanceof Short
                || number instanceof BigInteger;
    }

    private void encodeInteger(long data, OutputStream out) throws IOException {
        if (data >= 0) {
            this.encodeUnsignedInteger(data, out);
        } else {
            this.encodeNegativeInteger(data, out);
        }
    }

    private void encodeUnsignedInteger(long data, OutputStream out) throws IOException {
        out.write(this.encodeUnsignedInteger(data, 0));
    }

    private void encodeNegativeInteger(long data, OutputStream out) throws IOException {
        out.write(this.encodeUnsignedInteger(-data - 1, 1));
    }

    private void encodeDouble(double data, OutputStream out) throws IOException {
        out.write(CborConstant.DOUBLE);
        out.write(Convert.toBytes(data));
    }

    private void encodeFloat(float data, OutputStream out) throws IOException {
        out.write(CborConstant.FLOAT);
        out.write(Convert.toBytes(data));
    }

    private void encodeBytes(byte[] data, OutputStream out) throws IOException {
        int len = data.length;
        out.write(this.encodeUnsignedInteger(len, 2));
        out.write(data);
    }

    private void encodeString(String data, OutputStream out) throws IOException {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        int len = bytes.length;
        out.write(this.encodeUnsignedInteger(len, 3));
        out.write(bytes);
    }

    private void encodeList(List<Object> dataList, OutputStream out) throws IOException {
        int len = dataList.size();
        out.write(this.encodeUnsignedInteger(len, 4));
        for (Object data : dataList) {
            this.encode(data, out);
        }
    }

    private void encodeObject(Map<Object, Object> data, OutputStream out) throws IOException {
        int len = data.size();
        out.write(this.encodeUnsignedInteger(len, 5));
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            this.encode(entry.getKey(), out);
            this.encode(entry.getValue(), out);
        }
    }

    private byte[] encodeUnsignedInteger(long data, int type) {
        greaterThanOrEquals(data, 0, "The integer to encode must be unsigned. [integer={0}]", data);
        between(type, 0, 5, "The type is out of range. [type={0}]", type);
        if (data < 24) {
            return this.encodeUnsignedInteger(data, 0, this.firstByte((byte) data, type));
        } else if (data < 0xFF) {
            return this.encodeUnsignedInteger(data, 1, this.firstByte((byte) 0b000_11000, type));
        } else if (data < 0xFF_FF) {
            return this.encodeUnsignedInteger(data, 2, this.firstByte((byte) 0b000_11001, type));
        } else if (data < 0xFF_FF_FF_FFL) {
            return this.encodeUnsignedInteger(data, 4, this.firstByte((byte) 0b000_11010, type));
        } else {
            return this.encodeUnsignedInteger(data, 8, this.firstByte((byte) 0b000_11011, type));
        }
    }

    private byte firstByte(byte flag, int type) {
        return (byte) (flag | (type << 5));
    }

    private byte[] encodeUnsignedInteger(long data, int byteNum, byte first) {
        long value = data;
        byte[] bs = new byte[byteNum + 1];
        bs[0] = first;
        for (int i = byteNum; i > 0; i--) {
            byte cur = (byte) (value & 0xFF);
            bs[i] = cur;
            value = value >> 8;
        }
        return bs;
    }
}
