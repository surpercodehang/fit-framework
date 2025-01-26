/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.zipped;

import modelengine.fit.security.http.name.FileNameValidateConfig;

/**
 * 表示压缩文件校验的配置。
 *
 * @author 何天放
 * @since 2024-07-10
 */
public interface ZippedFileValidateConfig {
    /**
     * 获取压缩文件中各文件名校验配置。
     *
     * @return 表示文件名校验配置的 {@link FileNameValidateConfig}。
     */
    FileNameValidateConfig fileNameValidateConfig();

    /**
     * 获取压缩文件最多允许的子目录数量。
     * <p>当该值小于等于 0 时，表示不进行子目录数量的校验。</p>
     *
     * @return 表示最多允许的子目录数量的 {@code long}。
     */
    long zippedFileEntryCountLimit();

    /**
     * 获取压缩文件总大小限制。
     * <p>当该值小于等于 0 时，表示不进行总大小限制的校验。</p>
     *
     * @return 表示总大小限制的 {@code long}。
     */
    long zippedFileTotalSizeLimit();
}
