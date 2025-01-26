/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.loadbalance;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Target;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 最先满足的环境标调用链的负载均衡策略。
 * <p>该负载均衡策略具体的策略为：
 * <ol>
 *     <li>指定环境标调用链。</li>
 *     <li>依次遍历环境标调用链中的所有环境标，如果待过滤地址中存在满足环境的地址，则直接返回（短路）。</li>
 *     <li>如果环境标调用链中没有环境标被满足，则返回空地址列表。</li>
 * </ol>
 * </p>
 *
 * @author 季聿阶
 * @since 2022-06-06
 */
public class FirstMatchedEnvironmentFilter extends AbstractFilter {
    private final List<String> environmentPrioritySequence;

    public FirstMatchedEnvironmentFilter(List<String> environmentPrioritySequence) {
        this.environmentPrioritySequence = environmentPrioritySequence;
    }

    @Override
    protected List<Target> loadbalance(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets,
            Map<String, Object> extensions) {
        Optional<String> first = this.environmentPrioritySequence.stream()
                .filter(environment -> this.containEnvironment(toFilterTargets, environment))
                .findFirst();
        if (first.isPresent()) {
            return new EnvironmentFilter(first.get()).filter(fitable, localWorkerId, toFilterTargets, extensions);
        }
        return Collections.emptyList();
    }

    private boolean containEnvironment(List<Target> targets, String environment) {
        return targets.stream().map(Target::environment).collect(Collectors.toList()).contains(environment);
    }
}
