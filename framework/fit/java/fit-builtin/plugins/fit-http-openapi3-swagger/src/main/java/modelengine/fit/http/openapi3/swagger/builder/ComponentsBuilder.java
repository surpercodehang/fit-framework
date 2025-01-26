/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.builder;

import modelengine.fit.http.openapi3.swagger.EntityBuilder;
import modelengine.fit.http.openapi3.swagger.entity.Components;
import modelengine.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fitframework.ioc.BeanContainer;

import java.util.Map;

/**
 * 表示 {@link Components} 的构建器。
 *
 * @author 季聿阶
 * @since 2023-08-28
 */
public class ComponentsBuilder extends AbstractBuilder implements EntityBuilder<Components> {
    private final SchemasBuilder schemasBuilder;

    ComponentsBuilder(BeanContainer container) {
        super(container);
        this.schemasBuilder = new SchemasBuilder(container);
    }

    @Override
    public Components build() {
        Map<String, Schema> schemas = this.schemasBuilder.build();
        return Components.custom().schemas(schemas).build();
    }
}
