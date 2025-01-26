/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.zipped;

import modelengine.fit.security.http.FitSecurityException;

/**
 * 表示压缩文件校验器。
 *
 * @author 何天放
 * @since 2024-07-11
 */
public interface ZippedFileValidator {
    /**
     * 对压缩文件进行校验。
     *
     * @param filePath 表示压缩文件路径的 {@link String}。
     * @param fileName 表示压缩文件名称的 {@link String}。
     * @param config 表示校验配置的 {@link ZippedFileValidateConfig}。
     * @throws FitSecurityException 当压缩文件校验失败时。
     */
    void validate(String filePath, String fileName, ZippedFileValidateConfig config) throws FitSecurityException;
}
