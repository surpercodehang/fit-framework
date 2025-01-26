/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.upload.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.support.FileSizeException;
import modelengine.fit.security.http.upload.FileUploadValidateConfig;
import modelengine.fit.security.http.upload.FileUploadValidator;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示 {@link FileUploadValidator} 的文件大小校验功能实现。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class FileSizeUploadValidator implements FileUploadValidator {
    /**
     * 表示 {@link FileUploadValidator} 的文件大小校验功能实现的实例。
     */
    public static final FileUploadValidator INSTANCE = new FileSizeUploadValidator();

    private FileSizeUploadValidator() {}

    @Override
    public void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        if (config.fileSizeLimit() <= 0) {
            return;
        }
        long fileSize = entity.length();
        // 剩余的内存空间。
        long freeMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime()
                .freeMemory();
        if (fileSize > config.fileSizeLimit()) {
            throw new FileSizeException(StringUtils.format(
                    "File size error, file is too big. [fileSize={0}, configuredFileSize={1}]",
                    fileSize,
                    config.fileSizeLimit()));
        }
        if (fileSize > freeMemory) {
            throw new FileSizeException(StringUtils.format(
                    "File size error, free memory is not enough. [fileSize={0}, freeMemory={1}]",
                    fileSize,
                    freeMemory));
        }
    }
}
