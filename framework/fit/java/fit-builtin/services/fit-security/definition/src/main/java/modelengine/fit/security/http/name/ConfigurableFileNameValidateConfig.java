/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.name;

import java.util.List;

/**
 * 表示可配置的文件上传校验配置。
 *
 * @author 何天放
 * @since 2024-07-18
 */
public interface ConfigurableFileNameValidateConfig extends FileNameValidateConfig {
    /**
     * 设置文件名格式。
     * <p>当该值为 null 或空字符串时，表示不进行文件名格式的校验。</p>
     *
     * @param fileNameFormat 表示文件名格式的 {@link String}。
     * @return 表示当前可配置的文件上传校验配置的 {@link ConfigurableFileNameValidateConfig}。
     */
    ConfigurableFileNameValidateConfig fileNameFormat(String fileNameFormat);

    /**
     * 设置文件名字符黑名单。
     *
     * @param blackList 表示文件名字符黑名单的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示当前可配置的文件上传校验配置的 {@link ConfigurableFileNameValidateConfig}。
     */
    ConfigurableFileNameValidateConfig blackList(List<String> blackList);

    /**
     * 设置扩展名白名单。
     *
     * @param extensionNameWhiteList 表示扩展名白名单的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示当前可配置的文件上传校验配置的 {@link ConfigurableFileNameValidateConfig}。
     */
    ConfigurableFileNameValidateConfig extensionNameWhiteList(List<String> extensionNameWhiteList);
}
