/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.flow;

import lombok.Setter;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMemoMessenger;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMemoRepo;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.domain.context.repo.flowlock.FlowLocksMemo;
import modelengine.fit.waterflow.domain.emitters.Emitter;
import modelengine.fit.waterflow.domain.states.DataStart;
import modelengine.fit.waterflow.domain.states.Start;

/**
 * 用于工程师编程交互的Flow API集合
 * 通过使用Flows里的Function++能力，可以代码简洁轻松创建非常复杂的流处理的函数链
 * 辉子 2019-10-31
 *
 * @author 高诗意
 * @since 1.0
 */
public final class Flows {
    /**
     * stream持久化接口
     */
    @Setter
    private static FlowContextRepo repo;

    /**
     * 消息接口
     */
    @Setter
    private static FlowContextMessenger messenger;

    /**
     * 流程锁
     */
    @Setter
    private static FlowLocks locks;

    static {
        setRepo(new FlowContextMemoRepo());
        setMessenger(new FlowContextMemoMessenger());
        setLocks(new FlowLocksMemo());
    }

    /**
     * 先建立流程，然后通过offer灌入数据进行处理
     *
     * @param <D> 要处理的数据类型
     * @return 处理流头结点
     */
    public static <D> Start<D, D, D, ProcessFlow<D>> create() {
        ProcessFlow<D> flow = new ProcessFlow<>(repo, messenger, locks);
        return new Start<>(flow.start, flow);
    }

    /**
     * 先建立流程，然后通过offer灌入数据进行处理
     *
     * @param <D> 要处理的数据类型
     * @param repo 上下文处理类
     * @param messenger 事件发送类
     * @param locks 流程锁
     * @return 处理流头结点
     */
    public static <D> Start<D, D, D, ProcessFlow<D>> create(FlowContextRepo repo, FlowContextMessenger messenger,
            FlowLocks locks) {
        ProcessFlow<D> flow = new ProcessFlow<>(repo, messenger, locks);
        return new Start<>(flow.start, flow);
    }

    /**
     * 基于单条数据建立流，然后通过offer开始处理数据
     *
     * @param data 数据
     * @param <D> 要生产的数据类型
     * @return 处理流头结点
     */
    public static <D> DataStart<D, D, D> mono(D data) {
        Start<D, D, D, ProcessFlow<D>> start = Flows.create();
        return new DataStart<>(start, data);
    }

    /**
     * 基于LIST数据建立流，然后通过offer开始处理数据
     *
     * @param data 数据
     * @param <D> 要生产的数据类型
     * @return 处理流头结点
     */
    public static <D> DataStart<D, D, D> flux(D... data) {
        Start<D, D, D, ProcessFlow<D>> start = Flows.create();
        return new DataStart<>(start, data);
    }

    /**
     * 提供一个emitter作为数据源，通过该数据源来offer数据
     *
     * @param emitter 数据源
     * @param <D> emitter生产的数据类型
     * @return 处理流头结点
     */
    public static <D> DataStart<D, D, D> source(Emitter<D, FlowSession> emitter) {
        Start<D, D, D, ProcessFlow<D>> start = Flows.create();
        return new DataStart<>(start, emitter);
    }
}
