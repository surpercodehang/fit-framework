/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.PropertyValueMapper;

import java.util.Map;

/**
 * 表示错误参数的映射器。
 *
 * @author 季聿阶
 * @since 2023-12-11
 */
public class ErrorMapper implements PropertyValueMapper {
    /** 表示在自定义上下文中错误的主键。 */
    public static final String ERROR_KEY = "FIT-Error";

    private final Class<Throwable> errorClass;

    public ErrorMapper(Class<Throwable> errorClass) {
        this.errorClass = notNull(errorClass, "The error class cannot be null.");
    }

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        if (context == null) {
            return null;
        }
        Object error = context.get(ERROR_KEY);
        if (error == null) {
            return null;
        }
        if (this.errorClass.isAssignableFrom(error.getClass())) {
            return error;
        }
        return null;
    }
}
