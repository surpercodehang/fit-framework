/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger.builder;

import modelengine.fit.http.openapi3.swagger.EntityBuilder;
import modelengine.fit.http.openapi3.swagger.entity.Tag;
import modelengine.fit.http.server.HttpDispatcher;
import modelengine.fit.http.server.HttpHandlerGroup;
import modelengine.fitframework.ioc.BeanContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link List}{@code <}{@link Tag}{@code >} 的构建器。
 *
 * @author 季聿阶
 * @since 2023-08-23
 */
public class TagsBuilder extends AbstractBuilder implements EntityBuilder<List<Tag>> {
    TagsBuilder(BeanContainer container) {
        super(container);
    }

    @Override
    public List<Tag> build() {
        Collection<HttpHandlerGroup> groups = this.getHttpDispatcher()
                .map(HttpDispatcher::getHttpHandlerGroups)
                .map(Map::values)
                .orElseGet(Collections::emptyList);
        return groups.stream()
                .filter(group -> group.getHandlers().stream().anyMatch(handler -> !this.isHandlerIgnored(handler)))
                .map(group -> Tag.custom().name(group.getName()).description(group.getDescription()).build())
                .collect(Collectors.toList());
    }
}
