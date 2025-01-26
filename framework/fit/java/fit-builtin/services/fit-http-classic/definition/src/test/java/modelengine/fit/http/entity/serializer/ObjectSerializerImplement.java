/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.serializer;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.Convert;
import modelengine.fitframework.util.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 表示 {@link ObjectSerializer} 接口的实现
 *
 * @author 杭潇
 * @since 2023-02-21
 */
public class ObjectSerializerImplement implements ObjectSerializer {
    @Override
    public <T> void serialize(T object, Charset charset, OutputStream out, Map<String, Object> context)
            throws SerializationException {
        String intValue = cast(object);
        try {
            out.write(intValue.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize by String.", e);
        }
    }

    @Override
    public <T> T deserialize(InputStream in, Charset charset, Type objectType, Map<String, Object> context)
            throws SerializationException {
        try {
            byte[] read = IoUtils.read(in);
            return cast(Convert.toString(read));
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize by String.", e);
        }
    }
}
