/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.stream.nodes;

import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.domain.enums.FlowNodeType;
import modelengine.fit.waterflow.domain.enums.ParallelMode;
import modelengine.fit.waterflow.domain.stream.operators.Operators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Join节点，前面一定有一个parallel节点，不然filter无法过滤出可以处理的数据
 * Join节点只能配置reduce处理器
 * neither：就是用reduce处理map的方式
 * all：就是典型的n->1 reduce模式
 * 辉子 2019-12-17
 *
 * @param <I> 传入数据类型
 * @param <O> 处理完数据类型
 * @author 高诗意
 * @since 1.0
 */
public class JoinNode<I, O> extends Node<I, O> {
    /**
     * n->1 处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public JoinNode(String streamId, Operators.Map<FlowContext<I>, O> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        super(streamId, processor, repo, messenger, locks);
        // 找到属于同一个parallel的
        this.postFilter((contexts) -> {
            List<String> parallels = contexts.stream()
                    .map(FlowContext::getParallel)
                    .distinct()
                    .collect(Collectors.toList());
            for (final String id1 : parallels) { // 找到属于同一个parallel的
                List<FlowContext<I>> cs = contexts.stream()
                        .filter(c -> c.getParallel().equals(id1))
                        .collect(Collectors.toList());
                if (cs.get(0).getParallelMode().equals(ParallelMode.EITHER.name())) {
                    return cs;
                }

                if (cs.get(0).getParallelMode().equals(ParallelMode.ALL.name()) && cs.size() == froms.size()) {
                    return cs;
                }
            }
            return new ArrayList<>();
        });
    }

    /**
     * n->1 处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public JoinNode(String streamId, String nodeId, Operators.Map<FlowContext<I>, O> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        this(streamId, processor, repo, messenger, locks);
        this.id = nodeId;
        this.nodeType = nodeType;
    }

    /**
     * join节点做事后处理，从join节点出去后，context就只有一个了
     * join之后的上下文要立刻保存为archived状态，然后在生成一个新的为后续使用
     *
     * @param preList join节点处理之前的context集合
     * @param afterList join节点处理之后的context集合，实际只有一个元素
     */
    @Override
    public void afterProcess(List<FlowContext<I>> preList, List<FlowContext<O>> afterList) {
        preList.forEach(c -> c.join(true));
        afterList.forEach(c -> c.setParallel("").setParallelMode(""));
        super.afterProcess(preList, afterList);
    }
}
