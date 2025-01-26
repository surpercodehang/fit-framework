/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.type;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.support.FileTypeException;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * 为文件内容校验提供工具方法。
 *
 * @author 何天放
 * @since 2024-07-24
 */
public final class FileTypeValidateUtils {
    private static final int MAGIC_LENGTH = 10;
    private static final int BYTE_HEX_VALUE_MASK = 0xff;
    private static final int BYTE_HEX_VALUE_LENGTH = 2;

    private FileTypeValidateUtils() {}

    /**
     * 校验文件开头内容是否与期望内容匹配。
     *
     * @param filePath 表示文件路径的 {@link String}。
     * @param fileName 表示文件名的 {@link String}。
     * @param expected 表示所期望文件开头的 {@link String}。
     * @throws FitSecurityException 当类型校验未通过时。
     */
    public static void validate(String filePath, String fileName, String expected) throws FitSecurityException {
        notNull(filePath, "The file path cannot be null.");
        notBlank(fileName, "The file name cannot be blank.");
        notBlank(expected, "The expected content cannot be blank.");
        String fileExtensionName = FileUtils.extension(fileName);
        if (StringUtils.isBlank(fileExtensionName)) {
            throw new FileTypeException("The file extension name cannot be blank.");
        }
        validateMagic(filePath, fileName, expected);
    }

    private static void validateMagic(String filePath, String fileName, String expected) throws FitSecurityException {
        byte[] buffer = new byte[MAGIC_LENGTH];
        int byteCount;
        Path normalizedPath = new File(Paths.get(filePath, fileName).normalize().toString()).toPath();
        try (InputStream fileInputStream = Files.newInputStream(normalizedPath)) {
            byteCount = fileInputStream.read(buffer, 0, MAGIC_LENGTH);
        } catch (IOException ex) {
            throw new FileTypeException(StringUtils.format(
                    "Cannot read file content when try to read magic. [filePath={0}, fileName={1}]",
                    filePath,
                    fileName));
        }
        if (byteCount == -1) {
            throw new FileTypeException("Cannot read from input stream.");
        }
        StringBuilder actualBuilder = new StringBuilder();
        for (byte eachByte : buffer) {
            String hexValue = Integer.toHexString(eachByte & BYTE_HEX_VALUE_MASK);
            if (hexValue.length() < BYTE_HEX_VALUE_LENGTH) {
                actualBuilder.append("0");
            }
            actualBuilder.append(hexValue);
        }
        String actual = actualBuilder.toString().toLowerCase(Locale.getDefault());
        if (!actual.startsWith(expected)) {
            throw new FileTypeException(StringUtils.format(
                    "The file content is not start with expected. [actual={0}, expected={1}]",
                    actual,
                    expected));
        }
    }
}
