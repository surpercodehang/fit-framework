/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.security.http.name;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.security.http.FitSecurityException;
import modelengine.fit.security.http.name.support.DefaultFileNameValidator;
import modelengine.fit.security.http.support.FileNameException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 表示 {@link DefaultFileNameValidator} 的测试。
 *
 * @author 何天放
 * @since 2024-07-11
 */
@DisplayName("测试文件名校验器的默认实现")
public final class DefaultFileNameValidatorTest {
    @Test
    @DisplayName("当文件名为空时须返回文件名错误")
    void shouldReturnFileNameErrorWhenFileNameIsBlank() {
        FileNameValidateConfig config = mock(FileNameValidateConfig.class);
        boolean failed = false;
        try {
            DefaultFileNameValidator.INSTANCE.validate("", config);
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(FileNameException.class);
            assertThat(e.getMessage()).isEqualTo("The file name is blank.");
        }
        assertThat(failed).isTrue();
    }

    @Test
    @DisplayName("当文件名不满足规则时须返回文件名错误")
    void shouldReturnFileNameErrorWhenFileNameIsNotMatchFormat() {
        FileNameValidateConfig config = mock(FileNameValidateConfig.class);
        when(config.fileNameFormat()).thenReturn("^[^.]+\\.[^.]+$");
        boolean failed = false;
        try {
            DefaultFileNameValidator.INSTANCE.validate(".png", config);
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(FileNameException.class);
            assertThat(e.getMessage()).isEqualTo("The file name does not match the format.");
        }
        assertThat(failed).isTrue();
    }

    @Test
    @DisplayName("当文件名包含黑名单字符时须返回文件名错误")
    void shouldReturnFileNameErrorWhenFileNameContainsBlack() {
        FileNameValidateConfig config = mock(FileNameValidateConfig.class);
        when(config.blackList()).thenReturn(new ArrayList<>(Arrays.asList("..", "/")));
        boolean failed = false;
        try {
            DefaultFileNameValidator.INSTANCE.validate("abc..docx", config);
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(FileNameException.class);
            assertThat(e.getMessage()).isEqualTo("The file name contains illegal string.");
        }
        assertThat(failed).isTrue();
    }

    @Test
    @DisplayName("当文件扩展名非法时须返回文件名错误")
    void shouldReturnFileNameErrorWhenExtensionNameIsIllegal() {
        FileNameValidateConfig config = mock(FileNameValidateConfig.class);
        when(config.extensionNameWhiteList()).thenReturn(new ArrayList<>(Arrays.asList(".png", ".jpg")));
        boolean failed = false;
        try {
            DefaultFileNameValidator.INSTANCE.validate("abc.docx", config);
        } catch (FitSecurityException e) {
            failed = true;
            assertThat(e).isInstanceOf(FileNameException.class);
            assertThat(e.getMessage()).isEqualTo("The file extension name is illegal. [extensionName=.docx]");
        }
        assertThat(failed).isTrue();
    }

    @Test
    @DisplayName("当文件名合法时须返回校验通过")
    void shouldReturnOkWhenFileNameIsLegal() {
        FileNameValidateConfig config = mock(FileNameValidateConfig.class);
        when(config.extensionNameWhiteList()).thenReturn(new ArrayList<>(Arrays.asList(".png", ".jpg")));
        when(config.fileNameFormat()).thenReturn("^[^.]+\\.[^.]+$");
        when(config.blackList()).thenReturn(new ArrayList<>(Arrays.asList("..", "/")));
        boolean failed = false;
        try {
            DefaultFileNameValidator.INSTANCE.validate("abc.png", config);
        } catch (FitSecurityException e) {
            failed = true;
        }
        assertThat(failed).isFalse();
    }
}
