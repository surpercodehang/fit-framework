/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.serializer;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.EntitySerializer;
import modelengine.fit.http.entity.EntityWriteException;
import modelengine.fit.http.entity.ReadableBinaryEntity;
import modelengine.fit.http.entity.support.DefaultReadableBinaryEntity;
import modelengine.fitframework.inspection.Nonnull;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * 表示可以任意消息体格式的序列化器。
 *
 * @author 季聿阶
 * @since 2023-10-09
 */
public class ReadableBinaryEntitySerializer implements EntitySerializer<ReadableBinaryEntity> {
    /** 表示 {@link ReadableBinaryEntitySerializer} 的单例实现。 */
    public static final EntitySerializer<ReadableBinaryEntity> INSTANCE = new ReadableBinaryEntitySerializer();

    private ReadableBinaryEntitySerializer() {}

    @Override
    public void serializeEntity(@Nonnull ReadableBinaryEntity entity, Charset charset, OutputStream out) {
        throw new EntityWriteException("Unsupported to serialize entity of Content-Type '*/*'.");
    }

    @Override
    public ReadableBinaryEntity deserializeEntity(@Nonnull InputStream in, Charset charset,
            @Nonnull HttpMessage httpMessage, Type objectType) {
        return new DefaultReadableBinaryEntity(httpMessage, in);
    }
}
