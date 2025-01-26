/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.ioc.BeanContainer;

import java.util.Optional;

/**
 * 表示从配置项 {@code 'server.http.context-path'} 中获取全局路径样式的前缀解析器。
 *
 * @author 季聿阶
 * @since 2023-07-03
 */
public class FitGlobalPathPatternPrefixResolver implements GlobalPathPatternPrefixResolver {
    private final String prefix;

    FitGlobalPathPatternPrefixResolver(BeanContainer container) {
        notNull(container, "The bean container cannot be null.");
        this.prefix = container.plugin().config().get("server.http.context-path", String.class);
    }

    @Override
    public Optional<String> resolve() {
        return Optional.ofNullable(this.prefix);
    }
}
