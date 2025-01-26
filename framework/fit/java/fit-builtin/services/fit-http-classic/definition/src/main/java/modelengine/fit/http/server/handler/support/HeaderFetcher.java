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
 * 表示从消息头中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class HeaderFetcher extends AbstractSourceFetcher {
    private final String headerName;

    /**
     * 通过消息头名字来实例化 {@link HeaderFetcher}。
     *
     * @param headerName 表示消息头名字的 {@link String}。
     * @throws IllegalArgumentException 当 {@code headerName} 为 {@code null} 或空白字符串时。
     */
    public HeaderFetcher(String headerName) {
        super(false, null);
        this.headerName =
                notBlank(headerName, () -> new RequestParamFetchException("The header name cannot be blank."));
    }

    /**
     * 通过参数元数据来实例化 {@link HeaderFetcher}。
     *
     * @param paramValue 表示参数元数据的 {@link String}。
     * @throws IllegalArgumentException 当 {@code headerName} 为 {@code null} 或空白字符串时。
     */
    public HeaderFetcher(ParamValue paramValue) {
        super(paramValue.required(), paramValue.defaultValue());
        this.headerName =
                notBlank(paramValue.name(), () -> new RequestParamFetchException("The header name cannot be blank."));
    }

    @Override
    public boolean isArrayAble() {
        return true;
    }

    @Override
    public Object get(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        try {
            return this.resolveValue(request.headers().all(this.headerName));
        } catch (RequestMappingException e) {
            throw new RequestMappingException(StringUtils.format("Invalid header param. [headerName={0}]",
                    this.headerName), e);
        }
    }
}
