/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.support;

import modelengine.fitframework.serialization.ByteSerializer;
import modelengine.fitframework.serialization.Version;
import modelengine.fitframework.util.Convert;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * 为 {@link Version} 提供默认实现。
 *
 * @author 梁济时
 * @since 2020-11-13
 */
public class DefaultVersion implements Version {
    private final byte major;
    private final byte minor;
    private final byte revision;

    public DefaultVersion(byte major, byte minor, byte revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    @Override
    public byte major() {
        return this.major;
    }

    @Override
    public byte minor() {
        return this.minor;
    }

    @Override
    public byte revision() {
        return this.revision;
    }

    @Override
    public String toString() {
        return StringUtils.format("{0}.{1}.{2}",
                Byte.toUnsignedInt(this.major()),
                Byte.toUnsignedInt(this.minor()),
                Byte.toUnsignedInt(this.revision()));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(ByteSerializer.serialize2Bytes(Version.serializer(), this));
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof Version) {
            return this.hashCode() == another.hashCode();
        }
        return false;
    }

    /**
     * 为创建 {@link DefaultVersion} 的实例提供构建器。
     *
     * @author 梁济时
     * @since 2020-11-13
     */
    public static final class Builder implements Version.Builder {
        private byte major;
        private byte minor;
        private byte revision;

        /**
         * 通过已知的泛服务版本信息来实例化 {@link Builder}。
         *
         * @param another 表示已知的泛服务版本信息的 {@link Version}。构建器中会以该实例中的现有数据作为初始值。
         */
        public Builder(Version another) {
            if (another != null) {
                this.major = another.major();
                this.minor = another.minor();
                this.revision = another.revision();
            }
        }

        /**
         * 通过泛服务版本信息的字符串形式来实例化 {@link Builder}。
         *
         * @param versionString 表示泛服务版本信息的字符串形式的 {@link String}。
         */
        public Builder(String versionString) {
            if (StringUtils.isBlank(versionString)) {
                return;
            }
            List<String> list = StringUtils.splitToList(versionString, '.');
            this.major = (byte) Integer.parseInt(list.get(0));
            if (list.size() > 1) {
                this.minor = (byte) Integer.parseInt(list.get(1));
            }
            if (list.size() > 2) {
                this.revision = (byte) Integer.parseInt(list.get(2));
            }
        }

        @Override
        public Builder major(byte major) {
            this.major = major;
            return this;
        }

        @Override
        public Builder minor(byte minor) {
            this.minor = minor;
            return this;
        }

        @Override
        public Builder revision(byte revision) {
            this.revision = revision;
            return this;
        }

        @Override
        public Version build() {
            return new DefaultVersion(this.major, this.minor, this.revision);
        }
    }

    /**
     * 为 {@link Version} 提供序列化程序。
     *
     * @author 梁济时
     * @since 2020-11-13
     */
    public static class Serializer implements ByteSerializer<Version> {
        /** 获取序列化程序的唯一实例。 */
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(Version version, OutputStream out) throws IOException {
            out.write(Convert.toBytes(version.major()));
            out.write(Convert.toBytes(version.minor()));
            out.write(Convert.toBytes(version.revision()));
        }

        @Override
        public Version deserialize(InputStream in) throws IOException {
            return Version.builder()
                    .major(Convert.toByte(IoUtils.read(in, 1)))
                    .minor(Convert.toByte(IoUtils.read(in, 1)))
                    .revision(Convert.toByte(IoUtils.read(in, 1)))
                    .build();
        }
    }
}
