/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.broker.server.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.server.Response;
import modelengine.fitframework.serialization.ResponseMetadata;

import java.lang.reflect.Type;

/**
 * 表示 {@link Response} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-13
 */
public class DefaultResponse implements Response {
    private final ResponseMetadata metadata;
    private final Type type;
    private final Object data;
    
    /**
     * 使用指定的响应元数据、数据类型和数据来初始化 {@link DefaultResponse} 的新实例。
     *
     * @param metadata 表示响应元数据的 {@link ResponseMetadata}。
     * @param type 表示响应数据类型的 {@link Type}。
     * @param data 表示响应数据的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code metadata} 为 {@code null} 时。
     */
    public DefaultResponse(ResponseMetadata metadata, Type type, Object data) {
        this.metadata = notNull(metadata, "No metadata.");
        this.type = type;
        this.data = data;
    }

    @Override
    public ResponseMetadata metadata() {
        return this.metadata;
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public Object data() {
        return this.data;
    }
}
