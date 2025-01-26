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

/**
 * 只选择一个调用地址的负载均衡策略。
 *
 * @author 季聿阶
 * @since 2021-06-17
 */
public abstract class ChampionFilter extends AbstractFilter {
    @Override
    public List<Target> loadbalance(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets,
            Map<String, Object> extensions) {
        if (toFilterTargets.isEmpty()) {
            return Collections.emptyList();
        }
        return this.select(fitable, localWorkerId, toFilterTargets)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    /**
     * 选择一个调用地址。
     *
     * @param fitable 表示待过滤服务元数据的 {@link FitableMetadata}。
     * <p><b>该参数一定不为 {@code null}。</b></p>
     * @param localWorkerId 表示本地进程的唯一标识的 {@link String}。
     * <p><b>该参数一定不为 {@code null}，也一定不为空白字符串。</b></p>
     * @param toFilterTargets 表示待过滤服务的相关地址信息的 {@link List}{@code <}{@link Target}{@code >}。
     * <p><b>该参数一定不为 {@code null}，也一定不包含为 {@code null} 的地址，且至少有一个可选地址存在。</b></p>
     * @return 表示过滤后的服务的相关地址信息的 {@link Optional}{@code <}{@link Target}{@code >}。
     */
    protected abstract Optional<Target> select(FitableMetadata fitable, String localWorkerId,
            List<Target> toFilterTargets);
}
