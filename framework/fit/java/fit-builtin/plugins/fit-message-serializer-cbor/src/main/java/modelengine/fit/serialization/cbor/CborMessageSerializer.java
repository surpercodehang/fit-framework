/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.cbor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.serialization.MessageSerializer;
import modelengine.fit.serialization.util.MessageSerializerUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ArrayUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 表示 {@link MessageSerializer} 的 CBOR 的实现。
 * <p><a href="https://datatracker.ietf.org/doc/html/rfc8949">RFC 8949</a> 列出了 CBOR 的详细规范。</p>
 *
 * @author 季聿阶
 * @since 2024-01-25
 */
@Order(Order.LOW)
@Component
public class CborMessageSerializer implements MessageSerializer {
    private final ObjectSerializer serializer;
    private final Config config;

    /**
     * 构造一个新的 {@link CborMessageSerializer} 实例。
     *
     * @param serializer 表示用于序列化和反序列化实例的 {@link ObjectSerializer}。
     * @param config 表示配置的 {@link Config}。
     */
    public CborMessageSerializer(@Fit(alias = "cbor") ObjectSerializer serializer, Config config) {
        this.serializer = notNull(serializer, "The CBOR serializer cannot be null.");
        this.config = notNull(config, "The message serializer config cannot be null.");
    }

    @Override
    public byte[] serializeRequest(Type[] argumentTypes, Object[] arguments) {
        return this.serializer.serialize(arguments, UTF_8);
    }

    @Override
    public Object[] deserializeRequest(Type[] argumentTypes, byte[] serialized) {
        MessageSerializerUtils.isSupportedLength(serialized.length, this.config);
        List<Object> deserialized = this.serializer.deserialize(serialized,
                UTF_8,
                TypeUtils.parameterized(List.class, new Type[] {Object.class}));
        Object[] array = new Object[argumentTypes.length];
        for (int i = 0; i < deserialized.size(); i++) {
            array[i] = ObjectUtils.toCustomObject(deserialized.get(i), argumentTypes[i]);
        }
        return array;
    }

    @Override
    public <T> byte[] serializeResponse(Type returnType, T returnData) {
        return this.serializer.serialize(returnData, UTF_8);
    }

    @Override
    public <T> T deserializeResponse(Type returnType, byte[] serialized) {
        if (ArrayUtils.isEmpty(serialized)) {
            return null;
        }
        MessageSerializerUtils.isSupportedLength(serialized.length, this.config);
        return this.serializer.deserialize(serialized, UTF_8, returnType);
    }

    @Override
    public boolean isSupported(Method method) {
        return true;
    }

    @Override
    public int getFormat() {
        return SerializationFormat.CBOR.code();
    }
}
