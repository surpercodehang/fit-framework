/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin;

import modelengine.fitframework.util.CodeableEnum;

/**
 * 为插件提供类别。
 *
 * @author 梁济时
 * @since 2022-06-06
 */
public enum PluginCategory implements CodeableEnum<PluginCategory> {
    /**
     * 表示系统插件。
     */
    SYSTEM(1, "system"),

    /**
     * 表示用户插件。
     */
    USER(2, "user");

    private final Integer id;
    private final String code;

    PluginCategory(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.getCode();
    }
}
