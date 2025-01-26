/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.SourceFetcher;

import java.util.Optional;

/**
 * 表示从消息体中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class EntityFetcher implements SourceFetcher {
    @Override
    public Object get(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        Optional<Entity> opEntity = request.entity();
        if (!opEntity.isPresent()) {
            return null;
        }
        boolean isEntityMatch = this.entityType().isAssignableFrom(opEntity.get().getClass());
        if (!isEntityMatch) {
            return null;
        }
        return this.getFromRequest(request, opEntity.get());
    }

    /**
     * 获取当前消息体获取器可以获取的消息体的类型。
     *
     * @return 表示当前消息体获取器可以获取的消息体的类型的 {@link Class}{@code <? extends }{@link Entity}{@code >}。
     */
    protected Class<? extends Entity> entityType() {
        return Entity.class;
    }

    /**
     * 从 Http 请求中获取数据。
     *
     * @param request 表示 Http 请求的 {@link HttpClassicServerRequest}。
     * @param entity 表示 Http 请求的数据的 {@link Entity}。
     * @return 表示获取的数据的 {@link Object}。
     */
    protected Object getFromRequest(HttpClassicServerRequest request, Entity entity) {
        return entity;
    }
}
