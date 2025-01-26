/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.value.PropertyValue;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示组合的 {@link PropertyValueMapperResolver}。
 * <p>在组合的 Http 值映射解析器中，只要有任意一个参数映射解析器解析成功，即返回结果。</p>
 *
 * @author 季聿阶
 * @since 2022-08-30
 */
public class PropertyValueResolverComposite implements PropertyValueMapperResolver {
    private final List<PropertyValueMapperResolver> resolvers;

    /**
     * 创建 Http 值映射解析器的组合器对象。
     *
     * @param resolvers 表示需要组合的解析器的 {@link PropertyValueMapperResolver}。
     */
    public PropertyValueResolverComposite(PropertyValueMapperResolver... resolvers) {
        PropertyValueMapperResolver[] actualResolvers =
                ObjectUtils.getIfNull(resolvers, () -> new PropertyValueMapperResolver[0]);
        this.resolvers = Stream.of(actualResolvers).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        for (PropertyValueMapperResolver resolver : this.resolvers) {
            Optional<PropertyValueMapper> resolved = resolver.resolve(propertyValue);
            if (resolved.isPresent()) {
                return resolved;
            }
        }
        return Optional.empty();
    }
}
