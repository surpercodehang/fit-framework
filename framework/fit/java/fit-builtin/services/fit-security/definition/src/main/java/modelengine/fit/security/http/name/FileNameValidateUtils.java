/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.name;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.name.support.DefaultFileNameValidator;

/**
 * 为文件名校验提供工具方法。
 *
 * @author 何天放
 * @since 2024-07-18
 */
public final class FileNameValidateUtils {
    private FileNameValidateUtils() {}

    /**
     * 对文件名进行校验。
     *
     * @param processedFileName 表示经过预处理后的文件名的 {@link String}。
     * @param config 表示文件名校验配置的 {@link FileNameValidateConfig}。
     * @throws FitSecurityException 当文件名校验未通过时。
     */
    public static void validate(String processedFileName, FileNameValidateConfig config) throws FitSecurityException {
        notNull(processedFileName, "The processed file name cannot be null.");
        notNull(config, "The config for file name validate cannot be null.");
        DefaultFileNameValidator.INSTANCE.validate(processedFileName, config);
    }
}
