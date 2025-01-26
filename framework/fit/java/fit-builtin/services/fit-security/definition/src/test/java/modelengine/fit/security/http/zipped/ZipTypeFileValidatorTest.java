/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.zipped;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.name.FileNameValidateConfig;
import modelengine.fit.security.http.support.ZippedFileException;
import modelengine.fit.security.http.util.CreateZipUtils;
import modelengine.fit.security.http.zipped.support.ZipOrJarTypeFileValidator;
import modelengine.fitframework.util.FileUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 表示 {@link ZipOrJarTypeFileValidator} 的测试。
 *
 * @author 何天放
 * @since 2024-07-12
 */
@DisplayName("测试 zip 文件的校验功能")
public final class ZipTypeFileValidatorTest {
    private static final String ZIP_FILE_NAME = "zip_file_for_test.zip";
    private static final String ZIP_FILE_PATH = ".";

    @AfterEach
    void teardown() {
        FileUtils.delete(new File(ZIP_FILE_PATH + "/" + ZIP_FILE_NAME));
    }

    @Test
    @DisplayName("当 zip 文件合法时返回校验通过")
    void shouldReturnOkWhenZipFileIsLegal() throws IOException {
        CreateZipUtils.create(ZIP_FILE_PATH, ZIP_FILE_NAME, new Integer[] {100});
        ZippedFileValidateConfig config = createFileValidateConfig();
        boolean failed = false;
        try {
            ZipOrJarTypeFileValidator.INSTANCE.validate(ZIP_FILE_PATH, ZIP_FILE_NAME, config);
        } catch (FitSecurityException e) {
            failed = true;
        }
        assertThat(failed).isFalse();
    }

    @Test
    @DisplayName("当 zip 文件中存在过多的文件时须返回压缩文件校验错误")
    void shouldReturnZipFileErrorWhenTooManyEntriesInZipFile() throws IOException {
        CreateZipUtils.create(ZIP_FILE_PATH, ZIP_FILE_NAME, new Integer[] {100, 100, 100});
        ZippedFileValidateConfig config = createFileValidateConfig();
        boolean failed = false;
        try {
            ZipOrJarTypeFileValidator.INSTANCE.validate(ZIP_FILE_PATH, ZIP_FILE_NAME, config);
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(ZippedFileException.class);
            assertThat(e.getMessage()).isEqualTo(
                    "Too many entries in zipped file. [zippedFileEntryCountLimit=2, fileCount=3]");
        }
        assertThat(failed).isTrue();
    }

    @Test
    @DisplayName("当 zip 文件中各文件总大小过大时须返回压缩文件校验错误")
    void shouldReturnZipFileErrorWhenTotalSizeIsTooBig() throws IOException {
        CreateZipUtils.create(ZIP_FILE_PATH, ZIP_FILE_NAME, new Integer[] {1001});
        ZippedFileValidateConfig config = createFileValidateConfig();
        boolean failed = false;
        try {
            ZipOrJarTypeFileValidator.INSTANCE.validate(ZIP_FILE_PATH, ZIP_FILE_NAME, config);
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(ZippedFileException.class);
            assertThat(e.getMessage()).isEqualTo(
                    "The total size of zipped file is too big. [zippedFileTotalSizeLimit=1000, totalSize=1001]");
        }
        assertThat(failed).isTrue();
    }

    private static ZippedFileValidateConfig createFileValidateConfig() {
        FileNameValidateConfig fileNameValidateConfig = mock(FileNameValidateConfig.class);
        when(fileNameValidateConfig.fileNameFormat()).thenReturn("^[^.]+\\.[^.]+$");
        when(fileNameValidateConfig.blackList()).thenReturn(new ArrayList<>(Arrays.asList("..", "*")));
        when(fileNameValidateConfig.extensionNameWhiteList()).thenReturn(new ArrayList<>(Arrays.asList(".txt",
                ".png")));
        ZippedFileValidateConfig config = mock(ZippedFileValidateConfig.class);
        when(config.fileNameValidateConfig()).thenReturn(fileNameValidateConfig);
        when(config.zippedFileEntryCountLimit()).thenReturn(2L); // 限定压缩文件中文件数量不超过 2 个
        when(config.zippedFileTotalSizeLimit()).thenReturn(1000L); // 限定压缩文件总大小不超过 1000 字节
        return config;
    }
}
