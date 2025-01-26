/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.utils;

import modelengine.fit.waterflow.domain.flow.Flow;
import modelengine.fit.waterflow.domain.stream.reactive.Processor;
import modelengine.fit.waterflow.domain.stream.reactive.Publisher;
import modelengine.fit.waterflow.domain.stream.reactive.Subscriber;
import modelengine.fit.waterflow.domain.stream.reactive.Subscription;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 生成Mermaid绘图。
 *
 * @since 1.0
 */
public class Mermaid {
    private static final String TO_ARROW = "-->";

    private static final String DELEGATE_ARROW = "-. delegate .->";

    private static final String EMIT_ARROW = "-. emit .->";

    private static final String LINE_BREAK = System.lineSeparator();

    private static final String START_DISPLAY = "((Start))";

    private static final String END_DISPLAY = "((End))";

    private static final String START_NODE = "start";

    private static final String END_NODE = "end";

    private final String meta;

    private final List<String> subscriptions = new ArrayList<>();

    private final HashMap<Object, String> named = new HashMap<>();

    private final HashMap<Publisher<?>, Subscriber<?, ?>> pairs = new HashMap<>();

    private final AtomicInteger counter = new AtomicInteger();

    /**
     * 使用流程对象初始化 {@link Mermaid}。
     *
     * @param flow 表示流程对象的 {@link Flow}{@code <?>}。
     */
    public Mermaid(Flow<?> flow) {
        String startSubscription = START_NODE + START_DISPLAY + LINE_BREAK;
        named.put(flow.start(), START_NODE);
        buildMermaid(flow, flow.start());
        meta = startSubscription + subscriptions.stream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.joining(LINE_BREAK));
    }

    /**
     * 获取元数据。
     *
     * @return 元数据 {@link String}。
     */
    public String get() {
        return meta;
    }

    private void buildMermaid(Flow<?> flow, Publisher<?> from) {
        for (Object sp : from.getSubscriptions()) {
            Subscription<?> subscription = ObjectUtils.cast(sp);
            Subscriber<?, ?> subscriber = subscription.getTo();
            if (subscriber instanceof Processor) {
                buildNode(flow, from, (Processor<?, ?>) subscriber);
            } else {
                buildEnd(flow, from);
            }
        }
    }

    private String getNodeDisplayName(Flow<?> flow, Processor<?, ?> node) {
        // 不重复命名
        if (this.named.get(node) != null) {
            return StringUtils.EMPTY;
        }
        // 优先使用节点自定义名称
        if (flow.getNode(node.getId()) == null) {
            return node.display().getName();
        } else {
            return StringUtils.format("({0})", node.getId());
        }
    }

    private void buildNode(Flow<?> flow, Publisher<?> from, Processor<?, ?> to) {
        // do not build twice
        if (this.pairs.get(from) == to) {
            return;
        }
        // ignored node, like branch
        if (StringUtils.EMPTY.equals(to.display().getName())) {
            this.named.put(to, this.named.get(from));
            buildMermaid(flow, to);
            return;
        }
        String displayName = getNodeDisplayName(flow, to);
        String nodeName = getNodeName(to);
        subscriptions.add(this.named.get(from) + TO_ARROW + nodeName + displayName);

        this.pairs.put(from, to);
        buildMermaid(flow, to);
        buildSubFlowMermaid(to);
    }

    private String getNodeName(Processor<?, ?> to) {
        return Optional.ofNullable(this.named.get(to)).orElseGet(() -> {
            String name = "node" + this.counter.getAndIncrement();
            this.named.put(to, name);
            return name;
        });
    }

    private void buildSubFlowMermaid(Processor<?, ?> to) {
        Flow<?> subFlow = to.display().getFlow();
        if (subFlow == null) {
            return;
        }
        Publisher<?> start = subFlow.start();

        String startNodeName = "sub_" + START_NODE + counter.getAndIncrement();
        named.put(start, startNodeName);
        String nodeId = to.display().getNodeId();
        if (Objects.equals(nodeId, start.getId())) {
            subscriptions.add(named.get(to) + DELEGATE_ARROW + startNodeName + START_DISPLAY);
        } else {
            Optional<Processor<?, ?>> processor = subFlow.nodes()
                    .stream()
                    .filter(node -> Objects.equals(node.getId(), nodeId))
                    .findFirst();
            Validation.isTrue(processor.isPresent(), "Subflow node id {0} not found.", nodeId);

            Processor<?, ?> delegateNode = processor.get();
            String delegateDisplayName = getNodeDisplayName(subFlow, delegateNode);
            String delegateName = getNodeName(delegateNode);
            named.put(delegateNode, delegateName);
            subscriptions.add(startNodeName + START_DISPLAY);
            subscriptions.add(named.get(to) + DELEGATE_ARROW + delegateName + delegateDisplayName);
        }

        buildMermaid(subFlow, start);
        subscriptions.add(named.get(subFlow.end()) + EMIT_ARROW + named.get(to) + to.display().getName());
    }

    private void buildEnd(Flow<?> flow, Publisher<?> from) {
        if (Objects.equals(pairs.get(from), flow.end())) {
            return;
        }
        String endName = Optional.ofNullable(named.get(flow.end()))
                .orElseGet(() -> END_NODE + counter.getAndIncrement());
        subscriptions.add(named.get(from) + TO_ARROW + endName + END_DISPLAY);

        named.put(flow.end(), endName);
        pairs.put(from, flow.end());
    }
}
