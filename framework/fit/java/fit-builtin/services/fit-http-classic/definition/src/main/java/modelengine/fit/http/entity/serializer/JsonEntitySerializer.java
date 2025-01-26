/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.serializer;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.EntityReadException;
import modelengine.fit.http.entity.EntitySerializer;
import modelengine.fit.http.entity.EntityWriteException;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.support.DefaultObjectEntity;
import modelengine.fit.http.protocol.MessageHeaderNames;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示消息体格式为 {@code 'application/json'} 的序列化器。
 *
 * @param <T> 表示反序列化后对应的数据结构类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-10-11
 */
public class JsonEntitySerializer<T> implements EntitySerializer<ObjectEntity<T>> {
    private final Type type;
    private final ObjectSerializer jsonSerializer;

    public JsonEntitySerializer(Type type, ObjectSerializer jsonSerializer) {
        this.type = nullIf(type, Object.class);
        this.jsonSerializer = notNull(jsonSerializer, "The json serializer cannot be null.");
    }

    @Override
    public void serializeEntity(@Nonnull ObjectEntity<T> entity, Charset charset, OutputStream out) {
        try {
            this.jsonSerializer.serialize(entity.object(), charset, out);
        } catch (SerializationException e) {
            throw new EntityWriteException("Failed to serialize entity. [mimeType='application/json']", e);
        }
    }

    @Override
    public ObjectEntity<T> deserializeEntity(@Nonnull InputStream in, Charset charset, @Nonnull HttpMessage httpMessage,
            Type objectType) {
        try {
            Map<String, Object> context = new HashMap<>();
            httpMessage.headers()
                    .first(MessageHeaderNames.CONTENT_LENGTH)
                    .ifPresent(length -> context.put("length", Integer.parseInt(length)));
            T obj = this.jsonSerializer.deserialize(in, charset, this.type, context);
            return new DefaultObjectEntity<>(httpMessage, obj);
        } catch (SerializationException e) {
            throw new EntityReadException("Failed to deserialize message body. [mimeType='application/json']", e);
        }
    }
}
