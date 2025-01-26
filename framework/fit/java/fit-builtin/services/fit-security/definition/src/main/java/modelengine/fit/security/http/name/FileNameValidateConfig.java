/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.name;

import java.util.List;

/**
 * 表示文件名校验的配置。
 *
 * @author 何天放
 * @since 2024-07-18
 */
public interface FileNameValidateConfig {
    /**
     * 获取文件名格式。
     * <p>当该值为 null 或空字符串时，表示不进行文件名格式的校验。</p>
     *
     * @return 表示文件名格式的 {@link String}。
     */
    String fileNameFormat();

    /**
     * 获取文件名字符黑名单。
     * <p>当该值为 null 或空列表时，表示不进行文件名字符黑名单校验。</p>
     *
     * @return 表示文件名字符黑名单的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> blackList();

    /**
     * 获取扩展名白名单。
     * <p>当该值为 null 或空列表时，表示不进行扩展名校验。</p>
     *
     * @return 表示扩展名白名单的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> extensionNameWhiteList();
}
