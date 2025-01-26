/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.upload.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.name.FileNameValidateUtils;
import modelengine.fit.security.http.upload.FileUploadValidateConfig;
import modelengine.fit.security.http.upload.FileUploadValidator;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link FileUploadValidator} 的各校验功能聚合实现。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class AggregatedFileUploadValidator implements FileUploadValidator {
    /**
     * 表示 {@link FileUploadValidator} 的各校验功能聚合实现的实例。
     */
    public static final FileUploadValidator INSTANCE = new AggregatedFileUploadValidator();

    private static final List<FileUploadValidator> validators = Arrays.asList(FileNameUploadValidatorAdapter.INSTANCE,
            FileSizeUploadValidator.INSTANCE,
            SavePathUploadValidator.INSTANCE,
            UploadPathValidator.INSTANCE);

    private AggregatedFileUploadValidator() {}

    @Override
    public void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
        notNull(entity, "The file entity cannot be null.");
        notNull(config, "The config for file upload validate cannot be null.");
        for (FileUploadValidator validator : validators) {
            validator.validate(entity, config);
        }
    }

    private static final class FileNameUploadValidatorAdapter implements FileUploadValidator {
        static final FileUploadValidator INSTANCE = new FileNameUploadValidatorAdapter();

        private FileNameUploadValidatorAdapter() {}

        @Override
        public void validate(FileEntity entity, FileUploadValidateConfig config) throws FitSecurityException {
            FileNameValidateUtils.validate(entity.filename(), config.fileNameValidateConfig());
        }
    }
}
