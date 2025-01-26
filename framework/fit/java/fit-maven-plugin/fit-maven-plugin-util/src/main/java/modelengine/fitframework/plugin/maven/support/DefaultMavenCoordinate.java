/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import modelengine.fitframework.plugin.maven.MavenCoordinate;

import java.util.Arrays;

/**
 * The DefaultMavenCoordinate
 *
 * @author 陈镕希
 * @since 2020/12/26
 */
@Getter
@AllArgsConstructor
public class DefaultMavenCoordinate implements MavenCoordinate {
    private final String groupId;
    private final String artifactId;
    private final String version;

    @Override
    public String toString() {
        return this.getGroupId() + ':' + this.getArtifactId() + ':' + this.getVersion();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof MavenCoordinate) {
            MavenCoordinate another = (MavenCoordinate) obj;
            return another.getGroupId().equals(this.getGroupId()) && another.getArtifactId()
                    .equals(this.getArtifactId()) && another.getVersion().equals(this.getVersion());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.getGroupId(), this.getArtifactId(), this.getVersion()
        });
    }

    /**
     * {@link MavenCoordinate.Builder} 的具体实现。
     */
    public static class Builder implements MavenCoordinate.Builder {
        private String groupId;
        private String artifactId;
        private String version;

        /**
         * 创建一个 {@link Builder} 实例，根据已有的 Maven 坐标进行初始化。
         *
         * @param coordinate 表示已有的 Maven 坐标的 {@link MavenCoordinate}。
         */
        public Builder(MavenCoordinate coordinate) {
            if (coordinate != null) {
                this.groupId = coordinate.getGroupId();
                this.artifactId = coordinate.getArtifactId();
                this.version = coordinate.getVersion();
            }
        }

        @Override
        public Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        @Override
        public Builder setArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        @Override
        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        @Override
        public MavenCoordinate build() {
            return new DefaultMavenCoordinate(this.groupId, this.artifactId, this.version);
        }
    }
}
