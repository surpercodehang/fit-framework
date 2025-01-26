/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.MultiValueEntity;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fit.http.server.handler.exception.RequestParamFetchException;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 表示从 {@link MultiValueEntity} 中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class FormUrlEncodedEntityFetcher extends EntityFetcher {
    private final String key;

    /**
     * 用参数的键来实例化 {@link FormUrlEncodedEntityFetcher}。
     *
     * @param key 表示参数的键的 {@link String}。
     */
    public FormUrlEncodedEntityFetcher(String key) {
        this.key = notBlank(key, () -> new RequestParamFetchException("The key cannot be blank."));
    }

    /**
     * 用参数元数据来实例化 {@link FormUrlEncodedEntityFetcher}。
     *
     * @param paramValue 表示参数元数据的 {@link ParamValue}。
     */
    public FormUrlEncodedEntityFetcher(ParamValue paramValue) {
        this.key = notBlank(paramValue.name(), () -> new RequestParamFetchException("The key cannot be blank."));
    }

    @Override
    public boolean isArrayAble() {
        return true;
    }

    @Override
    protected Class<? extends Entity> entityType() {
        return MultiValueEntity.class;
    }

    @Override
    protected Object getFromRequest(HttpClassicServerRequest request, Entity entity) {
        MultiValueEntity multiValueEntity = ObjectUtils.cast(entity);
        return multiValueEntity.all(this.key);
    }
}
