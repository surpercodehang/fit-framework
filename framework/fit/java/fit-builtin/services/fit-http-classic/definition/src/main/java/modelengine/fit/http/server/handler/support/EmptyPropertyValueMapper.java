/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.PropertyValueMapper;

import java.util.Map;

/**
 * 表示空的 Http 值映射器。
 *
 * @author 邬涨财
 * @since 2023-01-02
 */
public class EmptyPropertyValueMapper implements PropertyValueMapper {
    /** 表示空的 Http 值映射器的单例。 */
    public static final PropertyValueMapper INSTANCE = new EmptyPropertyValueMapper();

    private EmptyPropertyValueMapper() {}

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        return null;
    }
}
