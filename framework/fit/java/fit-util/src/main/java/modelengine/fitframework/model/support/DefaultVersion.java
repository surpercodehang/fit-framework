/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.model.Version;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Comparator;
import java.util.Objects;

/**
 * 为 {@link Version} 提供默认实现。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2021-10-11
 */
public class DefaultVersion implements Version {
    /**
     * 为版本提供默认的比较程序。
     */
    public static final Comparator<Version> COMPARATOR = Comparator.comparingInt(Version::major)
            .thenComparingInt(Version::minor)
            .thenComparingInt(Version::revision)
            .thenComparingInt(Version::build);

    /** 表示版本号间的分隔符。 */
    private static final char SEPARATOR_NUMBER = '.';

    /** 表示版本阶段前的分隔符。 */
    private static final char SEPARATOR_STAGE = '-';

    private final int major;
    private final int minor;
    private final int revision;
    private final int build;
    private final String stage;

    /**
     * 使用主版本号、次版本号、修订版本号、构建版本号和阶段初始化 {@link DefaultVersion} 类的新实例。
     *
     * @param major 表示主版本号的 {@code int}。
     * @param minor 表示次版本号的 {@code int}。
     * @param revision 表示修订版本号的 {@code int}。
     * @param build 表示构建版本号的 {@code int}。
     * @param stage 表示版本阶段的 {@link String}。
     * @throws IllegalArgumentException 当 {@code major}、{@code minor}、{@code revision} 或 {@code build} 为负数时。
     */
    public DefaultVersion(int major, int minor, int revision, int build, String stage) {
        this.major =
                Validation.greaterThanOrEquals(major, 0, "The major version cannot be negative. [major={0}]", major);
        this.minor =
                Validation.greaterThanOrEquals(minor, 0, "The minor version cannot be negative. [minor={0}]", minor);
        this.revision = Validation.greaterThanOrEquals(revision,
                0,
                "The revision version cannot be negative. [revision={0}]",
                revision);
        this.build =
                Validation.greaterThanOrEquals(build, 0, "The build version cannot be negative. [build={0}]", build);
        this.stage = stage;
    }

    @Override
    public int major() {
        return this.major;
    }

    @Override
    public int minor() {
        return this.minor;
    }

    @Override
    public int revision() {
        return this.revision;
    }

    @Override
    public int build() {
        return this.build;
    }

    @Override
    public String stage() {
        return this.stage;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.major())
                .append(SEPARATOR_NUMBER)
                .append(this.minor)
                .append(SEPARATOR_NUMBER)
                .append(this.revision());
        if (this.build() > 0) {
            builder.append(SEPARATOR_NUMBER).append(this.build());
        }
        if (StringUtils.isNotBlank(this.stage)) {
            builder.append(SEPARATOR_STAGE).append(this.stage);
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass() && this.equals(ObjectUtils.cast(obj));
    }

    private boolean equals(DefaultVersion another) {
        return this.major == another.major && this.minor == another.minor && this.revision == another.revision
                && this.build == another.build && Objects.equals(this.stage, another.stage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.major, this.minor, this.revision, this.build, this.stage);
    }

    /**
     * 从字符串中解析版本信息。
     *
     * @param value 表示包含版本信息的字符串的 {@link String}。
     * @return 表示从字符串中解析的版本信息的 {@link Version}。
     * @throws IllegalArgumentException {@code value} 未包含有效的版本信息。
     */
    public static Version parse(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        int index = value.indexOf(SEPARATOR_STAGE);
        String number = index > -1 ? value.substring(0, index) : value;
        String parsedStage = index > -1 ? value.substring(index + 1) : StringUtils.EMPTY;
        String[] parts = StringUtils.split(number, SEPARATOR_NUMBER);
        if (parts.length < 2) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The value to parse does not contains a valid version. [value={0}]",
                    value));
        } else {
            return new DefaultVersion(part(parts, 0, "major"),
                    part(parts, 1, "minor"),
                    part(parts, 2, "revision"),
                    part(parts, 3, "build"),
                    parsedStage);
        }
    }

    private static int part(String[] parts, int index, String key) {
        if (index >= parts.length) {
            return 0;
        }
        try {
            return Integer.parseUnsignedInt(parts[index]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(StringUtils.format("The {0} version is invalid. [actual={1}]",
                    key,
                    parts[index]), ex);
        }
    }
}
