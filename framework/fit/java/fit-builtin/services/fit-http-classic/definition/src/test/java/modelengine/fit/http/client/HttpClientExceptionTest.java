/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link HttpClientException} 提供单元测试。
 *
 * @author 杭潇
 * @since 2023-02-16
 */
@DisplayName("测试 HttpClientException 类")
public class HttpClientExceptionTest {
    @Test
    @DisplayName("给定一个 String 参数，实例化对象成功")
    void givenStringParameterThenInitializeObjectSuccessfully() {
        String message = "testMessage";
        HttpClientException httpClientException = new HttpClientException(message);
        assertThat(httpClientException.getMessage()).isEqualTo(message);
    }
}
