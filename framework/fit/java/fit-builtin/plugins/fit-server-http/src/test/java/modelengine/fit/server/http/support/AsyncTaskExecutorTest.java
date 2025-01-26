/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.server.http.support;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.serialization.http.HttpUtils;
import modelengine.fitframework.broker.server.Response;
import modelengine.fitframework.serialization.RequestMetadata;
import modelengine.fitframework.serialization.ResponseMetadata;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.tlv.TlvUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * 为 {@link AsyncTaskExecutor} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2024-08-26
 */
@DisplayName("测试 AsyncTaskExecutor")
public class AsyncTaskExecutorTest {
    @Test
    @DisplayName("提交一个任务后，开始长轮训，一切正常")
    void shouldOkAfterSubmitAndGet() {
        TagLengthValues requestTlv = TagLengthValues.create();
        TlvUtils.setWorkerId(requestTlv, "workerId");
        TlvUtils.setWorkerInstanceId(requestTlv, "instanceId");
        HttpUtils.setAsyncTaskId(requestTlv, "taskId");
        RequestMetadata requestMetadata = RequestMetadata.custom().tagValues(requestTlv).build();
        Response response = Response.create(ResponseMetadata.custom().build());
        int code = AsyncTaskExecutor.INSTANCE.submit(requestMetadata, () -> response);
        assertThat(code).isEqualTo(ResponseMetadata.CODE_OK);
        Optional<Response> actual = AsyncTaskExecutor.INSTANCE.longPolling("workerId", "instanceId");
        assertThat(actual).isNotEmpty();
    }
}
