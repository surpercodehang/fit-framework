/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.zipped.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.support.ZippedFileException;
import modelengine.fit.security.http.zipped.ZippedFileValidateConfig;
import modelengine.fit.security.http.zipped.ZippedFileValidator;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link ZippedFileValidator} 的各类型校验器聚合实现。
 * <p>当前仅支持对于 zip 和 jar 类型的压缩文件进行校验，保留该聚合实现的目的是。</p>
 *
 * @author 何天放
 * @since 2024-07-20
 */
public final class AggregatedZippedFileValidator implements ZippedFileValidator {
    /**
     * 表示 {@link ZippedFileValidator} 的各类型校验器聚合实现的实例。
     */
    public static final ZippedFileValidator INSTANCE = new AggregatedZippedFileValidator();

    private static final String ZIP_FILE_EXTENSION_NAME = ".zip";
    private static final String JAR_FILE_EXTENSION_NAME = ".jar";
    private static final Map<String, ZippedFileValidator> validators = MapBuilder.<String, ZippedFileValidator>get()
            .put(ZIP_FILE_EXTENSION_NAME, ZipOrJarTypeFileValidator.INSTANCE)
            .put(JAR_FILE_EXTENSION_NAME, ZipOrJarTypeFileValidator.INSTANCE)
            .build();

    private AggregatedZippedFileValidator() {}

    @Override
    public void validate(String filePath, String fileName, ZippedFileValidateConfig config)
            throws FitSecurityException {
        notNull(filePath, "The file path cannot be null.");
        notBlank(fileName, "The file name cannot be blank.");
        notNull(config, "The config for zipped file validate cannot be null.");
        String extensionName = FileUtils.extension(fileName);
        if (!validators.containsKey(extensionName)) {
            throw new ZippedFileException(StringUtils.format(
                    "Cannot validate zipped file as this type. [fileExtensionName={0}]",
                    extensionName));
        }
    }
}
