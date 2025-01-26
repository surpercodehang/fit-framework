/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 表示从 {@link ObjectEntity} 中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class ObjectEntityFetcher extends EntityFetcher {
    private final String propertyPath;

    /**
     * 用参数路径来实例化 {@link ObjectEntityFetcher}。
     *
     * @param propertyPath 表示参数路径的 {@link ParamValue}。
     */
    public ObjectEntityFetcher(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    /**
     * 用参数的元数据来实例化 {@link ObjectEntityFetcher}。
     *
     * @param paramValue 表示参数元数据的 {@link ParamValue}。
     */
    public ObjectEntityFetcher(ParamValue paramValue) {
        this.propertyPath = paramValue.name();
    }

    @Override
    protected Class<? extends Entity> entityType() {
        return ObjectEntity.class;
    }

    @Override
    protected Object getFromRequest(HttpClassicServerRequest request, Entity entity) {
        ObjectEntity<?> objectEntity = ObjectUtils.cast(entity);
        return request.httpResource().valueFetcher().fetch(objectEntity.object(), this.propertyPath);
    }
}
