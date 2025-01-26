/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.loadbalance;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Target;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * 随机的负载均衡策略。
 *
 * @author 季聿阶
 * @since 2021-06-11
 */
public class RandomFilter extends ChampionFilter {
    private final Random random = new SecureRandom();

    @Override
    protected Optional<Target> select(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets) {
        return Optional.of(toFilterTargets.get(this.random.nextInt(toFilterTargets.size())));
    }
}
