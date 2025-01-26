/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.loadbalance;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Target;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 指定环境标的负载均衡策略。
 *
 * @author 季聿阶
 * @since 2022-06-06
 */
public class EnvironmentFilter extends AbstractFilter {
    private final String environment;

    public EnvironmentFilter(String environment) {
        this.environment = notBlank(environment, "The target environment to filter cannot be blank.");
    }

    @Override
    protected List<Target> loadbalance(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets,
            Map<String, Object> extensions) {
        return toFilterTargets.stream()
                .filter(target -> Objects.equals(target.environment(), this.environment))
                .collect(Collectors.toList());
    }
}
