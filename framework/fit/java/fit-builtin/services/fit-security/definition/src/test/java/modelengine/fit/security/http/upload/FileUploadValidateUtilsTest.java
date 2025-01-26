/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.upload;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.name.FileNameValidateConfig;
import modelengine.fit.security.http.support.FileNameException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 表示 {@link FileUploadValidateUtils} 的测试。
 *
 * @author 何天放
 * @since 2024-07-16
 */
@DisplayName("测试文件上传校验工具")
public final class FileUploadValidateUtilsTest {
    @Test
    @DisplayName("当待校验文件正确时需要返回校验通过")
    void shouldReturnOkWhenEverythingIsOk() throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(new byte[] {-1, -40, -1})) {
            FileEntity entity = mock(FileEntity.class);
            when(entity.filename()).thenReturn("test_file_name.jpg");
            when(entity.length()).thenReturn(100L);
            when(entity.getInputStream()).thenReturn(inputStream);
            FileNameValidateConfig fileNameValidateConfig = mock(FileNameValidateConfig.class);
            when(fileNameValidateConfig.extensionNameWhiteList()).thenReturn(new ArrayList<>(Arrays.asList(".png",
                    ".jpg")));
            when(fileNameValidateConfig.fileNameFormat()).thenReturn("^[^.]+\\.[^.]+$");
            when(fileNameValidateConfig.blackList()).thenReturn(new ArrayList<>(Arrays.asList("..", "/")));
            FileUploadValidateConfig config = Mockito.mock(FileUploadValidateConfig.class);
            when(config.fileNameValidateConfig()).thenReturn(fileNameValidateConfig);
            when(config.fileSizeLimit()).thenReturn(1000L);
            when(config.fileSavePath()).thenReturn("");
            boolean failed = false;
            try {
                FileUploadValidateUtils.validate(entity, config);
            } catch (FitSecurityException e) {
                failed = true;
            }
            assertThat(failed).isFalse();
        }
    }

    @Test
    @DisplayName("当待校验文件存在文件名错误时需要返回文件名异常")
    void shouldReturnErrorWhenSomethingWrong() {
        FileEntity entity = mock(FileEntity.class);
        when(entity.filename()).thenReturn("");
        FileNameValidateConfig fileNameValidateConfig = mock(FileNameValidateConfig.class);
        FileUploadValidateConfig config = Mockito.mock(FileUploadValidateConfig.class);
        when(config.fileNameValidateConfig()).thenReturn(fileNameValidateConfig);
        boolean failed = false;
        try {
            FileUploadValidateUtils.validate(entity, config);
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(FileNameException.class);
            assertThat(e.getMessage()).isEqualTo("The file name is blank.");
        }
        assertThat(failed).isTrue();
    }
}
