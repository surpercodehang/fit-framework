/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.service.entity;

import static modelengine.fitframework.util.ObjectUtils.cast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 表示应用信息。
 *
 * @author 季聿阶
 * @since 2023-05-06
 */
public class Application {
    private String name;
    private String nameVersion;
    private Map<String, String> extensions = new HashMap<>();

    /**
     * 获取应用名。
     *
     * @return 表示应用名的 {@link String}。
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置应用名。
     *
     * @param name 表示应用名的 {@link String}。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取应用名的版本。
     *
     * @return 表示应用名的版本的 {@link String}。
     */
    public String getNameVersion() {
        return this.nameVersion;
    }

    /**
     * 设置应用名的版本。
     *
     * @param nameVersion 表示应用名的版本的 {@link String}。
     */
    public void setNameVersion(String nameVersion) {
        this.nameVersion = nameVersion;
    }

    /**
     * 获取应用的扩展信息。
     *
     * @return 表示应用扩展信息的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public Map<String, String> getExtensions() {
        return this.extensions;
    }

    /**
     * 设置应用的扩展信息。
     *
     * @param extensions 表示需要设置的扩展信息的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    public void setExtensions(Map<String, String> extensions) {
        if (extensions != null) {
            this.extensions = extensions;
        }
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        Application that = cast(another);
        return Objects.equals(this.nameVersion, that.nameVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nameVersion);
    }
}
