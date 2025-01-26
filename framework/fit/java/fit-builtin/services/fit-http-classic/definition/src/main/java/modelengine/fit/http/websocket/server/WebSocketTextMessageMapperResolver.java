/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket.server;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.websocket.annotation.TextMessage;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示 {@link WebSocketTextMessageMapper} 的映射器。
 *
 * @author 季聿阶
 * @since 2023-12-11
 */
public class WebSocketTextMessageMapperResolver implements PropertyValueMapperResolver {
    private final AnnotationMetadataResolver annotationResolver;

    public WebSocketTextMessageMapperResolver(AnnotationMetadataResolver annotationResolver) {
        this.annotationResolver = annotationResolver;
    }

    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        return notNull(propertyValue, "The property value cannot be null.").getElement()
                .map(this.annotationResolver::resolve)
                .filter(annotations -> annotations.isAnnotationPresent(TextMessage.class))
                .map(annotations -> new WebSocketTextMessageMapper());
    }
}
