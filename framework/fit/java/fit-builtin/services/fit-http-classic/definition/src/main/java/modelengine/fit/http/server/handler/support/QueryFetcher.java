/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.RequestMappingException;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fit.http.server.handler.exception.RequestParamFetchException;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示从查询参数中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class QueryFetcher extends AbstractSourceFetcher {
    private final String queryKey;

    /**
     * 通过查询参数的键来实例化 {@link QueryFetcher}。
     *
     * @param queryKey 表示查询参数键的 {@link String}。
     * @throws IllegalArgumentException 当 {@code queryKey} 为 {@code null} 或空白字符串时。
     */
    public QueryFetcher(String queryKey) {
        super(false, null);
        this.queryKey = notBlank(queryKey, () -> new RequestParamFetchException("The query key cannot be blank."));
    }

    /**
     * 通过查询参数的元数据来实例化 {@link QueryFetcher}。
     *
     * @param paramValue 表示参数的元数据的 {@link ParamValue}。
     * @throws IllegalArgumentException 当 {@code queryKey} 为 {@code null} 或空白字符串时。
     */
    public QueryFetcher(ParamValue paramValue) {
        super(paramValue.required(), paramValue.defaultValue());
        this.queryKey =
                notBlank(paramValue.name(), () -> new RequestParamFetchException("The query key cannot be blank."));
    }

    @Override
    public boolean isArrayAble() {
        return true;
    }

    @Override
    public Object get(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        try {
            return this.resolveValue(request.queries().all(this.queryKey));
        } catch (RequestMappingException e) {
            throw new RequestMappingException(StringUtils.format("Invalid query param. [queryKey={0}]", this.queryKey),
                    e);
        }
    }
}
