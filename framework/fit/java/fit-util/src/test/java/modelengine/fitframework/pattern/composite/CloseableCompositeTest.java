/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/**
 * {@link CloseableComposite} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-01-28
 */
@DisplayName("测试 CloseableComposite 工具类")
class CloseableCompositeTest {
    @Nested
    @DisplayName("测试方法：combine")
    class TestCombine {
        @Test
        @DisplayName("当提供多个可关闭对象时，返回组合后的 1 个可关闭对象")
        void givenSomeCloseableObjectThenReturnCloseableComposite() throws IOException {
            final OutputStream[] outputStreams = this.getTwoOutputStream();
            final Closeable closeable = CloseableComposite.combine(outputStreams);
            closeable.close();
            assertThatThrownBy(() -> outputStreams[0].write(1)).isInstanceOf(IOException.class);
            assertThatThrownBy(() -> outputStreams[1].write(1)).isInstanceOf(IOException.class);
        }

        @Test
        @DisplayName("当提供 1 个集合含多个可关闭对象时，返回组合后的 1 个可关闭对象")
        void givenCollectionIncludeCloseableObjectThenReturnCloseableComposite() throws IOException {
            final OutputStream[] outputStreams = this.getTwoOutputStream();
            final List<OutputStream> outputStreamList = Arrays.asList(outputStreams);
            final Closeable closeable = CloseableComposite.combine(outputStreamList);
            assertThat(closeable).isNotNull();
            closeable.close();
            assertThatThrownBy(() -> outputStreams[0].write(1)).isInstanceOf(IOException.class);
            assertThatThrownBy(() -> outputStreams[1].write(1)).isInstanceOf(IOException.class);
        }

        private OutputStream[] getTwoOutputStream() throws IOException {
            File file1 = Files.createTempFile("CloseableCompositeTest-1", ".txt").toFile();
            file1.deleteOnExit();
            File file2 = Files.createTempFile("CloseableCompositeTest-2", ".txt").toFile();
            file2.deleteOnExit();
            final OutputStream outputStream1 = new FileOutputStream(file1);
            final OutputStream outputStream2 = new FileOutputStream(file2);
            return new OutputStream[] {outputStream1, outputStream2};
        }
    }
}
