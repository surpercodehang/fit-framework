/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.HttpResponse;
import modelengine.fit.http.protocol.HttpResponseStatus;

/**
 * 表示 {@link HttpResponse} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-11-28
 */
public class DefaultHttpResponse implements HttpResponse {
    private final HttpResponseStatus status;
    private final Object entity;

    public DefaultHttpResponse(HttpResponseStatus status, Object entity) {
        this.status = notNull(status, "The http response status cannot be null.");
        this.entity = entity;
    }

    @Override
    public HttpResponseStatus status() {
        return this.status;
    }

    @Override
    public Object entity() {
        return this.entity;
    }
}
