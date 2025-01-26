/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fit.http.server.handler.parameter.PropertyValueMetadataResolverComposite;
import modelengine.fitframework.value.PropertyValue;

import java.util.List;

/**
 * 表示属性值元数据的解析器。
 *
 * @author 季聿阶
 * @since 2023-01-12
 */
public interface PropertyValueMetadataResolver {
    /**
     * 解析一个属性值，来获取一个属性值元数据。
     *
     * @param propertyValue 表示待解析的属性值的 {@link PropertyValue}。
     * @return 表示解析后的属性值元数据的 {@link List}{@code <}{@link PropertyValueMetadata}{@code >}。
     */
    List<PropertyValueMetadata> resolve(PropertyValue propertyValue);

    /**
     * 合并参数元数据解析器。
     *
     * @param resolvers 表示待合并的参数元数据解析器列表的 {@link PropertyValueMetadataResolver}{@code []}。
     * @return 表示合并后的参数元数据解析器的 {@link PropertyValueMetadataResolver}。
     */
    static PropertyValueMetadataResolver combine(PropertyValueMetadataResolver... resolvers) {
        return new PropertyValueMetadataResolverComposite(resolvers);
    }
}
