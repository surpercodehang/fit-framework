/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.context.repo.flowsession;

import modelengine.fit.waterflow.domain.context.FlatMapSourceWindow;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.context.Window;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import modelengine.fitframework.inspection.Validation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程运行中 session 相关的数据缓存，用于统一管理这些数据和在 session 完成时统一进行释放。
 *
 * @author 宋永坦
 * @since 2025-02-12
 */
public class FlowSessionRepo {
    private static final Map<String, FlowSessionCache> cache = new ConcurrentHashMap<>();

    /**
     * 获取该 session 的 window 对应的向下一个节点传递数据使用的 session。
     *
     * @param session session。
     * @return 下一个 session。
     */
    public static FlowSession getNextSession(FlowSession session) {
        Validation.notNull(session, "Session cannot be null.");
        return cache.computeIfAbsent(session.getId(), __ -> new FlowSessionCache()).getNextSession(session);
    }

    /**
     * 获取 flatMap 节点生成的 {@link FlatMapSourceWindow}。
     *
     * @param window 进入到 flatMap 节点数据对应的window。
     * @param repo 流程数据上下文的持久化对象。
     * @return 对应的 {@link FlatMapSourceWindow}。
     */
    public static FlatMapSourceWindow getFlatMapSource(Window window, FlowContextRepo repo) {
        Validation.notNull(window, "Window cannot be null.");
        Validation.notNull(window.getSession(), "Session cannot be null.");
        Validation.notNull(repo, "Repo cannot be null.");
        return cache.computeIfAbsent(window.getSession().getId(), __ -> new FlowSessionCache())
                .getFlatMapSourceWindow(window, repo);
    }

    /**
     * 释放 session 下的所有资源。
     *
     * @param session 需要释放资源的 session。
     */
    public static void release(FlowSession session) {
        Validation.notNull(session, "Session cannot be null.");
        cache.remove(session.getId());
    }

    private static class FlowSessionCache {
        /**
         * 记录每个节点向下个节点流转数据时，下个节点使用的 session，用于将同一批数据汇聚。
         * 其中索引为当前节点正在处理数据的窗口的唯一标识。
         */
        private final Map<UUID, FlowSession> nextSessions = new ConcurrentHashMap<>();

        /**
         * 记录流程中经过 flatMap 节点产生的窗口信息，用于将同一批数据汇聚。
         * 其中索引为当前节点正在处理数据的窗口的唯一标识。
         */
        private final Map<UUID, FlatMapSourceWindow> flatMapSourceWindows = new ConcurrentHashMap<>();

        /**
         * 获取该 session 的 window 对应的向下一个节点传递数据使用的 session。
         *
         * @param session session。
         * @return 下一个 session。
         */
        private FlowSession getNextSession(FlowSession session) {
            return this.nextSessions.computeIfAbsent(session.getWindow().key(), __ -> {
                FlowSession next = new FlowSession(session);
                Window nextWindow = next.begin();
                // if the processor is not reduce, then inherit previous window condition
                if (!session.isAccumulator()) {
                    nextWindow.setCondition(session.getWindow().getCondition());
                }
                return next;
            });
        }

        /**
         * 获取 flatMap 节点生成的 {@link FlatMapSourceWindow}。
         *
         * @param window 进入到 flatMap 节点数据对应的window。
         * @param repo 流程数据上下文的持久化对象。
         * @return 对应的 {@link FlatMapSourceWindow}。
         */
        private FlatMapSourceWindow getFlatMapSourceWindow(Window window, FlowContextRepo repo) {
            return this.flatMapSourceWindows.computeIfAbsent(window.key(), __ -> {
                FlatMapSourceWindow newWindow = new FlatMapSourceWindow(window, repo);
                newWindow.setSession(new FlowSession(window.getSession().preserved()));
                newWindow.getSession().setWindow(newWindow);
                newWindow.getSession().begin();
                return newWindow;
            });
        }
    }
}
