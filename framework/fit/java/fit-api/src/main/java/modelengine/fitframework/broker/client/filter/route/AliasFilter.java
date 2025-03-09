/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.broker.client.filter.route;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.GenericableMetadata;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 指定泛服务实现的别名的路由的过滤器。
 *
 * @author 季聿阶
 * @since 2021-06-11
 */
public class AliasFilter extends AbstractFilter {
    private final List<String> aliases;

    /**
     * 使用指定的别名数组来初始化 {@link AliasFilter} 的新实例。
     *
     * @param aliases 表示别名数组的 {@link String}{@code []}。
     * @throws IllegalArgumentException 当过滤后的有效别名列表为空时。
     */
    public AliasFilter(String... aliases) {
        this(Stream.of(ObjectUtils.getIfNull(aliases, () -> new String[0])).collect(Collectors.toList()));
    }

    /**
     * 使用指定的别名列表来初始化 {@link AliasFilter} 的新实例。
     *
     * @param aliases 表示别名列表的 {@link List}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当过滤后的有效别名列表为空时。
     */
    public AliasFilter(List<String> aliases) {
        this.aliases = ObjectUtils.getIfNull(aliases, Collections::<String>emptyList)
                .stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        Validation.isTrue(CollectionUtils.isNotEmpty(this.aliases), "No valid alias to instantiate AliasFilter.");
    }

    /**
     * 根据别名过滤服务实现列表。
     *
     * @param genericable 表示服务的元数据的 {@link GenericableMetadata}。
     * @param toFilterFitables 表示待过滤的服务实现列表的 {@link List}{@code <? extends }{@link FitableMetadata}{@code >}。
     * @param args 表示调用参数的 {@link Object}{@code []}。
     * @param extensions 表示扩展参数的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示过滤后的服务实现列表的 {@link List}{@code <? extends }{@link FitableMetadata}{@code >}。
     */
    @Override
    protected List<? extends FitableMetadata> route(GenericableMetadata genericable,
            List<? extends FitableMetadata> toFilterFitables, Object[] args, Map<String, Object> extensions) {
        return toFilterFitables.stream().filter(this::containsAnyAlias).collect(Collectors.toList());
    }

    /**
     * 检查指定的服务实现是否包含过滤器中的任一别名。
     *
     * @param fitable 表示待检查的服务实现的 {@link FitableMetadata}。
     * @return 表示是否包含任一别名的 {@code boolean}。
     */
    private boolean containsAnyAlias(FitableMetadata fitable) {
        Set<String> theSameAliases = CollectionUtils.intersect(fitable.aliases().all(), this.aliases);
        return !theSameAliases.isEmpty();
    }

    @Override
    public String toString() {
        return "AliasFilter{" + "aliases=" + this.aliases + '}';
    }
}
