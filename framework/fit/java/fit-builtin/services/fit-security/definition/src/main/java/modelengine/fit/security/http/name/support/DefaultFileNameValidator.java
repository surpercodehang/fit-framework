/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.name.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.name.FileNameValidateConfig;
import modelengine.fit.security.http.name.FileNameValidator;
import modelengine.fit.security.http.support.FileNameException;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;

/**
 * 表示 {@link FileNameValidator} 的默认实现。
 *
 * @author 何天放
 * @since 2024-07-18
 */
public final class DefaultFileNameValidator implements FileNameValidator {
    /**
     * 表示 {@link FileNameValidator} 默认实现的实例。
     */
    public static final FileNameValidator INSTANCE = new DefaultFileNameValidator();

    private DefaultFileNameValidator() {}

    @Override
    public void validate(String processedFileName, FileNameValidateConfig config) throws FitSecurityException {
        notNull(processedFileName, "The processed file name cannot be null.");
        notNull(config, "The config for file name validate cannot be null.");
        if (processedFileName.isEmpty()) {
            throw new FileNameException("The file name is blank.");
        }
        if (config.blackList() != null && this.containsBlackList(processedFileName, config.blackList())) {
            throw new FileNameException("The file name contains illegal string.");
        }
        if (!StringUtils.isBlank(config.fileNameFormat()) && !processedFileName.matches(config.fileNameFormat())) {
            throw new FileNameException("The file name does not match the format.");
        }
        String extensionName = FileUtils.extension(processedFileName);
        boolean extensionNameCheckResult = this.checkFileExtensionName(extensionName, config);
        if (!extensionNameCheckResult) {
            throw new FileNameException(StringUtils.format("The file extension name is illegal. [extensionName={0}]",
                    extensionName));
        }
    }

    private boolean checkFileExtensionName(String fileExtensionName, FileNameValidateConfig config) {
        if (config.extensionNameWhiteList() == null || config.extensionNameWhiteList().isEmpty()) {
            return true;
        }
        return config.extensionNameWhiteList().stream().anyMatch(fileExtensionName::equals);
    }

    private boolean containsBlackList(String fileName, List<String> blackList) {
        if (blackList == null) {
            return false;
        }
        return blackList.stream().anyMatch(fileName::contains);
    }
}
