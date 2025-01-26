/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.engine.operators.patterns;

import modelengine.fel.core.pattern.Pattern;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.flows.ConverseLatch;
import modelengine.fel.engine.util.AiFlowSession;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.context.Window;
import modelengine.fit.waterflow.domain.emitters.EmitterListener;
import modelengine.fit.waterflow.domain.flow.Flow;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.LazyLoader;

/**
 * 流程委托单元。
 *
 * @author 刘信宏
 * @since 2024-06-04
 */
public abstract class AbstractFlowPattern<I, O> implements FlowPattern<I, O> {
    private final LazyLoader<AiProcessFlow<I, O>> flowSupplier;

    protected AbstractFlowPattern() {
        this.flowSupplier = LazyLoader.of(this::buildFlow);
    }

    /**
     * 构造处理流程。
     *
     * @return 表示数据处理流程的 {@code <}{@link AiProcessFlow}{@code <}{@link I}{@code , }{@link O}{@code >}。
     */
    protected abstract AiProcessFlow<I, O> buildFlow();

    @Override
    public void register(EmitterListener<O, FlowSession> handler) {
        if (handler != null) {
            this.getFlow().register(handler);
        }
    }

    @Override
    public void emit(O data, FlowSession session) {
        FlowSession flowSession = new FlowSession(session);
        this.getFlow().emit(data, flowSession);
    }

    @Override
    public O invoke(I data) {
        this.getFlow().converse(AiFlowSession.require()).offer(data);
        return null;
    }

    /**
     * 获取同步委托单元。
     *
     * @return 表示同步委托单元的 {@link Pattern}{@code <}{@link I}{@code , }{@link O}{@code >}。
     * @throws IllegalStateException 当流程发生异常时。
     */
    public Pattern<I, O> sync() {
        return new SimplePattern<>(data -> {
            FlowSession require = AiFlowSession.require();
            FlowSession session = new FlowSession();
            Window window = session.begin();
            session.copySessionState(require);
            ConverseLatch<O> conversation = this.getFlow().converse(session).offer(data);
            window.complete();
            return conversation.await();
        });
    }

    /**
     * 获取被装饰的流程对象。
     *
     * @return 表示被装饰流程对象的 {@link Flow}{@code <}{@link I}{@code >}。
     */
    public Flow<I> origin() {
        return this.getFlow().origin();
    }

    private AiProcessFlow<I, O> getFlow() {
        return Validation.notNull(this.flowSupplier.get(), "The flow cannot be null.");
    }
}
