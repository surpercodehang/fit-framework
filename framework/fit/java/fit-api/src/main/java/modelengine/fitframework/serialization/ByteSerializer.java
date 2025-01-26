/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 载体为字节序列的序列化器。
 *
 * @param <T> 表示序列化的对象的类型。
 * @author 季聿阶
 * @since 2021-12-10
 */
public interface ByteSerializer<T> {
    /**
     * 将对象序列化到输出字节流中。
     *
     * @param value 表示待序列化的对象的 {@link Object}。
     * @param out 表示序列化的目标输出字节流的 {@link OutputStream}。
     * @throws IOException 当序列化过程中发生输入输出异常时。
     */
    void serialize(T value, OutputStream out) throws IOException;

    /**
     * 将对象序列化为字节序列。
     *
     * @param value 表示待序列化的对象的 {@link Object}。
     * @return 表示序列化后的字节序列的 {@code byte[]}。
     * @throws SerializationException 当序列化过程中发生异常时。
     */
    default byte[] serialize2Bytes(T value) {
        return serialize2Bytes(this, value);
    }

    /**
     * 将对象通过指定的序列化器，序列化为字节序列。
     *
     * @param serializer 表示使用的序列化程序的 {@link ByteSerializer}{@code <}{@link T}{@code >}。
     * @param value 表示待序列化的对象的 {@link Object}。
     * @return 表示序列化后得到的包含源对象信息的字节序列的 {@code byte[]}。
     * @throws SerializationException 当序列化过程发生异常时。
     */
    static <T> byte[] serialize2Bytes(ByteSerializer<T> serializer, T value) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            serializer.serialize(value, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Fail to serialize object with serializer: " + e.getMessage(), e);
        }
    }

    /**
     * 将输入字节流的内容反序列化为对象。
     *
     * @param in 表示包含对象信息的输入字节流的 {@link InputStream}。
     * @return 表示从输入字节流中反序列化得到的对象的 {@link Object}。
     * @throws IOException 当反序列化过程中发生输入输出异常时。
     */
    T deserialize(InputStream in) throws IOException;

    /**
     * 将输入字节序列反序列化为对象。
     *
     * @param value 表示包含对象信息的字节序列的 {@code byte[]}。
     * @return 表示从字节序列中反序列化得到的对象的 {@link Object}。
     * @throws SerializationException 当反序列化过程发生异常时。
     */
    default T deserialize(byte[] value) {
        return deserialize(this, value);
    }

    /**
     * 将输入字节序列通过指定的序列化器，反序列化为对象。
     *
     * @param serializer 表示使用的序列化程序的 {@link ByteSerializer}{@code <}{@link T}{@code >}。
     * @param value 表示包含对象信息的字节序列的 {@code byte[]}。
     * @return 表示从字节序列中反序列化得到的对象的 {@link Object}。
     * @throws SerializationException 当反序列化过程发生异常时。
     */
    static <T> T deserialize(ByteSerializer<T> serializer, byte[] value) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(value)) {
            return serializer.deserialize(in);
        } catch (IOException e) {
            throw new SerializationException("Fail to deserialize object from bytes: " + e.getMessage(), e);
        }
    }
}
