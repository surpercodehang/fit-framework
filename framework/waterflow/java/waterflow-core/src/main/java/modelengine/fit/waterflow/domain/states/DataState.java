/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.states;

import modelengine.fit.waterflow.domain.common.Constants;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.context.Window;
import modelengine.fit.waterflow.domain.flow.ProcessFlow;
import modelengine.fit.waterflow.domain.utils.SleepUtil;
import modelengine.fitframework.util.ObjectUtils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据前置的state节点
 *
 * @param <O> 出参数据类型
 * @param <D> 原始数据类型
 * @param <I> 入参数据类型
 * @since 1.0
 */
public class DataState<O, D, I> extends DataStart<O, D, I> {
    /**
     * 数据前置的state节点
     *
     * @param state 基于用户的processor的节点数据处理逻辑
     * @param start 指定的DataStart
     */
    public DataState(State<O, D, I, ProcessFlow<D>> state, DataStart<D, D, D> start) {
        super(state);
        this.start = start;
    }

    /**
     * 完成流程定义，并发射一个数据
     */
    @Override
    public void offer() {
        this.offer(new FlowSession());
    }

    /**
     * 指定session的offer
     *
     * @param session 指定的session
     */
    public void offer(FlowSession session) {
        ObjectUtils.<State<O, D, I, ProcessFlow<D>>>cast(this.state).close();
        this.start.offer(session);
    }

    /**
     * 指定window的offer
     *
     * @param window 指定的window
     */
    public void offer(Window window) {
        FlowSession session = new FlowSession();
        session.setWindow(window);
        session.setId(Constants.FROM_FLATMAP);
        this.offer(session);
    }

    /**
     * 启动流程并同步等待结果
     *
     * @return 流程处理的最终结果
     */
    public O invoke() {
        AtomicReference<O> result = new AtomicReference<>();
        ObjectUtils.<State<O, D, I, ProcessFlow<D>>>cast(this.state)
                .close(callback -> result.set(callback.get().getData()));
        this.start.offer();
        SleepUtil.waitUntil(() -> result.get() != null, 1000);
        return result.get();
    }

    /**
     * 跳转到指定节点，使用节点的别名id来标识一个节点
     * <p>
     * 类似于goto，通常在conditions的分支中使用，用于形成循环
     * </p>
     * <p>
     * 包装了{@link State#to(String)}的实现
     * </p>
     *
     * @param id 节点的别名id，通常使用 {@link State#id(String)} 指定
     */
    public void to(String id) {
        ObjectUtils.<State>cast(this.state).to(id);
    }

    /**
     * 跳转到指定节点
     * <p>
     * 类似于goto，通常在conditions的分支中使用，用于形成循环
     * </p>
     * <p>
     * 包装了{@link State#to(State)}的实现
     * </p>
     *
     * @param state 指定的节点
     */
    public void to(DataState<?, D, O> state) {
        ObjectUtils.<State<O, D, I, ProcessFlow<D>>>cast(this.state)
                .to(ObjectUtils.<State<?, D, O, ProcessFlow<D>>>cast(state.state));
    }
}