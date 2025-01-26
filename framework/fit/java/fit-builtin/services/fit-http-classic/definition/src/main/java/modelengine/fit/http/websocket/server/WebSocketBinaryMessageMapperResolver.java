/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.websocket.server;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.PropertyValueMapperResolver;
import modelengine.fit.http.websocket.annotation.BinaryMessage;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.value.PropertyValue;

import java.util.Optional;

/**
 * 表示 {@link WebSocketBinaryMessageMapper} 的映射器。
 *
 * @author 季聿阶
 * @since 2023-12-11
 */
public class WebSocketBinaryMessageMapperResolver implements PropertyValueMapperResolver {
    private final AnnotationMetadataResolver annotationResolver;

    public WebSocketBinaryMessageMapperResolver(AnnotationMetadataResolver annotationResolver) {
        this.annotationResolver = annotationResolver;
    }

    @Override
    public Optional<PropertyValueMapper> resolve(PropertyValue propertyValue) {
        return notNull(propertyValue, "The property value cannot be null.").getElement()
                .map(this.annotationResolver::resolve)
                .filter(annotations -> annotations.isAnnotationPresent(BinaryMessage.class))
                .map(annotations -> new WebSocketBinaryMessageMapper());
    }
}
