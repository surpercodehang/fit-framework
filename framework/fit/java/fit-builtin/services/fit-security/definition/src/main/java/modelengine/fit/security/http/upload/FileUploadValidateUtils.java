/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.upload;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.upload.support.AggregatedFileUploadValidator;

/**
 * 为文件上传校验提供工具方法。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class FileUploadValidateUtils {
    private FileUploadValidateUtils() {}

    /**
     * 对文件进行校验。
     *
     * @param entity 表示文件的 {@link FileEntity}。
     * @param config 表示校验配置的 {@link FileUploadValidateConfig}。
     * @throws FitSecurityException 当文件上传校验未通过时。
     */
    public static void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        AggregatedFileUploadValidator.INSTANCE.validate(entity, config);
    }

    /**
     * 对文件进行校验。
     *
     * @param entity 表示文件的 {@link FileEntity}。
     * @param config 表示校验配置的 {@link FileUploadValidateConfig}。
     * @param validator 表示用户自定义文件校验器的 {@link FileUploadValidator}。
     * @throws FitSecurityException 当文件上传校验未通过时。
     */
    public static void validate(FileEntity entity, FileUploadValidateConfig config, FileUploadValidator validator)
            throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        notNull(validator, "The file validator cannot be null.");
        AggregatedFileUploadValidator.INSTANCE.validate(entity, config);
        validator.validate(entity, config);
    }
}
