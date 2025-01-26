/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin;

/**
 * 当插件重复时引发的异常。
 *
 * @author 梁济时
 * @since 2022-06-29
 */
public class DuplicatePluginException extends RuntimeException {
    /**
     * 使用异常信息初始化 {@link DuplicatePluginException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public DuplicatePluginException(String message) {
        super(message);
    }
}
