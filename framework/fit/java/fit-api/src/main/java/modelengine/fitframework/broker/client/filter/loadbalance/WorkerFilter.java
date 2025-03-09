/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.broker.client.filter.loadbalance;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 指定进程唯一标识的负载均衡策略。
 *
 * @author 季聿阶
 * @since 2021-08-26
 */
public class WorkerFilter extends ChampionFilter {
    private final String workerId;

    /**
     * 使用指定的进程标识初始化 {@link WorkerFilter} 的新实例。
     *
     * @param workerId 表示进程标识的 {@link String}。
     * @throws IllegalArgumentException 当 {@code workerId} 为 {@code null} 或空白时。
     */
    public WorkerFilter(String workerId) {
        this.workerId = notBlank(workerId, "The target worker id to filter cannot be blank.");
    }

    @Override
    protected Optional<Target> select(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets) {
        return toFilterTargets.stream()
                .filter(target -> StringUtils.equals(target.workerId(), this.workerId))
                .findFirst();
    }
}
