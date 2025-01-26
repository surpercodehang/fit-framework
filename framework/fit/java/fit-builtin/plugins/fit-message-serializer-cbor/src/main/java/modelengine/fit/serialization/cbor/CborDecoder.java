/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.cbor;

import static modelengine.fitframework.inspection.Validation.between;
import static modelengine.fitframework.inspection.Validation.greaterThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.Convert;
import modelengine.fitframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 表示 CBOR 的解码器。
 *
 * @author 季聿阶
 * @since 2024-01-28
 */
public class CborDecoder {
    /**
     * 将指定的二进制数组进行解码。
     *
     * @param bytes 表示待解码的二进制数组的 {@code byte[]}。
     * @return 表示解码后的数据的 {@link Object}。
     * @throws IOException 当解码过程中发生错误时。
     */
    public Object decode(byte[] bytes) throws IOException {
        notNull(bytes, "The bytes to decode by CBOR cannot be null.");
        try (InputStream in = new ByteArrayInputStream(bytes)) {
            return this.decode(in);
        }
    }

    /**
     * 从指定字节输入流中读取字节，并进行解码。
     *
     * @param in 表示指定的字节输入流的 {@link InputStream}。
     * @return 表示解码后的数据的 {@link Object}。
     * @throws IOException 当解码过程中发生错误时。
     */
    public Object decode(InputStream in) throws IOException {
        int first = greaterThanOrEquals(in.read(), 0, "No enough byte to decode by CBOR.");
        int type = (first & 0b111_00000) >> 5;
        switch (type) {
            case 0:
                return this.decodeUnsignedInteger(in, first);
            case 1:
                return this.decodeNegativeInteger(in, first);
            case 2:
                return this.decodeBytes(in, (byte) first);
            case 3:
                return this.decodeString(in, (byte) first);
            case 4:
                return this.decodeArray(in, (byte) first);
            case 5:
                return this.decodeMap(in, (byte) first);
            case 7:
                return this.decodeFloatingPointAndSimpleType(in, (byte) first);
            default:
                throw new IllegalStateException(StringUtils.format("Unsupported type to decode by CBOR. [type={0}]",
                        type));
        }
    }

    private long decodeUnsignedInteger(InputStream in, int first) throws IOException {
        int dataType = first & 0b000_11111;
        if (dataType < 24) {
            return dataType;
        } else if (dataType == 24) {
            return greaterThanOrEquals((long) in.read(),
                    0,
                    "No enough byte to decode to unsigned integer by CBOR. [size=1]");
        } else if (dataType == 25) {
            byte[] bytes = new byte[2];
            this.readBytes(in, bytes);
            return Convert.toLong(bytes);
        } else if (dataType == 26) {
            byte[] bytes = new byte[4];
            this.readBytes(in, bytes);
            return Convert.toLong(bytes);
        } else if (dataType == 27) {
            byte[] bytes = new byte[8];
            this.readBytes(in, bytes);
            long data = Convert.toLong(bytes);
            if (data < 0) {
                throw new IllegalStateException(StringUtils.format("Unsupported number scope. [data={0}]", data));
            }
            return data;
        } else {
            throw new IllegalStateException(StringUtils.format("Unsupported unsigned integer type. [type={0}]",
                    dataType));
        }
    }

    private Number decodeNegativeInteger(InputStream in, int first) throws IOException {
        long unsignedInteger = this.decodeUnsignedInteger(in, first);
        return -(unsignedInteger + 1);
    }

    private byte[] decodeBytes(InputStream in, byte first) throws IOException {
        long len = this.decodeUnsignedInteger(in, first);
        between(len, 0L, (long) Integer.MAX_VALUE, "The bytes length is out of range. [length={0}]", len);
        byte[] bytes = new byte[(int) len];
        if (len == 0) {
            return bytes;
        }
        int read = in.read(bytes);
        Validation.equals(read,
                (int) len,
                "No enough bytes to decode to bytes by CBOR. [total={0}, actual={1}]",
                len,
                read);
        return bytes;
    }

    private String decodeString(InputStream in, byte first) throws IOException {
        return new String(this.decodeBytes(in, first), StandardCharsets.UTF_8);
    }

    private List<Object> decodeArray(InputStream in, byte first) throws IOException {
        long len = this.decodeUnsignedInteger(in, first);
        between(len, 0L, (long) Integer.MAX_VALUE, "The array length is out of range. [length={0}]", len);
        List<Object> list = new LinkedList<>();
        for (int i = 0; i < len; i++) {
            list.add(this.decode(in));
        }
        return list;
    }

    private Map<Object, Object> decodeMap(InputStream in, byte first) throws IOException {
        long size = this.decodeUnsignedInteger(in, first);
        between(size, 0L, (long) Integer.MAX_VALUE, "The map size is out of range. [size={0}]", size);
        Map<Object, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            Object key = this.decode(in);
            Object value = this.decode(in);
            map.put(key, value);
        }
        return map;
    }

    private Object decodeFloatingPointAndSimpleType(InputStream in, byte first) throws IOException {
        if (first == CborConstant.NULL) {
            return null;
        }
        if (first == CborConstant.TRUE) {
            return true;
        }
        if (first == CborConstant.FALSE) {
            return false;
        }
        if (first == CborConstant.FLOAT) {
            byte[] bytes = new byte[4];
            this.readBytes(in, bytes);
            return Convert.toFloat(bytes);
        }
        if (first == CborConstant.DOUBLE) {
            byte[] bytes = new byte[8];
            this.readBytes(in, bytes);
            return Convert.toDouble(bytes);
        }
        throw new IllegalStateException(StringUtils.format("Unsupported type to decode by CBOR. [type={0}]",
                first & 0b000_11111));
    }

    private void readBytes(InputStream in, byte[] bytes) throws IOException {
        int len = bytes.length;
        int read = in.read(bytes);
        Validation.equals(read, len, "No enough bytes to decode. [need={0}, read={1}]", len, read);
    }
}
