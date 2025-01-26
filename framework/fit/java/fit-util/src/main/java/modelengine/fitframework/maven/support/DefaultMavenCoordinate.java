/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.maven.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.maven.MavenCoordinate;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link MavenCoordinate} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-10-18
 */
public final class DefaultMavenCoordinate implements MavenCoordinate {
    private static final String EMPTY_VALUE = "UNKNOWN";
    private static final char SEPARATOR = ':';

    private final String groupId;
    private final String artifactId;
    private final String version;

    /**
     * 使用分组标识、制成件标识及版本信息初始化 {@link DefaultMavenCoordinate} 类的新实例。
     *
     * @param groupId 表示分组标识的 {@link String}。
     * @param artifactId 表示制成件标识的 {@link String}。
     * @param version 表示版本信息的 {@link String}。
     */
    public DefaultMavenCoordinate(String groupId, String artifactId, String version) {
        this.groupId = canonicalize(groupId);
        this.artifactId = canonicalize(artifactId);
        this.version = canonicalize(version);
    }

    private static String canonicalize(String value) {
        String actual = StringUtils.trim(value);
        if (StringUtils.isEmpty(actual)) {
            return EMPTY_VALUE;
        } else {
            return actual;
        }
    }

    @Override
    public String groupId() {
        return this.groupId;
    }

    @Override
    public String artifactId() {
        return this.artifactId;
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultMavenCoordinate another = (DefaultMavenCoordinate) obj;
            return Objects.equals(this.groupId(), another.groupId()) && Objects.equals(this.artifactId(),
                    another.artifactId()) && Objects.equals(this.version(), another.version());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.groupId(), this.artifactId(), this.version()});
    }

    @Override
    public String toString() {
        return StringUtils.join(SEPARATOR, this.groupId(), this.artifactId(), this.version());
    }

    /**
     * 从指定字符串中解析 Maven 坐标信息。
     *
     * @param text 表示包含 Maven 坐标信息的字符串的 {@link String}。
     * @return 表示从字符串中解析到的 Maven 坐标的 {@link DefaultMavenCoordinate}。
     * @throws IllegalArgumentException {@code text} 为 {@code null} 或格式不正确。
     */
    public static DefaultMavenCoordinate parse(String text) {
        notNull(text, "The text to parse maven coordinate cannot be null.");
        String[] parts = StringUtils.split(text, SEPARATOR);
        if (parts.length != 3) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The text to parse maven coordinate must contain 3 parts. [text={0}, separator={1}]",
                    text,
                    SEPARATOR));
        }
        return new DefaultMavenCoordinate(parts[0], parts[1], parts[2]);
    }
}
