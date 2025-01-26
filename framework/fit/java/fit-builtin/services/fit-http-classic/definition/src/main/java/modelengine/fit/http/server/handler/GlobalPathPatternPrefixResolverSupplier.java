/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fitframework.ioc.BeanContainer;

/**
 * 表示 {@link GlobalPathPatternPrefixResolver} 的提供者。
 *
 * @author 季聿阶
 * @since 2023-06-11
 */
@FunctionalInterface
public interface GlobalPathPatternPrefixResolverSupplier {
    /**
     * 从指定容器中获取全局路径样式的前缀的解析器。
     *
     * @param container 表示指定容器的 {@link BeanContainer}。
     * @return 表示从指定容器中获取到的全局路径样式的前缀的解析器的 {@link GlobalPathPatternPrefixResolver}。
     */
    GlobalPathPatternPrefixResolver get(BeanContainer container);
}
