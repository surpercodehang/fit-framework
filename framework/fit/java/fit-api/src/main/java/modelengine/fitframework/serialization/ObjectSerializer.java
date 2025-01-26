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
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * 对象序列化器。
 * <p>目前支持的序列化方式有：
 * <ol>
 *     <li>Json，注入时的别名为：{@code json}。</li>
 *     <li>CBOR，注入时的别名为：{@code cbor}。</li>
 * </ol>
 * </p>
 *
 * @author 季聿阶
 * @since 2022-08-03
 */
public interface ObjectSerializer {
    /**
     * 将指定对象按照指定编码方式序列化到输出字节流中。
     *
     * @param object 表示指定对象的 {@link T}。
     * @param charset 表示指定的编码方式的 {@link Charset}。当 {@code charset} 为 {@code null} 时，默认选择
     * {@link StandardCharsets#UTF_8}。
     * @param out 表示输出的字节流的 {@link OutputStream}。
     * @param context 表示序列化时的上下文信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param <T> 表示指定对象类型的 {@link T}。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws SerializationException 当序列化过程发生异常时。
     */
    <T> void serialize(T object, Charset charset, OutputStream out, Map<String, Object> context)
            throws SerializationException;

    /**
     * 将指定对象按照指定的编码方式序列化到输出字节流中。
     *
     * @param object 表示指定对象的 {@link T}。
     * @param charset 表示指定的编码方式的 {@link Charset}。当 {@code charset} 为 {@code null} 时，默认选择
     * {@link StandardCharsets#UTF_8}。
     * @param out 表示输出的字节流的 {@link OutputStream}。
     * @param <T> 表示指定对象类型的 {@link T}。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws SerializationException 当序列化过程发生异常时。
     */
    default <T> void serialize(T object, Charset charset, OutputStream out) throws SerializationException {
        this.serialize(object, charset, out, Collections.emptyMap());
    }

    /**
     * 将指定对象按照 {@link StandardCharsets#UTF_8} 的编码方式序列化到输出字节流中。
     *
     * @param object 表示指定对象的 {@link T}。
     * @param out 表示输出的字节流的 {@link OutputStream}。
     * @param <T> 表示指定对象类型的 {@link T}。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws SerializationException 当序列化过程发生异常时。
     */
    default <T> void serialize(T object, OutputStream out) {
        this.serialize(object, StandardCharsets.UTF_8, out);
    }

    /**
     * 将指定对象按照指定的编码方式序列化为字节数组。
     *
     * @param object 表示指定对象的 {@link T}。
     * @param charset 表示指定的编码方式的 {@link Charset}。
     * @param <T> 表示指定对象类型的 {@link T}。
     * @return 表示序列化后的字节数组的 {@code byte[]}。
     * @throws SerializationException 当序列化过程发生异常时。
     */
    default <T> byte[] serialize(T object, Charset charset) throws SerializationException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            this.serialize(object, charset, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize object to bytes.", e);
        }
    }

    /**
     * 将指定对象按照 {@link StandardCharsets#UTF_8} 的编码方式序列化为字符串。
     *
     * @param object 表示指定对象的 {@link T}。
     * @return 表示序列化后的字符串 {@link String}。
     * @throws SerializationException 当序列化过程发生异常时。
     */
    default <T> String serialize(T object) throws SerializationException {
        return new String(this.serialize(object, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    /**
     * 将输入字节流中的数据按照指定编码方式反序列化为指定类型的对象。
     *
     * @param in 表示输入字节流的 {@link InputStream}。
     * @param charset 表示指定的编码方式的 {@link Charset}。当 {@code charset} 为 {@code null} 时，默认选择
     * {@link StandardCharsets#UTF_8}。
     * @param objectType 表示指定对象类型的 {@link Type}。
     * @param context 表示反序列化时的上下文信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param <T> 表示指定对象类型的 {@link T}。
     * @return 表示反序列化后的对象的 {@link T}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws SerializationException 当反序列化过程发生异常时。
     */
    <T> T deserialize(InputStream in, Charset charset, Type objectType, Map<String, Object> context)
            throws SerializationException;

    /**
     * 将输入字节流中的数据按照指定编码方式反序列化为指定类型的对象。
     *
     * @param in 表示输入字节流的 {@link InputStream}。
     * @param charset 表示指定的编码方式的 {@link Charset}。当 {@code charset} 为 {@code null} 时，默认选择
     * {@link StandardCharsets#UTF_8}。
     * @param objectType 表示指定对象类型的 {@link Type}。
     * @param <T> 表示指定对象类型的 {@link T}。
     * @return 表示反序列化后的对象的 {@link T}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws SerializationException 当反序列化过程发生异常时。
     */
    default <T> T deserialize(InputStream in, Charset charset, Type objectType) throws SerializationException {
        return this.deserialize(in, charset, objectType, Collections.emptyMap());
    }

    /**
     * 将输入字节流中的数据按照 {@link StandardCharsets#UTF_8} 的编码方式反序列化为指定类型的对象。
     *
     * @param in 表示输入字节流的 {@link InputStream}。
     * @param objectType 表示指定对象类型的 {@link Type}。
     * @param <T> 表示指定对象类型的 {@link T}。
     * @return 表示反序列化后的对象的 {@link T}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws SerializationException 当反序列化过程发生异常时。
     */
    default <T> T deserialize(InputStream in, Type objectType) {
        return this.deserialize(in, StandardCharsets.UTF_8, objectType);
    }

    /**
     * 将字节数组按照指定编码方式反序列化为指定类型的对象。
     *
     * @param bytes 表示字节数组的 {@code byte[]}。
     * @param charset 表示指定编码方式的 {@link Charset}。
     * @param objectType 表示指定对象类型的 {@link Type}。
     * @param <T> 表示指定对象类型的 {@link T}。
     * @return 表示反序列化后的对象的 {@link T}。
     * @throws SerializationException 当反序列化过程发生异常时。
     */
    default <T> T deserialize(byte[] bytes, Charset charset, Type objectType) throws SerializationException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            return this.deserialize(in, charset, objectType);
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize bytes to object.", e);
        }
    }

    /**
     * 将指定字符串按照 {@link StandardCharsets#UTF_8} 的编码方式反序列化为指定类型的对象。
     *
     * @param s 表示指定字符串。
     * @param objectType 表示指定对象类型的 {@link Type}。
     * @param <T> 表示指定对象类型的 {@link T}。
     * @return 表示反序列化后的对象的 {@link T}。
     * @throws SerializationException 当反序列化过程发生异常时。
     */
    default <T> T deserialize(String s, Type objectType) throws SerializationException {
        return this.deserialize(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8, objectType);
    }
}
