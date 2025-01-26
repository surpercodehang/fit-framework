/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.support.DefaultReadableBinaryEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 表示 {@link ReadableBinaryEntity} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-22
 */
@DisplayName("测试 ReadableBinaryEntity 类")
class ReadableBinaryEntityTest {
    private final byte[] readBytes = new byte[3];
    private ReadableBinaryEntity readableBinaryEntity;

    @BeforeEach
    void setup() {
        final HttpMessage httpMessage = mock(HttpMessage.class);
        InputStream inputStream = new ByteArrayInputStream(new byte[] {1, 2, 3});
        this.readableBinaryEntity = new DefaultReadableBinaryEntity(httpMessage, inputStream);
    }

    @Test
    @DisplayName("当提供合适的字节数组，向 Http 消息体中读取数据成功")
    void givenProperBytesThenReturnReadSuccessful() throws IOException {
        this.readableBinaryEntity.read(this.readBytes);
        assertThat(this.readBytes).hasSize(3).contains(1, 2, 3);
    }

    @Test
    @DisplayName("当提供大字节数组，向 Http 消息体中读取数据成功")
    void givenBigBytesThenReturnReadSuccessful() throws IOException {
        final byte[] bigBytes = new byte[5];
        this.readableBinaryEntity.read(bigBytes, 0, bigBytes.length);
        assertThat(bigBytes).contains(1, 2, 3, 0, 0);
    }

    @Test
    @DisplayName("当读取偏移量为负数时，抛出异常")
    void whenReadOffsetIsNegateThenThrowException() {
        assertThatThrownBy(() -> this.readableBinaryEntity.read(this.readBytes,
                -1,
                this.readBytes.length)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("当读取长度为负数时，抛出异常")
    void whenReadLengthIsNegateThenThrowException() {
        assertThatThrownBy(() -> this.readableBinaryEntity.read(this.readBytes, 0, -1)).isInstanceOf(
                IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("当读取长度加偏移量超过总长度时，抛出异常")
    void whenReadLengthGreaterActualLengthThenThrowException() {
        assertThatThrownBy(() -> this.readableBinaryEntity.read(this.readBytes, 1, this.readBytes.length)).isInstanceOf(
                IndexOutOfBoundsException.class);
    }
}
