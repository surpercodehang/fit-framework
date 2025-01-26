/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * 表示 {@link SecurityUtils} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-07-26
 */
@DisplayName("测试 SecurityUtils")
public class SecurityUtilsTest {
    @Nested
    @DisplayName("测试方法：signatureOf(File file, String algorithm, int bufferSize)")
    class SignatureOfTest {
        @Test
        @DisplayName("当签名算法不存在时，抛出异常")
        void shouldThrowsWhenAlgorithmNotFound() {
            File file = new File("");
            final String algorithm = "fake-algorithm";
            final int bufferSize = 1;
            IllegalStateException exception =
                    catchThrowableOfType(() -> SecurityUtils.signatureOf(file, algorithm, bufferSize),
                            IllegalStateException.class);
            assertThat(exception).hasMessage("Signature algorithm not found. [algorithm=fake-algorithm]");
        }

        @Test
        @DisplayName("当文件不可读时，抛出异常")
        void shouldThrowsWhenFileCannotBeRead() {
            File file = new File("bcb4a610-5aec-4e32-ac6d-16861ec0cb8e");
            final String algorithm = "MD5";
            final int bufferSize = 1;
            IllegalStateException exception =
                    catchThrowableOfType(() -> SecurityUtils.signatureOf(file, algorithm, bufferSize),
                            IllegalStateException.class);
            assertThat(exception).hasMessageContaining("Failed to read file to compute signature.");
        }

        @Test
        @DisplayName("当文件可读，返回该文件的签名值")
        void shouldReturnSignatureForFile() throws IOException {
            File file = Files.createTempFile("digest", ".txt").toFile();
            try (InputStream in = IoUtils.resource(Thread.currentThread().getContextClassLoader(),
                    "text/3ab2ed235174c136f296f6126dfa9393.txt");
                 OutputStream out = Files.newOutputStream(file.toPath())) {
                IoUtils.copy(in, out);
            }
            String signature = SecurityUtils.signatureOf(file, "MD5", 512);
            assertThat(signature).hasSize(32);
            for (int i = 0; i < signature.length(); i++) {
                char ch = signature.charAt(i);
                assertThat(CharacterUtils.between(ch, 'a', 'f', true, true) || CharacterUtils.between(ch,
                        '0',
                        '9',
                        true,
                        true)).isTrue();
            }
        }
    }
}
