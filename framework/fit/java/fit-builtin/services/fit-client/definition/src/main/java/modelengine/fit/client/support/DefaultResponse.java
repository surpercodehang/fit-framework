/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.client.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.client.Response;
import modelengine.fitframework.serialization.ResponseMetadata;

/**
 * 表示 {@link Response} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-09-19
 */
public class DefaultResponse implements Response {
    private final ResponseMetadata metadata;
    private final Object data;

    public DefaultResponse(ResponseMetadata metadata, Object data) {
        this.metadata = notNull(metadata, "The metadata cannot be null.");
        this.data = data;
    }

    @Override
    public ResponseMetadata metadata() {
        return this.metadata;
    }

    @Override
    public Object data() {
        return this.data;
    }
}
