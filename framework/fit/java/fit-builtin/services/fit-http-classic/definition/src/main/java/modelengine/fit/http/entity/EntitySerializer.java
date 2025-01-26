/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.serializer.FormUrlEncodedEntitySerializer;
import modelengine.fit.http.entity.serializer.JsonEntitySerializer;
import modelengine.fit.http.entity.serializer.MultiPartEntitySerializer;
import modelengine.fit.http.entity.serializer.ReadableBinaryEntitySerializer;
import modelengine.fit.http.entity.serializer.TextEntitySerializer;
import modelengine.fit.http.entity.serializer.TextEventStreamSerializer;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.serialization.ObjectSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 表示消息体的序列化器。
 *
 * @param <E> 表示消息体类型的 {@link E}。
 * @author 季聿阶
 * @since 2022-10-11
 */
public interface EntitySerializer<E extends Entity> {
    /**
     * 将消息体内容按照指定字符集序列化到指定输出流中。
     *
     * @param entity 表示消息体内容的 {@link E}。
     * @param charset 表示指定的字符集的 {@link Charset}。
     * @param out 表示指定输出流的 {@link OutputStream}。
     * @throws EntityWriteException 当将消息体按指定方式进行序列化失败时。
     */
    void serializeEntity(@Nonnull E entity, Charset charset, OutputStream out);

    /**
     * 将消息体内容按照 {@code UTF_8} 字符集序列化到指定输出流中。
     *
     * @param entity 表示消息体内容的 {@link E}。
     * @param out 表示指定输出流的 {@link OutputStream}。
     * @throws EntityWriteException 当将消息体按指定方式进行序列化失败时。
     */
    default void serializeEntity(@Nonnull E entity, OutputStream out) {
        this.serializeEntity(entity, StandardCharsets.UTF_8, out);
    }

    /**
     * 将消息体内容按照指定字符集序列化为二进制数组。
     *
     * @param entity 表示消息体内容的 {@link E}。
     * @param charset 表示指定的字符集的 {@link Charset}。
     * @return 表示序列化后的二进制数组的 {@code byte[]}。
     * @throws EntityWriteException 当将消息体按指定方式进行序列化失败时。
     */
    default byte[] serializeEntity(@Nonnull E entity, Charset charset) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            this.serializeEntity(entity, charset, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new EntityWriteException("Failed to serialize object to bytes.", e);
        }
    }

    /**
     * 将输入字节流中的数据按照指定编码方式反序列化为指定类型的消息体对象。
     *
     * @param in 表示输入字节流的 {@link InputStream}。
     * @param charset 表示指定的编码方式的 {@link Charset}。当 {@code charset} 为 {@code null} 时，默认选择
     * {@link StandardCharsets#UTF_8}。
     * @param httpMessage 表示所属的 Http 消息的 {@link HttpMessage}。
     * @param objectType 表示消息体中对象类型的 {@link Type}。
     * @return 表示反序列化后的消息体对象的 {@link E}。
     */
    E deserializeEntity(@Nonnull InputStream in, Charset charset, @Nonnull HttpMessage httpMessage, Type objectType);

    /**
     * 将输入字节流中的数据按照指定编码方式反序列化为指定类型的消息体对象。
     *
     * @param in 表示输入字节流的 {@link InputStream}。
     * @param charset 表示指定的编码方式的 {@link Charset}。当 {@code charset} 为 {@code null} 时，默认选择
     * {@link StandardCharsets#UTF_8}。
     * @param httpMessage 表示所属的 Http 消息的 {@link HttpMessage}。
     * @return 表示反序列化后的消息体对象的 {@link E}。
     */
    default E deserializeEntity(@Nonnull InputStream in, Charset charset, @Nonnull HttpMessage httpMessage) {
        return this.deserializeEntity(in, charset, httpMessage, null);
    }

    /**
     * 将输入字节流中的数据按照指定编码方式反序列化为指定类型的消息体对象。
     *
     * @param in 表示输入字节流的 {@link InputStream}。
     * @param httpMessage 表示所属的 Http 消息的 {@link HttpMessage}。
     * @return 表示反序列化后的消息体对象的 {@link E}。
     */
    default E deserializeEntity(@Nonnull InputStream in, @Nonnull HttpMessage httpMessage) {
        return this.deserializeEntity(in, StandardCharsets.UTF_8, httpMessage, null);
    }

    /**
     * 将指定的二进制数组按照指定字符集反序列化为消息体内容。
     *
     * @param bytes 表示指定的二进制数组的 {@code byte[]}。
     * @param charset 表示指定的字符集的 {@link Charset}。
     * @param httpMessage 表示所属的 Http 消息的 {@link HttpMessage}。
     * @return 表示反序列后的消息体内容的 {@link E}。
     * @throws EntityReadException 当从 {@code httpMessage} 中解析消息体发生错误时。
     */
    default E deserializeEntity(@Nonnull byte[] bytes, Charset charset, @Nonnull HttpMessage httpMessage) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            return this.deserializeEntity(in, charset, httpMessage);
        } catch (IOException e) {
            throw new EntityReadException("Failed to deserialize bytes to object.", e);
        }
    }

    /**
     * 将指定的二进制数组按照指定字符集反序列化为消息体内容。
     *
     * @param bytes 表示指定的二进制数组的 {@code byte[]}。
     * @param charset 表示指定的字符集的 {@link Charset}。
     * @param httpMessage 表示所属的 Http 消息的 {@link HttpMessage}。
     * @param objectType 表示消息体中对象类型的 {@link Type}。
     * @return 表示反序列后的消息体内容的 {@link E}。
     * @throws EntityReadException 当从 {@code httpMessage} 中解析消息体发生错误时。
     */
    default E deserializeEntity(@Nonnull byte[] bytes, Charset charset, @Nonnull HttpMessage httpMessage,
            Type objectType) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            return this.deserializeEntity(in, charset, httpMessage, objectType);
        } catch (IOException e) {
            throw new EntityReadException("Failed to deserialize bytes to object.", e);
        }
    }

    /**
     * 获取消息体格式为 {@code 'text/plain'} 的序列化器。
     *
     * @return 表示消息体格式为 {@code 'text/plain'} 的序列化器的 {@link EntitySerializer}{@code <}{@link
     * TextEntity}{@code >}。
     */
    static EntitySerializer<TextEntity> textSerializer() {
        return TextEntitySerializer.INSTANCE;
    }

    /**
     * 获取消息体格式为 {@code 'text/event-stream'} 的序列化器。
     *
     * @param serializer 表示 JSON 序列化器的 {@link ObjectSerializer}。
     * @param type 表示消息体中数据类型的 {@link Type}。
     * @return 表示消息体格式为 {@code 'text/event-stream'} 的序列化器的 {@link EntitySerializer}{@code <}{@link
     * TextEventStreamEntity}{@code >}。
     */
    static EntitySerializer<TextEventStreamEntity> textEventStreamSerializer(ObjectSerializer serializer, Type type) {
        return new TextEventStreamSerializer(type, serializer);
    }

    /**
     * 获取消息体格式为 {@code 'application/json'} 的序列化器。
     *
     * @param jsonSerializer 表示 JSON 序列化器。
     * @return 表示消息体格式为 {@code 'application/json'} 的序列化器的 {@link EntitySerializer}{@code <}{@link
     * ObjectEntity}{@code <}{@link Object}{@code >>}。
     */
    static EntitySerializer<ObjectEntity<Object>> jsonSerializer(ObjectSerializer jsonSerializer) {
        return new JsonEntitySerializer<>(Object.class, jsonSerializer);
    }

    /**
     * 获取消息体格式为 {@code 'application/json'} 的序列化器。
     *
     * @param jsonSerializer 表示 JSON 序列化器。
     * @param type 表示消息体中数据类型的 {@link Type}。
     * @param <T> 表示消息体中数据类型的 {@link T}。
     * @return 表示消息体格式为 {@code 'application/json'} 的序列化器的 {@link EntitySerializer}{@code <}{@link
     * ObjectEntity}{@code <}{@link T}{@code >>}。
     */
    static <T> EntitySerializer<ObjectEntity<T>> jsonSerializer(ObjectSerializer jsonSerializer, Type type) {
        return new JsonEntitySerializer<>(type, jsonSerializer);
    }

    /**
     * 获取消息体格式为 {@code 'multipart/*'} 的序列化器。
     *
     * @return 表示消息体格式为 {@code 'multipart/*'} 的序列化器的 {@link EntitySerializer}{@code
     * <}{@link PartitionedEntity}{@code >}。
     */
    static EntitySerializer<PartitionedEntity> multiPartSerializer() {
        return MultiPartEntitySerializer.INSTANCE;
    }

    /**
     * 获取消息体格式为 {@code 'application/x-www-form-urlencoded'} 的序列化器。
     *
     * @return 表示消息体格式为 {@code 'application/x-www-form-urlencoded'} 的序列化器的 {@link EntitySerializer}{@code
     * <}{@link MultiValueEntity}{@code >}。
     */
    static EntitySerializer<MultiValueEntity> formUrlEncodedSerializer() {
        return FormUrlEncodedEntitySerializer.INSTANCE;
    }

    /**
     * 获取默认的可以处理任意消息体格式的序列化器。
     *
     * @return 表示默认的可以处理任意消息体格式的序列化器的 {@link EntitySerializer}{@code <}{@link
     * ReadableBinaryEntity}{@code >}。
     */
    static EntitySerializer<ReadableBinaryEntity> readableBinarySerializer() {
        return ReadableBinaryEntitySerializer.INSTANCE;
    }
}
