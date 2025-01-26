/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示 {@link ErrorMapper} 的解析器。
 *
 * @author 季聿阶
 * @since 2023-12-11
 */
public class ErrorMapperResolver implements PropertyValueMapperResolver {
    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        if (Throwable.class.isAssignableFrom(propertyValue.getType())) {
            Class<Throwable> errorClass = cast(propertyValue.getType());
            return Optional.of(new ErrorMapper(errorClass));
        }
        return Optional.empty();
    }
}
