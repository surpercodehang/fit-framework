/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client;

import modelengine.fit.client.support.DefaultResponse;
import modelengine.fitframework.serialization.ResponseMetadata;

/**
 * 表示响应。
 *
 * @author 季聿阶
 * @since 2022-09-19
 */
public interface Response {
    /**
     * 获取响应元数据信息。
     *
     * @return 表示响应元数据的 {@link ResponseMetadata}。
     */
    ResponseMetadata metadata();

    /**
     * 获取响应数据。
     *
     * @return 表示响应数据的 {@link Object}。
     */
    Object data();

    /**
     * 创建一个响应。
     *
     * @param metadata 表示响应元数据的 {@link ResponseMetadata}。
     * @param data 表示响应数据的 {@link Object}。
     * @return 表示创建的请求的 {@link Response}。
     * @throws IllegalArgumentException 当 {@code metadata} 为 {@code null} 时。
     */
    static Response create(ResponseMetadata metadata, Object data) {
        return new DefaultResponse(metadata, data);
    }
}
