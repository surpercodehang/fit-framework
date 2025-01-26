/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.type;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.support.FileTypeException;
import modelengine.fit.security.http.util.CreateZipUtils;
import modelengine.fitframework.util.FileUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * 表示 {@link FileTypeValidateUtils} 的测试。
 *
 * @author 何天放
 * @since 2024-07-25
 */
@DisplayName("测试文件类型校验工具")
public final class FileTypeValidateUtilsTest {
    private static final String ZIP_FILE_NAME = "zip_file_for_test.zip";
    private static final String ZIP_FILE_PATH = ".";
    private static final String ZIP_FILE_MAGIC = "504b0304";

    @Test
    @DisplayName("当内容与期望的魔数匹配时须返回文件类型错误")
    void shouldReturnFileTypeErrorWhenMagicNotMatch() throws IOException {
        CreateZipUtils.create(ZIP_FILE_PATH, ZIP_FILE_NAME, new Integer[] {100});
        boolean failed = false;
        try {
            FileTypeValidateUtils.validate(ZIP_FILE_PATH, ZIP_FILE_NAME, "504b0303");
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(FileTypeException.class);
        }
        assertThat(failed).isTrue();
        FileUtils.delete(new File(ZIP_FILE_PATH + "/" + ZIP_FILE_NAME));
    }

    @Test
    @DisplayName("当内容与期望的魔数匹配时须返回校验通过")
    void shouldReturnOkWhenMagicMatch() throws IOException {
        CreateZipUtils.create(ZIP_FILE_PATH, ZIP_FILE_NAME, new Integer[] {100});
        boolean failed = false;
        try {
            FileTypeValidateUtils.validate(ZIP_FILE_PATH, ZIP_FILE_NAME, ZIP_FILE_MAGIC);
        } catch (FitSecurityException e) {
            failed = true;
        }
        assertThat(failed).isFalse();
        FileUtils.delete(new File(ZIP_FILE_PATH + "/" + ZIP_FILE_NAME));
    }
}
