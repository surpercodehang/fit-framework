/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import java.util.StringJoiner;

/**
 * 为Maven工程提供坐标。
 *
 * @author 梁济时
 * @since 2020-10-09
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class MavenCoordinate {
    private final String groupId;
    private final String artifactId;
    private final String version;

    @Override
    public String toString() {
        return new StringJoiner(":").add(this.getGroupId()).add(this.getArtifactId()).add(this.getVersion()).toString();
    }

    /**
     * 传入maven的Artifact实体创建一个新的maven坐标实体。
     *
     * @param artifact 表示maven的 {@link Artifact} 实体。
     * @return 表示maven坐标的实体 {@link MavenCoordinate}。
     */
    public static MavenCoordinate create(Artifact artifact) {
        return create(artifact.getGroupId(), artifact.getArtifactId(), artifact.getBaseVersion());
    }

    /**
     * 传入maven的MavenProject实体创建一个新的maven坐标实体。
     *
     * @param project 表示maven的 {@link MavenProject} 实体。
     * @return 表示maven坐标的实体 {@link MavenCoordinate}。
     */
    public static MavenCoordinate create(MavenProject project) {
        return create(project.getGroupId(), project.getArtifactId(), project.getVersion());
    }

    /**
     * 传入maven的Dependency实体创建一个新的maven坐标实体。
     *
     * @param dependency 表示maven的 {@link Dependency} 实体。
     * @return 表示maven坐标的实体 {@link MavenCoordinate}。
     */
    public static MavenCoordinate create(Dependency dependency) {
        return create(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
    }

    /**
     * 传入groupId、artifactId、version创建一个新的maven坐标实体。
     *
     * @param groupId 表示jar包所属的项目组织 {@link String}。
     * @param artifactId 表示jar包所属项目 {@link String}。
     * @param version 表示jar包的版本 {@link String}。
     * @return 表示maven坐标的实体 {@link MavenCoordinate}。
     */
    public static MavenCoordinate create(String groupId, String artifactId, String version) {
        return new MavenCoordinate(groupId, artifactId, version);
    }
}
