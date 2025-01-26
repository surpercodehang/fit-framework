/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.openapi3.swagger;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.annotation.DocumentIgnored;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.openapi3.swagger.builder.OpenApiBuilder;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanContainer;

import java.util.Map;

/**
 * 表示 OpenAPI 文档的控制器。
 *
 * @author 季聿阶
 * @since 2023-08-15
 */
@Component
public class DocumentController {
    private final OpenApiBuilder openApiBuilder;

    public DocumentController(BeanContainer container) {
        this.openApiBuilder = new OpenApiBuilder(notNull(container, "The bean container cannot be null."));
    }

    /**
     * 获取当前进程所有 REST 接口的 OpenAPI 文档。
     *
     * @return 表示当前进程所有 REST 接口的 OpenAPI 文档的 {@link Map}{@code <}{@link String}{@code , }{@link
     * Object}{@code >}。
     */
    @DocumentIgnored
    @GetMapping("/v3/openapi")
    public Map<String, Object> getDocument() {
        return this.openApiBuilder.build().toJson();
    }
}
