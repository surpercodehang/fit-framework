/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.zipped.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.name.FileNameValidateUtils;
import modelengine.fit.security.http.support.ZippedFileException;
import modelengine.fit.security.http.zipped.ZippedFileValidateConfig;
import modelengine.fit.security.http.zipped.ZippedFileValidator;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 表示 {@link ZippedFileValidator} 的 zip 类型或 jar 类型压缩文件校验功能实现。
 *
 * @author 何天放
 * @since 2024-07-12
 */
public final class ZipOrJarTypeFileValidator implements ZippedFileValidator {
    /**
     * 表示 {@link ZippedFileValidator} 的 zip 类型或 jar 类型压缩文件校验功能实现的实例。
     */
    public static final ZippedFileValidator INSTANCE = new ZipOrJarTypeFileValidator();

    private ZipOrJarTypeFileValidator() {}

    @Override
    public void validate(String filePath, String fileName, ZippedFileValidateConfig config)
            throws FitSecurityException {
        notNull(filePath, "The file path cannot be null.");
        notBlank(fileName, "The file name cannot be blank.");
        notNull(config, "The config for zipped file validate cannot be null.");
        long fileCount = 0L;
        long totalSize = 0L;
        try (ZipFile zipFile = new ZipFile(Paths.get(filePath, fileName).toString())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                fileCount += 1;
                if (config.zippedFileEntryCountLimit() > 0 && fileCount > config.zippedFileEntryCountLimit()) {
                    throw new ZippedFileException(StringUtils.format(
                            "Too many entries in zipped file. [zippedFileEntryCountLimit={0}, fileCount={1}]",
                            config.zippedFileEntryCountLimit(),
                            fileCount));
                }
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    totalSize += entry.getSize();
                }
                if (config.zippedFileTotalSizeLimit() > 0 && totalSize > config.zippedFileTotalSizeLimit()) {
                    throw new ZippedFileException(StringUtils.format(
                            "The total size of zipped file is too big. [zippedFileTotalSizeLimit={0}, "
                                    + "totalSize={1}]",
                            config.zippedFileTotalSizeLimit(),
                            totalSize));
                }
                // 在对于压缩文件中的子文件进行文件名校验时，需要预先去除文件名中的正斜杠和反斜杠。
                String entryName = entry.getName().replaceAll("[/\\\\]", "");
                FileNameValidateUtils.validate(entryName, config.fileNameValidateConfig());
            }
        } catch (IOException ex) {
            throw new ZippedFileException("File io exception.");
        }
    }
}
