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
import modelengine.fitframework.util.FileUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 表示 {@link ZippedFileValidator} 的测试。
 *
 * @author 何天放
 * @since 2024-07-12
 */
@DisplayName("测试压缩文件校验功能")
public final class ZippedFileValidateUtilsTest {
    private static final String ZIP_FILE_NAME = "zip_file_for_test.zip";
    private static final String ZIP_FILE_PATH = "./" + ZIP_FILE_NAME;

    @AfterEach
    void teardown() {
        FileUtils.delete(new File(ZIP_FILE_PATH));
    }

    @Test
    @DisplayName("当无法校验该类型时须返回压缩文件校验错误")
    void shouldReturnZipFileErrorWhenCannotValidateThisType() {
        ZippedFileValidateConfig config = createZippedFileValidateConfig();
        boolean failed = false;
        try {
            ZippedFileValidateUtils.validate(".", "tar_file_for_test.tar", config);
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(ZippedFileException.class);
            assertThat(e.getMessage()).isEqualTo("Cannot validate zipped file as this type. [fileExtensionName=.tar]");
        }
        assertThat(failed).isTrue();
    }

    private static ZippedFileValidateConfig createZippedFileValidateConfig() {
        FileNameValidateConfig fileNameValidateConfig = mock(FileNameValidateConfig.class);
        when(fileNameValidateConfig.fileNameFormat()).thenReturn("^[^.]+\\.[^.]+$");
        when(fileNameValidateConfig.blackList()).thenReturn(new ArrayList<>(Arrays.asList("..", "*")));
        when(fileNameValidateConfig.extensionNameWhiteList()).thenReturn(new ArrayList<>(Arrays.asList(".txt",
                ".png")));
        ZippedFileValidateConfig config = mock(ZippedFileValidateConfig.class);
        when(config.zippedFileEntryCountLimit()).thenReturn(2L); // 限定压缩文件中文件数量不超过 2 个
        when(config.zippedFileTotalSizeLimit()).thenReturn(1000L); // 限定压缩文件总大小不超过 1000 字节
        return config;
    }
}
