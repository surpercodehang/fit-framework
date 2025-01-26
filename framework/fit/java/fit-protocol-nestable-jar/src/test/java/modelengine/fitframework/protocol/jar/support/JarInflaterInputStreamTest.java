/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 为 {@link JarInflaterInputStream} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2024-08-12
 */
@DisplayName("测试 JarInflaterInputStream")
public class JarInflaterInputStreamTest {
    @Test
    @DisplayName("当填充过程发生 EOF 异常时，处理异常成功")
    void givenEofExceptionWhenFillThenHandleExceptionSuccessfully() throws IOException {
        InputStream in = mock(InputStream.class);
        when(in.read(any(), anyInt(), anyInt())).thenThrow(new EOFException());
        JarInflaterInputStream jarInflaterInputStream = new JarInflaterInputStream(in, 0);
        assertThatNoException().isThrownBy(jarInflaterInputStream::fill);
    }
}
