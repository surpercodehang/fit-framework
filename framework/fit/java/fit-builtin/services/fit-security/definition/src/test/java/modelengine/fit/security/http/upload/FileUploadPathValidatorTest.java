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
import modelengine.fit.security.http.support.FileCountOverflowException;
import modelengine.fit.security.http.upload.support.UploadPathValidator;
import modelengine.fitframework.util.FileUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 表示 {@link UploadPathValidator} 的测试。
 * <p>受限于无法在测试中控制剩余空间，因此不对于基于剩余空间大小校验的功能进行测试。</p>
 *
 * @author 何天放
 * @since 2024-07-12
 */
@DisplayName("测试文件路径校验器")
public final class FileUploadPathValidatorTest {
    private static final String FILE_PATH = "./test_path";
    private static final String FILE_NAME = "test_file_name.txt";
    private static final long FILE_SIZE = 20 * 1024; // 20 KB

    @BeforeAll
    public static void setup() throws IOException {
        File directory = new File(FILE_PATH);
        if (!directory.exists()) {
            boolean result = directory.mkdirs();
            assertThat(result).isTrue();
        }
        try (RandomAccessFile file = new RandomAccessFile(FILE_PATH + "/" + FILE_NAME, "rw")) {
            file.setLength(FILE_SIZE);
        }
    }

    @AfterAll
    public static void teardown() {
        FileUtils.delete(new File(FILE_PATH));
    }

    @Test
    @DisplayName("当上传路径满足要求时须返回校验通过")
    void shouldReturnOkWhenFileUploadPathIsLegal() {
        FileUploadValidateConfig config = Mockito.mock(FileUploadValidateConfig.class);
        when(config.fileSavePath()).thenReturn(FILE_PATH);
        when(config.fileSavePathFileCountLimit()).thenReturn(10L);
        when(config.fileSavePathRestSpaceLimit()).thenReturn(FILE_SIZE + 1);
        FileEntity entity = mock(FileEntity.class);
        when(entity.length()).thenReturn(1L);
        boolean failed = false;
        try {
            UploadPathValidator.INSTANCE.validate(entity, config);
        } catch (FitSecurityException e) {
            failed = true;
        }
        assertThat(failed).isFalse();
    }

    @Test
    @DisplayName("当上传路径文件数量超过阈值时须返回文件数量移除错误")
    void shouldReturnFileCountOverflewErrorWhenTooManyFilesInTargetPath() {
        FileUploadValidateConfig config = Mockito.mock(FileUploadValidateConfig.class);
        when(config.fileSavePath()).thenReturn(FILE_PATH);
        when(config.fileSavePathFileCountLimit()).thenReturn(1L);
        when(config.fileSavePathRestSpaceLimit()).thenReturn(FILE_SIZE + 1);
        FileEntity entity = mock(FileEntity.class);
        when(entity.length()).thenReturn(1L);
        boolean failed = false;
        try {
            UploadPathValidator.INSTANCE.validate(entity, config);
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(FileCountOverflowException.class);
            assertThat(e.getMessage()).isEqualTo("Too many files in target path. [fileCount=1]");
        }
        assertThat(failed).isTrue();
    }
}
