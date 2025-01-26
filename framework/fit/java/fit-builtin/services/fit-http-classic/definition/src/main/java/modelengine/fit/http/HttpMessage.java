/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http;

import static modelengine.fit.http.protocol.MimeType.APPLICATION_OCTET_STREAM;

import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.EntitySerializer;
import modelengine.fit.http.header.ContentType;
import modelengine.fit.http.header.CookieCollection;
import modelengine.fit.http.protocol.HttpVersion;
import modelengine.fit.http.protocol.MessageHeaderValues;
import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.serialization.ObjectSerializer;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * 表示 Http 消息。
 *
 * @author 季聿阶
 * @since 2022-08-03
 */
public interface HttpMessage extends HttpResourceSupplier {
    /**
     * 获取 Http 消息的版本号。
     *
     * @return 表示 Http 消息的版本号的 {@link HttpVersion}。
     */
    HttpVersion httpVersion();

    /**
     * 获取 Http 消息的消息头集合。
     *
     * @return 表示 Http 消息的消息头集合的 {@link MessageHeaders}。
     */
    MessageHeaders headers();

    /**
     * 获取 Http 消息的传输编码方式。
     *
     * @return 表示 Http 消息的传输编码方式的 {@link Optional}{@code <}{@link String}{@code >}。
     */
    Optional<String> transferEncoding();

    /**
     * 判断 Http 消息的传输编码方式是否为 {@link MessageHeaderValues#CHUNKED}。
     *
     * @return 表示 Http 消息的传输编码方式是否为 {@link MessageHeaderValues#CHUNKED}
     * 的标记的 {@code boolean}。
     */
    boolean isChunked();

    /**
     * 获取 Http 消息的消息体的数据格式。
     *
     * @return 表示 Http 消息的消息体的数据格式的 {@link Optional}{@code <}{@link ContentType}{@code >}。
     */
    Optional<ContentType> contentType();

    /**
     * 获取 Http 消息的消息体的 {@code MIME} 格式，如果没有明确指定，按照 {@link MimeType#APPLICATION_OCTET_STREAM} 处理。
     *
     * @return 表示 Http 消息的消息体的 {@code MIME} 格式的 {@link MimeType}。
     */
    default MimeType mimeTypeOrDefault() {
        return this.contentType().map(ContentType::mediaType).map(MimeType::from).orElse(APPLICATION_OCTET_STREAM);
    }

    /**
     * 获取 Http 消息的消息体的长度。
     *
     * @return 表示 Http 消息的消息体的长度的 {@code int}。当消息体长度未知时，返回 {@code -1}。
     */
    int contentLength();

    /**
     * 获取 Http 消息的 Cookie 集合。
     *
     * @return 表示 Http 消息的 Cookie 集合的 {@link CookieCollection}。
     */
    CookieCollection cookies();

    /**
     * 获取 Http 消息的消息体的结构化数据。
     *
     * @return 表示消息体的结构化数据的 {@link Optional}{@code <}{@link Entity}{@code >}。
     */
    Optional<Entity> entity();

    /**
     * 获取当前的 Http 消息体的序列化器。
     *
     * @return 表示当前的 Http 消息体的序列化器的 {@link EntitySerializer}{@code <? extends }{@link Entity}{@code >}。
     * @throws IllegalStateException 当不存在指定消息体格式的序列化器时。
     */
    EntitySerializer<? extends Entity> entitySerializer();

    /**
     * 获取当前的 Http 的 Json 序列化器。
     *
     * @return 表示当前的 Http 的 Json 序列化器的 {@link Optional}{@code <}{@link ObjectSerializer}{@code >}。
     */
    Optional<ObjectSerializer> jsonSerializer();

    /**
     * 获取当前的 Http 消息体的序列化器。
     *
     * @param type 表示 {@link Entity} 内数据类型的 {@link Type}。
     * @return 表示当前的 Http 消息体的序列化器的 {@link EntitySerializer}{@code <? extends }{@link Entity}{@code >}。
     * @throws IllegalStateException 当不存在指定消息体格式的序列化器时。
     */
    EntitySerializer<? extends Entity> entitySerializer(Type type);

    /**
     * 判断当前 Http 消息体是否已经提交过。
     *
     * @return 如果当前 Http 消息体已经提交过，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isCommitted();

    /**
     * 向当前 Http 消息设置自定义的消息体序列化器。
     *
     * @param mimeType 表示序列化器对应的消息体格式的 {@link MimeType}。
     * @param serializer 表示待设置的自定义消息体序列化器的 {@link EntitySerializer}{@code <? extends }{@link
     * Entity}{@code >}。
     */
    void customEntitySerializer(MimeType mimeType, EntitySerializer<? extends Entity> serializer);

    /**
     * 向当前 Http 消息设置自定义的 Json 序列化器。
     *
     * @param objectSerializer 表示待设置的自定义的 Json 序列化器的 {@link ObjectSerializer}。
     */
    void customJsonSerializer(ObjectSerializer objectSerializer);
}
