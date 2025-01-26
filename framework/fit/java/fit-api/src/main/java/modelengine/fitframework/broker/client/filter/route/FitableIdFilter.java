/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 指定泛服务实现唯一标识的路由的过滤器。
 *
 * @author 季聿阶
 * @since 2021-06-11
 */
public class FitableIdFilter extends AbstractFilter {
    private final List<String> fitableIds;

    public FitableIdFilter(String... fitableIds) {
        this(Stream.of(ObjectUtils.getIfNull(fitableIds, () -> new String[0])).collect(Collectors.toList()));
    }

    /**
     * 创建唯一标识的路由过滤器。
     *
     * @param fitableIds 表示用于初始化过滤器的服务实现标识集合的 {@link List}{@code <}{@link String}{@code >}。
     */
    public FitableIdFilter(List<String> fitableIds) {
        this.fitableIds = ObjectUtils.getIfNull(fitableIds, Collections::<String>emptyList)
                .stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        Validation.isTrue(CollectionUtils.isNotEmpty(this.fitableIds),
                "No valid fitable id to instantiate FitableIdFilter.");
    }

    @Override
    protected List<? extends FitableMetadata> route(GenericableMetadata genericable,
            List<? extends FitableMetadata> toFilterFitables, Object[] args, Map<String, Object> extensions) {
        return toFilterFitables.stream()
                .filter(toFilterFitable -> this.containsAnyFitableId(toFilterFitable, this.fitableIds))
                .collect(Collectors.toList());
    }

    private boolean containsAnyFitableId(FitableMetadata toFilterFitable, List<String> fitableIds) {
        Validation.notNull(fitableIds, "The fitable ids cannot be null.");
        return fitableIds.stream()
                .filter(StringUtils::isNotBlank)
                .anyMatch(fitableId -> Objects.equals(toFilterFitable.id(), fitableId));
    }

    @Override
    public String toString() {
        return "FitableIdFilter{" + "fitableIds=" + this.fitableIds + '}';
    }
}
