/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.entity;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Objects;

/**
 * 表示服务实现信息。
 *
 * @author 季聿阶
 * @since 2023-05-06
 */
public class FitableInfo {
    private String genericableId;
    private String genericableVersion;
    private String fitableId;
    private String fitableVersion;

    /**
     * 表示服务实现所属的服务的唯一标识。
     *
     * @return 表示服务实现所属的服务的唯一标识的 {@link String}。
     */
    public String getGenericableId() {
        return this.genericableId;
    }

    /**
     * 设置服务实现所属的服务的唯一标识。
     *
     * @param genericableId 表示服务实现所属的服务的唯一标识的 {@link String}。
     */
    public void setGenericableId(String genericableId) {
        this.genericableId = genericableId;
    }

    /**
     * 获取服务实现所属的服务的版本号。
     *
     * @return 表示服务实现所属的服务的版本号的 {@link String}。
     */

    public String getGenericableVersion() {
        return this.genericableVersion;
    }

    /**
     * 设置服务实现所属的服务的版本号。
     *
     * @param genericableVersion 表示服务实现所属的服务的版本号的 {@link String}。
     */
    public void setGenericableVersion(String genericableVersion) {
        this.genericableVersion = genericableVersion;
    }

    /**
     * 获取服务实现的唯一标识。
     *
     * @return 表示服务实现的唯一标识的 {@link String}。
     */
    public String getFitableId() {
        return this.fitableId;
    }

    /**
     * 设置服务实现的唯一标识。
     *
     * @param fitableId 表示服务实现的唯一标识的 {@link String}。
     */
    public void setFitableId(String fitableId) {
        this.fitableId = fitableId;
    }

    /**
     * 获取服务实现的版本号。
     *
     * @return 表示服务实现的版本号的 {@link String}。
     */
    public String getFitableVersion() {
        return this.fitableVersion;
    }

    /**
     * 设置服务实现的版本号。
     *
     * @param fitableVersion 表示服务实现的版本号的 {@link String}。
     */
    public void setFitableVersion(String fitableVersion) {
        this.fitableVersion = fitableVersion;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        FitableInfo that = ObjectUtils.cast(another);
        return Objects.equals(this.genericableId, that.genericableId) && Objects.equals(this.genericableVersion,
                that.genericableVersion) && Objects.equals(this.fitableId, that.fitableId)
                && Objects.equals(this.fitableVersion, that.fitableVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.genericableId, this.genericableVersion, this.fitableId, this.fitableVersion);
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "/{\"genericableId\": \"{0}\", \"genericableVersion\": \"{1}\", \"fitableId\": \"{2}\", "
                        + "\"fitableVersion\": \"{3}\"/}",
                this.getGenericableId(),
                this.getGenericableVersion(),
                this.getFitableId(),
                this.getFitableVersion());
    }
}
