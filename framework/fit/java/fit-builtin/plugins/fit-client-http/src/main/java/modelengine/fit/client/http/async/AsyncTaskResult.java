/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.http.async;

import modelengine.fit.client.Response;
import modelengine.fit.http.exception.AsyncTaskExecutionException;
import modelengine.fitframework.serialization.ResponseMetadata;

/**
 * FIT 异步任务执行结果，封装 {@link modelengine.fit.http.client.HttpClassicClientResponse} 和 {@link ResponseMetadata}。
 * <p>任务执行完毕后，长轮询线程会读取 HTTP 头部并进行解析，而消息体的读取会在客户端调用线程处完成。</p>
 *
 * @author 王成
 * @author 季聿阶
 * @since 2023-11-17
 */
class AsyncTaskResult {
    private final Response response;

    AsyncTaskResult(Response response) {
        this.response = response;
    }

    /**
     * 获取异步响应结果。
     *
     * @return 表示异步响应结果的 {@link Response}。
     */
    public Response getResponse() {
        return this.response;
    }

    /**
     * 当长轮询线程因为自身异常或者其他原因退出时，会清空所有未完成任务，并返回空结果，提示客户端应重新提交任务。
     *
     * @return 表示空结果的 {@link AsyncTaskResult}。
     */
    public static AsyncTaskResult getEmptyResult() {
        return new AsyncTaskResult(Response.create(ResponseMetadata.custom()
                .code(AsyncTaskExecutionException.CODE)
                .message("Async task has lost. Please re-submit.")
                .build(), null));
    }
}

