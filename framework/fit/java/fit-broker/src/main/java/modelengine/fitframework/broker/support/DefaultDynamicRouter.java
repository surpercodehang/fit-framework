/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.DynamicRouter;
import modelengine.fitframework.broker.Fitable;
import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示 {@link DynamicRouter} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-26
 */
public class DefaultDynamicRouter implements DynamicRouter {
    @Override
    public List<Fitable> route(Genericable genericable, InvocationContext context, Object[] args) {
        if (context.routingFilter() == null) {
            return genericable.fitables();
        }
        return context.routingFilter().filter(genericable, genericable.fitables(), args, context.filterExtensions())
                .stream()
                .filter(Objects::nonNull)
                .map(metadata -> this.cast(metadata, genericable.fitables()))
                .collect(Collectors.toList());
    }

    private Fitable cast(FitableMetadata metadata, List<Fitable> scope) {
        if (metadata instanceof Fitable) {
            return ObjectUtils.cast(metadata);
        }
        for (Fitable fitable : scope) {
            if (Objects.equals(metadata.toUniqueId(), fitable.toUniqueId())) {
                return fitable;
            }
        }
        throw new IllegalStateException(StringUtils.format(
                "Failed to cast FitableMetadata to Fitable after dynamic routing. [id={0}]",
                metadata.toUniqueId()));
    }
}
