/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import modelengine.fitframework.plugin.maven.support.DefaultMavenCoordinate;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Optional;

/**
 * 为Maven归档件提供坐标。
 *
 * @author 梁济时
 * @since 2020-11-20
 */
public interface MavenCoordinate {
    /**
     * 获取Maven坐标的分组唯一标识。
     *
     * @return 表示分组唯一标识的 {@link String}。
     */
    String getGroupId();

    /**
     * 获取Maven坐标的归档件的唯一标识。
     *
     * @return 表示归档件唯一标识的 {@link String}。
     */
    String getArtifactId();

    /**
     * 获取Maven坐标的版本号。
     *
     * @return 表示版本号的 {@link String}。
     */
    String getVersion();

    /**
     * 为 {@link MavenCoordinate} 提供构建器。
     *
     * @author 梁济时
     * @since 2020-11-20
     */
    interface Builder {
        /**
         * 设置Maven坐标的分组唯一标识。
         *
         * @param groupId 表示分组唯一标识的 {@link String}。
         * @return 表示当前的构建者的 {@link Builder}。
         */
        Builder setGroupId(String groupId);

        /**
         * 设置Maven坐标的归档件的唯一标识。
         *
         * @param artifactId 表示归档件唯一标识的 {@link String}。
         * @return 表示当前的构建者的 {@link Builder}。
         */
        Builder setArtifactId(String artifactId);

        /**
         * 设置Maven坐标的版本号。
         *
         * @param version 表示版本号的 {@link String}。
         * @return 表示当前的构建者的 {@link Builder}。
         */
        Builder setVersion(String version);

        /**
         * 构建一个 {@link MavenCoordinate} 的默认实现。
         *
         * @return 表示Maven坐标的默认实现的 {@link MavenCoordinate}。
         */
        MavenCoordinate build();
    }

    /**
     * 返回一个构建器，用以构建 {@link MavenCoordinate} 的默认实现。
     *
     * @return 表示用以构建 {@link MavenCoordinate} 默认实现的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(null);
    }

    /**
     * 返回一个构建器，用以构建 {@link MavenCoordinate} 的默认实现。
     *
     * @param coordinate 表示作为初始数据的 {@link MavenCoordinate}。
     * @return 表示用以构建 {@link MavenCoordinate} 默认实现的构建器的 {@link Builder}。
     */
    static Builder builder(MavenCoordinate coordinate) {
        return new DefaultMavenCoordinate.Builder(coordinate);
    }

    /**
     * 返回一个构建器，以当前 {@link MavenCoordinate} 作为初始数据，以构建 {@link MavenCoordinate} 的默认实现。
     *
     * @return 表示用以构建 {@link MavenCoordinate} 默认实现的构建器的 {@link Builder}。
     */
    default Builder copy() {
        return builder(this);
    }

    /**
     * 从Maven项目中获取Maven坐标。
     *
     * @param project 表示Maven项目的 {@link Artifact}。
     * @return 表示Maven坐标的 {@link MavenCoordinate}。
     */
    static Optional<MavenCoordinate> of(MavenProject project) {
        return Optional.ofNullable(project).map(MavenProject::getArtifact).flatMap(MavenCoordinate::of);
    }

    /**
     * 从Maven归档件中获取Maven坐标。
     *
     * @param artifact 表示Maven归档件的 {@link Artifact}。
     * @return 表示Maven坐标的 {@link MavenCoordinate}。
     */
    static Optional<MavenCoordinate> of(Artifact artifact) {
        return Optional.ofNullable(artifact).map(MavenCoordinate::buildCoordinate);
    }

    /**
     * 从Jar归档件中获取Maven坐标。
     *
     * @param jarFile 表示Maven归档件的 {@link File}。
     * @return 表示Maven坐标的 {@link MavenCoordinate}。
     */
    static Optional<MavenCoordinate> of(File jarFile) {
        return Optional.ofNullable(jarFile).map(JarMavenCoordinateResolver::resolve);
    }

    /**
     * 从给定的 Maven 归档件构建一个 Maven 坐标。
     *
     * @param artifact 表示 Maven 归档件的 {@link Artifact}。
     * @return 表示 Maven 坐标的 {@link MavenCoordinate}。
     */
    static MavenCoordinate buildCoordinate(Artifact artifact) {
        return MavenCoordinate.builder()
                .setGroupId(artifact.getGroupId())
                .setArtifactId(artifact.getArtifactId())
                .setVersion(artifact.getBaseVersion())
                .build();
    }
}
