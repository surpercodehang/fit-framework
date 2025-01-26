/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.route;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.GenericableMetadata;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Router} 的抽象实现。
 *
 * @author 季聿阶
 * @since 2022-03-22
 */
public abstract class AbstractFilter implements Router.Filter {
    @Override
    public List<? extends FitableMetadata> filter(GenericableMetadata genericable,
            List<? extends FitableMetadata> toFilterFitables, Object[] args, Map<String, Object> extensions) {
        notNull(genericable, "The genericable metadata to filter cannot be null.");
        notNull(toFilterFitables, "The metadata of fitables to filter cannot be null.");
        for (FitableMetadata toFilterFitable : toFilterFitables) {
            notNull(toFilterFitable, "The fitable metadata to filter cannot be null.");
            Validation.equals(toFilterFitable.genericable(),
                    genericable,
                    "The genericable metadata of fitable is mismatch. "
                            + "[requiredGenericableId={0}, mismatchedGenericableId={1}, mismatchedFitableId={2}]",
                    genericable.id(),
                    toFilterFitable.genericable().id(),
                    toFilterFitable.id());
        }
        return this.route(genericable, toFilterFitables, args, ObjectUtils.getIfNull(extensions, HashMap::new));
    }

    /**
     * 进行路由过滤。
     *
     * @param genericable 表示待过滤的泛服务元数据的 {@link GenericableMetadata}。
     * @param toFilterFitables 表示待过滤的泛服务实现元数据列表的 {@link List}{@code <}{@link
     * FitableMetadata}{@code >}。
     * <p><b>该参数一定不为 {@code null}，且不包含为 {@code null} 的元素。</b></p>
     * @param args 表示实际调用参数的 {@link Object}{@code []}。
     * @param extensions 表示负载均衡所需扩展信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * <p><b>该参数一定不为 {@code null}，且不包含键或值为 {@code null} 的元素。</b></p>
     * @return 表示过滤后的泛服务实现列表的 {@link List}{@code <}{@link FitableMetadata}{@code >}。
     */
    protected abstract List<? extends FitableMetadata> route(GenericableMetadata genericable,
            List<? extends FitableMetadata> toFilterFitables, Object[] args, Map<String, Object> extensions);
}
