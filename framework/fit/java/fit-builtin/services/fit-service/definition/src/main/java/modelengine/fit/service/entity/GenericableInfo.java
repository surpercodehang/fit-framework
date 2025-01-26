/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.entity;

/**
 * 表示服务信息。
 *
 * @author 季聿阶
 * @since 2023-05-17
 */
public class GenericableInfo {
    private String genericableId;
    private String genericableVersion;

    /**
     * 获取服务的唯一标识。
     *
     * @return 表示服务唯一标识的 {@link String}。
     */
    public String getGenericableId() {
        return this.genericableId;
    }

    /**
     * 设置服务的唯一标识。
     *
     * @param genericableId 表示待设置的服务唯一标识的 {@link String}。
     */
    public void setGenericableId(String genericableId) {
        this.genericableId = genericableId;
    }

    /**
     * 获取服务的版本号。
     *
     * @return 表示服务的版本号的 {@link String}。
     */
    public String getGenericableVersion() {
        return this.genericableVersion;
    }

    /**
     * 设置服务的版本号。
     *
     * @param genericableVersion 表示服务的版本号的 {@link String}。
     */
    public void setGenericableVersion(String genericableVersion) {
        this.genericableVersion = genericableVersion;
    }
}
