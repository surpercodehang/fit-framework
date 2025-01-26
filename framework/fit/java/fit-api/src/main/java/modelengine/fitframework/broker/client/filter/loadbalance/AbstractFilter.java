/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.loadbalance;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Invoker.Filter} 的抽象实现。
 *
 * @author 季聿阶
 * @since 2022-03-22
 */
public abstract class AbstractFilter implements Invoker.Filter {
    @Override
    public List<Target> filter(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets,
            Map<String, Object> extensions) {
        notNull(fitable, "The fitable metadata to balance load cannot be null.");
        notBlank(localWorkerId,
                "The local worker id to balance load cannot be blank. [genericableId={0}, fitableId={1}]",
                fitable.genericable().id(),
                fitable.id());
        notNull(toFilterTargets,
                "The targets to balance load cannot be null. [genericableId={0}, fitableId={1}]",
                fitable.genericable().id(),
                fitable.id());
        for (Target target : toFilterTargets) {
            notNull(target,
                    "The target to balance load cannot be null. [genericableId={0}, fitableId={1}]",
                    fitable.genericable().id(),
                    fitable.id());
        }
        return this.loadbalance(fitable,
                localWorkerId,
                toFilterTargets,
                ObjectUtils.getIfNull(extensions, HashMap::new));
    }

    /**
     * 进行调用地址过滤。
     *
     * @param fitable 表示待过滤服务地址所属服务实现的元数据的 {@link FitableMetadata}。
     * <p><b>该参数一定不为 {@code null}。</b></p>
     * @param localWorkerId 表示本地进程的唯一标识的 {@link String}。
     * <p><b>该参数一定不为 {@code null} 且一定不为空白字符串。</b></p>
     * @param toFilterTargets 表示待过滤服务地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     * <p><b>该参数一定不为 {@code null}，且不包含为 {@code null} 的元素。</b></p>
     * @param extensions 表示负载均衡所需扩展信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * <p><b>该参数一定不为 {@code null}，且不包含键或值为 {@code null} 的元素。</b></p>
     * @return 表示过滤后的服务地址列表的 {@link List}{@code <}{@link Target}{@code >}。
     */
    protected abstract List<Target> loadbalance(FitableMetadata fitable, String localWorkerId,
            List<Target> toFilterTargets, Map<String, Object> extensions);
}
