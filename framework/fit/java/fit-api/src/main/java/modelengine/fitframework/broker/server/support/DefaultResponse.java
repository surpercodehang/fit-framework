/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

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
