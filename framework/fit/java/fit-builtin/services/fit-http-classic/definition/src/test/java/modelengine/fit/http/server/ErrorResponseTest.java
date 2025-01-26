/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import static modelengine.fit.http.protocol.HttpResponseStatus.MULTI_STATUS;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link ErrorResponse} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-23
 */
@DisplayName("测试 ErrorResponse 类")
public class ErrorResponseTest {
    private ErrorResponse errorResponse;

    @BeforeEach
    void setup() {
        this.errorResponse = ErrorResponse.create(MULTI_STATUS, MULTI_STATUS.reasonPhrase(), "testPath");
    }

    @Test
    @DisplayName("获取内部错误的 http 状态码与给定的值相等")
    void theStatusIsEqualsToTheGivenStatus() {
        int status = this.errorResponse.getStatus();
        assertThat(status).isEqualTo(207);
    }

    @Test
    @DisplayName("获取内部错误的 Http 错误信息与给定值相等")
    void theaErrorIsEqualsToTheGivenError() {
        String error = this.errorResponse.getError();
        assertThat(error).isEqualTo("Multi-Status");
    }

    @Test
    @DisplayName("获取内部错误的 Http 请求路径与给定路径值相等")
    void thePathIsEqualsToTheGivenPath() {
        String path = this.errorResponse.getPath();
        assertThat(path).isEqualTo("testPath");
    }

    @Test
    @DisplayName("获取内部错误的发生时间戳不为空")
    void theTimestampIsNotEmpty() {
        String timestamp = this.errorResponse.getTimestamp();
        assertThat(timestamp).isNotEmpty();
    }
}
