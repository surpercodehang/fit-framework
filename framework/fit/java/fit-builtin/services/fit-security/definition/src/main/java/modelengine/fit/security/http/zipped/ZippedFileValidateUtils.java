/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.zipped;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.zipped.support.AggregatedZippedFileValidator;

/**
 * 为文件校验提供工具方法。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class ZippedFileValidateUtils {
    private ZippedFileValidateUtils() {}

    /**
     * 对压缩文件进行校验。
     *
     * @param filePath 表示压缩文件路径的 {@link String}。
     * @param fileName 表示压缩文件名称的 {@link String}。
     * @param config 表示校验配置的 {@link ZippedFileValidateConfig}。
     * @throws FitSecurityException 当压缩文件校验失败时。
     */
    public static void validate(String filePath, String fileName, ZippedFileValidateConfig config)
            throws FitSecurityException {
        notNull(filePath, "The file path cannot be null.");
        notBlank(fileName, "The file name cannot be blank.");
        notNull(config, "The config for zipped file validate cannot be null.");
        AggregatedZippedFileValidator.INSTANCE.validate(filePath, fileName, config);
    }
}
