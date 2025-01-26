/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.model.support.DefaultVersion;
import modelengine.fitframework.util.StringUtils;

/**
 * 为应用程序提供版本信息的定义。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2021-10-11
 */
public interface Version extends Comparable<Version> {
    /**
     * 获取主版本号。
     *
     * @return 表示主版本号的 {@code int}。
     */
    int major();

    /**
     * 获取次版本号。
     *
     * @return 表示次版本号的 {@code int}。
     */
    int minor();

    /**
     * 获取修订版本号。
     *
     * @return 表示修订版本号的 {@code int}。
     */
    int revision();

    /**
     * 获取构建版本号。
     *
     * @return 表示构建版本号的 {@code int}。
     */
    int build();

    /**
     * 获取阶段名称。
     * <p>例如：Alpha、Beta、RC、SNAPSHOT 等。</p>
     *
     * @return 表示阶段名称的 {@link String}。
     */
    String stage();

    @Override
    default int compareTo(@Nonnull Version another) {
        return DefaultVersion.COMPARATOR.compare(this, another);
    }

    /**
     * 构建版本实例。
     *
     * @param major 表示主版本号的 {@code int}。
     * @param minor 表示次版本号的 {@code int}。
     * @return 表示版本信息实例的 {@link Version}。
     * @throws IllegalArgumentException 当 {@code major} 或 {@code minor} 为负数时。
     */
    static Version create(int major, int minor) {
        return create(major, minor, 0, 0);
    }

    /**
     * 构建版本实例。
     *
     * @param major 表示主版本号的 {@code int}。
     * @param minor 表示次版本号的 {@code int}。
     * @param revision 表示修订版本号的 {@code int}。
     * @return 表示版本信息实例的 {@link Version}。
     * @throws IllegalArgumentException 当 {@code major}、{@code minor} 或 {@code revision} 为负数时。
     */
    static Version create(int major, int minor, int revision) {
        return create(major, minor, revision, 0);
    }

    /**
     * 构建版本实例。
     *
     * @param major 表示主版本号的 {@code int}。
     * @param minor 表示次版本号的 {@code int}。
     * @param revision 表示修订版本号的 {@code int}。
     * @param build 表示构建版本号的 {@code int}。
     * @return 表示版本信息实例的 {@link Version}。
     * @throws IllegalArgumentException 当 {@code major}、{@code minor}、{@code revision} 或 {@code build} 为负数时。
     */
    static Version create(int major, int minor, int revision, int build) {
        return new DefaultVersion(major, minor, revision, build, StringUtils.EMPTY);
    }

    /**
     * 构建版本实例。
     *
     * @param major 表示主版本号的 {@code int}。
     * @param minor 表示次版本号的 {@code int}。
     * @param revision 表示修订版本号的 {@code int}。
     * @param build 表示构建版本号的 {@code int}。
     * @param stage 表示版本阶段的 {@link String}。
     * @return 表示版本信息实例的 {@link Version}。
     * @throws IllegalArgumentException 当 {@code major}、{@code minor}、{@code revision} 或 {@code build} 为负数时。
     */
    static Version create(int major, int minor, int revision, int build, String stage) {
        return new DefaultVersion(major, minor, revision, build, stage);
    }

    /**
     * 从字符串中解析版本信息。
     *
     * @param value 表示包含版本信息的字符串的 {@link String}。
     * @return 表示从字符串中解析的版本信息的 {@link Version}。
     * @throws IllegalArgumentException 当 {@code value} 未包含有效的版本信息时。
     */
    static Version parse(String value) {
        return DefaultVersion.parse(value);
    }
}
