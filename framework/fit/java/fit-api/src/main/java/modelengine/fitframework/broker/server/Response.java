/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.server;

import modelengine.fitframework.broker.server.support.DefaultResponse;
import modelengine.fitframework.serialization.ResponseMetadata;

import java.lang.reflect.Type;

/**
 * 表示 Fit 调用的原始返回值内容。
 *
 * @author 季聿阶
 * @since 2021-05-14
 */
public interface Response {
    /**
     * 获取响应的元数据。
     *
     * @return 表示响应元数据的 {@link ResponseMetadata}。
     */
    ResponseMetadata metadata();

    /**
     * 获取数据类型。
     *
     * @return 表示数据类型的 {@link Type}。
     */
    Type type();

    /**
     * 获取数据。
     *
     * @return 表示数据的 {@link Object}。
     */
    Object data();

    /**
     * 创建一个携带数据的返回值。
     *
     * @param metadata 表示返回值中的元数据的 {@link ResponseMetadata}。
     * @param type 表示返回值中的数据类型的 {@link Type}。
     * @param data 表示返回值中的数据的 {@link Object}。
     * @return 表示一个携带数据的返回值的 {@link Response}。
     * @throws IllegalArgumentException 当 {@code metadata} 为 {@code null} 时。
     */
    static Response create(ResponseMetadata metadata, Type type, Object data) {
        return new DefaultResponse(metadata, type, data);
    }

    /**
     * 创建一个不携带数据的返回值。
     *
     * @param metadata 表示返回值中的元数据的 {@link ResponseMetadata}。
     * @return 表示一个不携带数据的返回值的 {@link Response}。
     * @throws IllegalArgumentException 当 {@code metadata} 为 {@code null} 时。
     */
    static Response create(ResponseMetadata metadata) {
        return new DefaultResponse(metadata, null, null);
    }
}
