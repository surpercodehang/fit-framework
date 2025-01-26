/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fit.http.server.handler.support.EntityResolver;
import modelengine.fit.http.server.handler.support.HttpClassicRequestResolver;
import modelengine.fit.http.server.handler.support.HttpClassicResponseResolver;
import modelengine.fit.http.server.handler.support.PropertyValueResolverComposite;
import modelengine.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示属性值解析器。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
@FunctionalInterface
public interface PropertyValueMapperResolver {
    /**
     * 解析一个属性值，来获取一个属性值映射器。
     *
     * @param propertyValue 表示待解析的属性值的 {@link PropertyValue}。
     * @return 表示解析后的属性值映射器的 {@link Optional}{@code <}{@link PropertyValueMapper}{@code >}。
     */
    Optional<PropertyValueMapper> resolve(PropertyValue propertyValue);

    /**
     * 获取默认的属性值映射解析器。
     *
     * @return 表示属性值映射解析器的 {@link PropertyValueMapperResolver}。
     */
    static PropertyValueMapperResolver defaultResolver() {
        return PropertyValueMapperResolver.combine(new HttpClassicRequestResolver(),
                new HttpClassicResponseResolver(),
                new EntityResolver());
    }

    /**
     * 合并属性值映射解析器。
     *
     * @param resolvers 表示待合并的属性值映射解析器列表的 {@link PropertyValueMapperResolver}{@code []}。
     * @return 表示合并后的属性值映射解析器的 {@link PropertyValueMapperResolver}。
     */
    static PropertyValueMapperResolver combine(PropertyValueMapperResolver... resolvers) {
        return new PropertyValueResolverComposite(resolvers);
    }
}
