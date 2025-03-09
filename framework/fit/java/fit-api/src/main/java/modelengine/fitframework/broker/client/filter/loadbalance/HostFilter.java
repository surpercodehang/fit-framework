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
 * 指定主机地址的负载均衡策略。
 *
 * @author 张浩亮
 * @author 季聿阶
 * @since 2021-09-26
 */
public class HostFilter extends ChampionFilter {
    private final String host;

    /**
     * 使用指定的主机地址初始化 {@link HostFilter} 的新实例。
     *
     * @param host 表示目标主机地址的 {@link String}。
     * @throws IllegalArgumentException 当 {@code host} 为空或空白时。
     */
    public HostFilter(String host) {
        this.host = notBlank(host, "The target host to filter cannot be blank.");
    }

    @Override
    protected Optional<Target> select(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets) {
        return toFilterTargets.stream().filter(target -> StringUtils.equals(target.host(), this.host)).findFirst();
    }
}
