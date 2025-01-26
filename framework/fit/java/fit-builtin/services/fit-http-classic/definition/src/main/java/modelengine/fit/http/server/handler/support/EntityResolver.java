/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示解析 {@link Entity} 对象参数的 {@link PropertyValueMapperResolver}。
 *
 * @author 季聿阶
 * @since 2022-08-31
 */
public class EntityResolver implements PropertyValueMapperResolver {
    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        if (!Entity.class.isAssignableFrom(propertyValue.getType())) {
            return Optional.empty();
        }
        UniqueSourcePropertyValueMapper mapper = new UniqueSourcePropertyValueMapper(new EntityFetcher(), false);
        return Optional.of(mapper);
    }
}
