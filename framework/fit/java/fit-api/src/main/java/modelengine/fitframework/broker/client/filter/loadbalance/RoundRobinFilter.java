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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询的负载均衡策略。
 * <p>该轮询算法实现仅为简单实现：
 * <ul>限制条件：
 *      <li>假定相同的服务实现的待过滤地址列表都是排序的，这样在服务地址变化不大的情况下，其结果是趋近轮询的。</li>
 * </ul>
 * </p>
 *
 * @author 季聿阶
 * @since 2021-06-11
 */
public class RoundRobinFilter extends ChampionFilter {
    /** {@link RoundRobinFilter} 的预置实例。 */
    public static final RoundRobinFilter INSTANCE = new RoundRobinFilter();

    private final Map<String, AtomicInteger> fitablePositions = new ConcurrentHashMap<>();

    /**
     * 隐藏默认构造方法，如需使用，请直接使用 {@link #INSTANCE 预置实例}。
     */
    private RoundRobinFilter() {}

    @Override
    protected Optional<Target> select(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets) {
        String key = this.getRoundRobinKey(fitable);
        AtomicInteger currentPosition = this.fitablePositions.computeIfAbsent(key, fitableKey -> new AtomicInteger());
        int position = currentPosition.getAndIncrement() % toFilterTargets.size();
        return Optional.of(toFilterTargets.get(position));
    }

    private String getRoundRobinKey(FitableMetadata fitable) {
        return fitable.genericable().id() + ":" + fitable.id();
    }
}
