/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.server.handler.GlobalPathPatternPrefixResolver;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link GlobalPathPatternPrefixResolver} 的组合器。
 *
 * @author 季聿阶
 * @since 2023-07-03
 */
public class GlobalPathPatternPrefixResolverComposite implements GlobalPathPatternPrefixResolver {
    private final List<GlobalPathPatternPrefixResolver> resolvers;

    /**
     * 创建全局路径样式的前缀解析器的组合器对象。
     *
     * @param resolvers 表示需要组合的解析器的 {@link GlobalPathPatternPrefixResolver}。
     */
    public GlobalPathPatternPrefixResolverComposite(GlobalPathPatternPrefixResolver... resolvers) {
        GlobalPathPatternPrefixResolver[] actualResolvers =
                ObjectUtils.getIfNull(resolvers, () -> new GlobalPathPatternPrefixResolver[0]);
        this.resolvers = Stream.of(actualResolvers).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Optional<String> resolve() {
        for (GlobalPathPatternPrefixResolver resolver : this.resolvers) {
            Optional<String> resolved = resolver.resolve();
            if (resolved.isPresent()) {
                return resolved;
            }
        }
        return Optional.empty();
    }
}
