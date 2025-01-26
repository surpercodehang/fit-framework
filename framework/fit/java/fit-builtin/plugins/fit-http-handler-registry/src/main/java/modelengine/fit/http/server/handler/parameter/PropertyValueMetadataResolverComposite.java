/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.parameter;

import modelengine.fit.http.server.handler.PropertyValueMetadata;
import modelengine.fit.http.server.handler.PropertyValueMetadataResolver;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.value.PropertyValue;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示组合的 {@link PropertyValueMetadataResolver}。
 * <p>在组合的参数元数据解析器中，只要有任意一个参数元数据解析器解析成功，即返回结果。</p>
 *
 * @author 季聿阶
 * @since 2023-01-12
 */
public class PropertyValueMetadataResolverComposite implements PropertyValueMetadataResolver {
    private final List<PropertyValueMetadataResolver> resolvers;

    /**
     * 创建组合的参数元数据解析器。
     *
     * @param resolvers 需要组合的参数元数据解析器集合的 {@link PropertyValueMetadataResolver}。
     */
    public PropertyValueMetadataResolverComposite(PropertyValueMetadataResolver... resolvers) {
        PropertyValueMetadataResolver[] actualResolvers =
                ObjectUtils.getIfNull(resolvers, () -> new PropertyValueMetadataResolver[0]);
        this.resolvers = Stream.of(actualResolvers).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<PropertyValueMetadata> resolve(PropertyValue propertyValue) {
        for (PropertyValueMetadataResolver resolver : this.resolvers) {
            List<PropertyValueMetadata> resolved = resolver.resolve(propertyValue);
            if (!resolved.isEmpty()) {
                return resolved;
            }
        }
        return Collections.emptyList();
    }
}
