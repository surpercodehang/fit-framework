/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.upload.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.support.FileSavePathException;
import modelengine.fit.security.http.upload.FileUploadValidateConfig;
import modelengine.fit.security.http.upload.FileUploadValidator;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 表示 {@link FileUploadValidator} 的跨路径安全性校验功能实现。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class SavePathUploadValidator implements FileUploadValidator {
    /**
     * 表示 {@link FileUploadValidator} 的跨路径安全性校验功能实现的实例。
     */
    public static final FileUploadValidator INSTANCE = new SavePathUploadValidator();

    private SavePathUploadValidator() {}

    @Override
    public void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        String fileSavePath = config.fileSavePath();
        if (StringUtils.isBlank(fileSavePath)) {
            return;
        }
        String canonicalPathOfFile;
        try {
            canonicalPathOfFile = new File(fileSavePath, entity.filename()).getCanonicalPath();
        } catch (IOException ex) {
            throw new FileSavePathException("Cannot get canonical path of file.");
        }
        String canonicalPathOfSavePath;
        try {
            canonicalPathOfSavePath = new File(fileSavePath).getCanonicalPath();
        } catch (IOException ex) {
            throw new FileSavePathException("Cannot get canonical path of file save path.");
        }
        if (!canonicalPathOfFile.startsWith(canonicalPathOfSavePath)) {
            throw new FileSavePathException(StringUtils.format(
                    "Path of file is not start with path of save path. [canonicalPathOfFile={0}, "
                            + "canonicalPathOfSavePath={1}]",
                    canonicalPathOfFile,
                    canonicalPathOfSavePath));
        }
    }
}
