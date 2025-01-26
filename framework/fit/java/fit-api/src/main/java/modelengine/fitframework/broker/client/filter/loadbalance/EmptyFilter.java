/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.loadbalance;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Target;

import java.util.List;
import java.util.Map;

/**
 * 为 {@link modelengine.fitframework.broker.client.Invoker.Filter} 提供空的实现。
 * <p>不进行任何过滤，直接返回输入的内容。</p>
 *
 * @author 梁济时
 * @since 2022-01-21
 */
public class EmptyFilter extends AbstractFilter {
    /** {@link EmptyFilter} 的预置实例。 */
    public static final EmptyFilter INSTANCE = new EmptyFilter();

    /**
     * 隐藏默认构造方法，如需使用，请直接使用 {@link #INSTANCE 预置实例}。
     */
    private EmptyFilter() {}

    @Override
    protected List<Target> loadbalance(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets,
            Map<String, Object> extensions) {
        return toFilterTargets;
    }
}
