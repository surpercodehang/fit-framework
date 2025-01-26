/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.upload;

import modelengine.fit.security.http.name.FileNameValidateConfig;

/**
 * 表示可配置的文件上传校验配置。
 *
 * @author 何天放
 * @since 2024-07-10
 */
public interface ConfigurableFileUploadValidateConfig extends FileUploadValidateConfig {
    /**
     * 设置文件名校验配置。
     *
     * @param fileNameValidateConfig 表示文件名校验配置的 {@link FileNameValidateConfig}。
     * @return 表示当前可配置的文件上传校验配置的 {@link ConfigurableFileUploadValidateConfig}。
     */
    ConfigurableFileUploadValidateConfig fileNameValidateConfig(FileNameValidateConfig fileNameValidateConfig);

    /**
     * 设置文件大小限制。
     * <p>当该值为负数时，表示不进行文件大小的校验。</p>
     *
     * @param fileSize 表示文件大小限制的 {@code long}。
     * @return 表示当前可配置的文件上传校验配置的 {@link ConfigurableFileUploadValidateConfig}。
     */
    ConfigurableFileUploadValidateConfig fileSizeLimit(long fileSize);

    /**
     * 设置文件存放路径。
     * <p>当该值为 null 或空字符串时，表示不进行文件上传路径相关校验，也无法进行压缩文件相关校验。</p>
     *
     * @param fileSaveDir 表示文件存放路径的 {@link String}。
     * @return 表示当前可配置的文件上传校验配置的 {@link ConfigurableFileUploadValidateConfig}。
     */
    ConfigurableFileUploadValidateConfig fileSavePath(String fileSaveDir);

    /**
     * 设置文件存放路径中文件与目录数量之和的限制值。
     * <p>当该值为负时表示不进行文件与目录数量之和的校验</p>
     *
     * @param limit 表示文件存放路径中文件及目录数量之和的限制值的 {@code long}。
     * @return 表示当前可配置的文件上传校验配置的 {@link ConfigurableFileUploadValidateConfig}。
     */
    ConfigurableFileUploadValidateConfig fileSavePathFileCountLimit(long limit);

    /**
     * 设置文件存放路径剩余空间限制值。
     * <p>当该值为负时表示不进行存放路径剩余空间的校验。</p>
     *
     * @param limit 表示文件存放路径剩余空间限制值的 {@code long}。
     * @return 表示当前可配置的文件上传校验配置的 {@link ConfigurableFileUploadValidateConfig}。
     */
    ConfigurableFileUploadValidateConfig fileSavePathRestSpaceLimit(long limit);
}
