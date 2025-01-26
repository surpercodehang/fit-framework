/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.support;

import static modelengine.fitframework.inspection.Validation.between;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fitframework.serialization.ByteSerializer;
import modelengine.fitframework.serialization.CommunicationVersion;
import modelengine.fitframework.serialization.RequestMetadata;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.Version;
import modelengine.fitframework.util.Convert;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 为 {@link RequestMetadata} 提供默认实现。
 *
 * @author 季聿阶
 * @since 2023-11-27
 */
public class DefaultRequestMetadata implements RequestMetadata {
    private final int dataFormat;
    private final String genericableId;
    private final Version genericableVersion;
    private final String fitableId;
    private final Version fitableVersion;
    private final TagLengthValues tagValues;
    private final String accessToken;

    private DefaultRequestMetadata(int dataFormat, String genericableId, Version genericableVersion, String fitableId,
            Version fitableVersion, TagLengthValues tagLengthValues, String accessToken) {
        this.dataFormat = between(dataFormat, 0, 0x7F, "The data format is out of range. [dataFormat={0}]", dataFormat);
        this.genericableId = genericableId;
        this.genericableVersion = genericableVersion;
        this.fitableId = fitableId;
        this.fitableVersion = fitableVersion;
        this.tagValues = getIfNull(tagLengthValues, TagLengthValues::create);
        this.accessToken = accessToken;
    }

    @Override
    public byte dataFormatByte() {
        return (byte) this.dataFormat;
    }

    @Override
    public int dataFormat() {
        return this.dataFormat;
    }

    @Override
    public String genericableId() {
        return this.genericableId;
    }

    @Override
    public Version genericableVersion() {
        return this.genericableVersion;
    }

    @Override
    public String fitableId() {
        return this.fitableId;
    }

    @Override
    public Version fitableVersion() {
        return this.fitableVersion;
    }

    @Override
    public TagLengthValues tagValues() {
        return this.tagValues;
    }

    @Override
    public String accessToken() {
        return this.accessToken;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.serialize());
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof RequestMetadata) {
            return this.hashCode() == another.hashCode();
        }
        return false;
    }

    @Override
    public String toString() {
        return "{\"dataFormat\": " + this.dataFormat + ", \"genericableId\": \"" + this.genericableId
                + "\", \"genericableVersion\": \"" + this.genericableVersion + "\", \"fitableId\": " + this.fitableId
                + "\", \"fitableVersion\": \"" + this.fitableVersion + "\", \"tagValues\": " + this.tagValues + '}';
    }

    /**
     * 为创建 {@link DefaultRequestMetadata} 的实例提供构建器。
     *
     * @author 季聿阶
     * @since 2023-11-27
     */
    public static final class Builder implements RequestMetadata.Builder {
        private int dataFormat;
        private String genericableId;
        private Version genericableVersion;
        private String fitableId;
        private Version fitableVersion;
        private TagLengthValues tagValues;
        private String accessToken;

        /**
         * 使用已知的远端调用元数据初始化 {@link Builder} 类的新实例。
         *
         * @param metadata 表示已知的远端调用的元数据的 {@link RequestMetadata}。构建器中会以该实例中的现有数据作为初始值。
         */
        public Builder(RequestMetadata metadata) {
            if (metadata != null) {
                this.dataFormat = metadata.dataFormat();
                this.genericableId = metadata.genericableId();
                this.genericableVersion = metadata.genericableVersion();
                this.fitableId = metadata.fitableId();
                this.fitableVersion = metadata.fitableVersion();
                this.tagValues = metadata.tagValues();
                this.accessToken = metadata.accessToken();
            }
        }

        @Override
        public Builder dataFormat(int dataFormat) {
            this.dataFormat = dataFormat;
            return this;
        }

        @Override
        public Builder genericableId(String genericableId) {
            this.genericableId = genericableId;
            return this;
        }

        @Override
        public Builder genericableVersion(Version version) {
            this.genericableVersion = version;
            return this;
        }

        @Override
        public Builder fitableId(String fitableId) {
            this.fitableId = fitableId;
            return this;
        }

        @Override
        public Builder fitableVersion(Version version) {
            this.fitableVersion = version;
            return this;
        }

        @Override
        public Builder tagValues(TagLengthValues tagLengthValues) {
            this.tagValues = tagLengthValues;
            return this;
        }

        @Override
        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        @Override
        public RequestMetadata build() {
            return new DefaultRequestMetadata(this.dataFormat,
                    this.genericableId,
                    this.genericableVersion,
                    this.fitableId,
                    this.fitableVersion,
                    this.tagValues,
                    this.accessToken);
        }
    }

    /**
     * 为 {@link RequestMetadata} 提供序列化器。
     * <p>该序列化器为通用的通讯序列化器，但是不同的通讯通道应该优先使用通道特有属性，只有通道没有特有属性可用时，才考虑使用该序列化器。</p>
     *
     * @author 季聿阶
     * @since 2023-11-27
     */
    public static class Serializer implements ByteSerializer<RequestMetadata> {
        /** 获取序列化程序的唯一实例。 */
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(RequestMetadata metadata, OutputStream out) throws IOException {
            CommunicationVersion version =
                    notNull(CommunicationVersion.latest().peek(), "No supported communication version.");
            out.write(Convert.toBytes(version.supported()));
            version.serializeRequestMetadata(metadata, out);
        }

        @Override
        public RequestMetadata deserialize(InputStream in) throws IOException {
            short versionNum = Convert.toShort(IoUtils.read(in, 2));
            CommunicationVersion version = CommunicationVersion.choose(versionNum)
                    .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                            "No supported communication version. [num={0}]",
                            versionNum)));
            return version.deserializeRequestMetadata(in);
        }
    }
}
