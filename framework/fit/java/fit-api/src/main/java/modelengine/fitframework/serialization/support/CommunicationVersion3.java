/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.support;

import static java.nio.charset.StandardCharsets.UTF_8;

import modelengine.fitframework.serialization.CommunicationVersion;
import modelengine.fitframework.serialization.RequestMetadata;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.Version;
import modelengine.fitframework.util.Convert;
import modelengine.fitframework.util.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 表示 {@link CommunicationVersion} 的第 3 版实现。
 *
 * @author 季聿阶
 * @since 2024-02-18
 */
public class CommunicationVersion3 implements CommunicationVersion {
    /** 表示第 3 版通讯实现的单例。 */
    public static final CommunicationVersion INSTANCE = new CommunicationVersion3();

    private CommunicationVersion3() {}

    @Override
    public short supported() {
        return 3;
    }

    @Override
    public void serializeRequestMetadata(RequestMetadata metadata, OutputStream out) throws IOException {
        out.write(Convert.toBytes(metadata.dataFormatByte()));
        byte[] genericableIdBytes = metadata.genericableId().getBytes(UTF_8);
        out.write(Convert.toBytes((short) genericableIdBytes.length));
        out.write(genericableIdBytes);
        Version.serializer().serialize(metadata.genericableVersion(), out);
        byte[] fitableIdBytes = metadata.fitableId().getBytes(UTF_8);
        out.write(Convert.toBytes((short) fitableIdBytes.length));
        out.write(fitableIdBytes);
        Version.serializer().serialize(metadata.fitableVersion(), out);
        TagLengthValues.serializer().serialize(metadata.tagValues(), out);
    }

    @Override
    public RequestMetadata deserializeRequestMetadata(InputStream in) throws IOException {
        byte dataFormatByte = Convert.toByte(IoUtils.read(in, 1));
        int genericableIdLength = Short.toUnsignedInt(Convert.toShort(IoUtils.read(in, 2)));
        String genericableId = new String(IoUtils.read(in, genericableIdLength), UTF_8);
        Version genericableVersion = Version.serializer().deserialize(in);
        int fitableIdLength = Short.toUnsignedInt(Convert.toShort(IoUtils.read(in, 2)));
        String fitableId = new String(IoUtils.read(in, fitableIdLength), UTF_8);
        Version fitableVersion = Version.serializer().deserialize(in);
        TagLengthValues tlv = TagLengthValues.serializer().deserialize(in);
        return RequestMetadata.custom()
                .dataFormat(dataFormatByte)
                .genericableId(genericableId)
                .genericableVersion(genericableVersion)
                .fitableId(fitableId)
                .fitableVersion(fitableVersion)
                .tagValues(tlv)
                .build();
    }

    @Override
    public void serializeResponseMetadata(ResponseMetadata metadata, OutputStream out) throws IOException {
        out.write(Convert.toBytes(metadata.dataFormatByte()));
        out.write(Convert.toBytes(metadata.code()));
        out.write(Convert.toBytes(getFlag(metadata)));
        byte[] messageBytes = Optional.ofNullable(metadata.message())
                .map(message -> message.getBytes(StandardCharsets.UTF_8))
                .orElse(new byte[0]);
        out.write(Convert.toBytes(messageBytes.length));
        out.write(messageBytes);
        TagLengthValues.serializer().serialize(metadata.tagValues(), out);
    }

    private static byte getFlag(ResponseMetadata metadata) {
        byte degradableFlag = metadata.isDegradable() ? (byte) 0x01 : (byte) 0x00;
        byte retryableFlag = metadata.isRetryable() ? (byte) 0x02 : (byte) 0x00;
        return (byte) (degradableFlag | retryableFlag);
    }

    @Override
    public ResponseMetadata deserializeResponseMetadata(InputStream in) throws IOException {
        byte dataFormat = Convert.toByte(IoUtils.read(in, 1));
        int code = Convert.toInteger(IoUtils.read(in, 4));
        byte flag = Convert.toByte(IoUtils.read(in, 1));
        boolean isDegradable = (flag & 0x01) == 0x01;
        boolean isRetryable = (flag & 0x02) == 0x02;
        int messageLength = Convert.toInteger(IoUtils.read(in, 4));
        byte[] messageBytes = IoUtils.read(in, messageLength);
        TagLengthValues tagValues = TagLengthValues.serializer().deserialize(in);
        return ResponseMetadata.custom()
                .dataFormat(dataFormat)
                .code(code)
                .isDegradable(isDegradable)
                .isRetryable(isRetryable)
                .message(new String(messageBytes, StandardCharsets.UTF_8))
                .tagValues(tagValues)
                .build();
    }
}
