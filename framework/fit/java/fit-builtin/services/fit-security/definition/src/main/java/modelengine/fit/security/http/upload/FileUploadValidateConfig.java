/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.upload;

import modelengine.fit.security.http.name.FileNameValidateConfig;

/**
 * 表示文件上传校验的配置。
 *
 * @author 何天放
 * @since 2024-07-10
 */
public interface FileUploadValidateConfig {
    /**
     * 获取文件名校验配置。
     *
     * @return 表示文件名校验配置的 {@link FileNameValidateConfig}。
     */
    FileNameValidateConfig fileNameValidateConfig();

    /**
     * 获取文件大小限制。
     * <p>当该值小于等于 0 时，表示不进行文件大小的校验。</p>
     *
     * @return 表示文件大小限制的 {@code long}。
     */
    long fileSizeLimit();

    /**
     * 获取文件存放路径。
     * <p>当该值为 null 或空字符串时，表示不进行文件上传路径文件数量、剩余空间校验以及跨路径校验。</p>
     *
     * @return 表示文件存放路径的 {@link String}。
     */
    String fileSavePath();

    /**
     * 获取文件存放路径中文件与目录数量之和的限制值。
     * <p>当该值小于等于 0 时表示不进行文件与目录数量之和的校验</p>
     *
     * @return 表示文件存放路径中文件及目录数量之和的限制值的 {@code long}。
     */
    long fileSavePathFileCountLimit();

    /**
     * 获取文件存放路径剩余空间限制值。
     * <p>当该值小于等于 0 时表示不进行存放路径剩余空间的校验。</p>
     *
     * @return 表示文件存放路径剩余空间限制值的 {@code long}。
     */
    long fileSavePathRestSpaceLimit();
}
