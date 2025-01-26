/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.support;

import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.FitRuntimeFailedEvent;
import modelengine.fitframework.runtime.FitRuntimePreparedEvent;
import modelengine.fitframework.runtime.FitRuntimeStartedEvent;

import java.time.Duration;

/**
 * 定义运行时相关的事件。
 *
 * @author 梁济时
 * @since 2023-05-17
 */
final class Events {
    private Events() {}

    private static final class Prepared implements FitRuntimePreparedEvent {
        private final FitRuntime runtime;
        private final Duration duration;

        private Prepared(FitRuntime runtime, Duration duration) {
            this.runtime = runtime;
            this.duration = duration;
        }

        @Override
        public FitRuntime runtime() {
            return this.runtime;
        }

        @Override
        public Duration duration() {
            return this.duration;
        }
    }

    private static final class Started implements FitRuntimeStartedEvent {
        private final FitRuntime runtime;
        private final Duration duration;

        private Started(FitRuntime runtime, Duration duration) {
            this.runtime = runtime;
            this.duration = duration;
        }

        @Override
        public FitRuntime runtime() {
            return this.runtime;
        }

        @Override
        public Duration duration() {
            return this.duration;
        }
    }

    private static final class Failed implements FitRuntimeFailedEvent {
        private final FitRuntime runtime;
        private final Throwable cause;

        private Failed(FitRuntime runtime, Throwable cause) {
            this.runtime = runtime;
            this.cause = cause;
        }

        @Override
        public FitRuntime runtime() {
            return this.runtime;
        }

        @Override
        public Throwable cause() {
            return this.cause;
        }
    }

    /**
     * 获取 FIT 运行时环境已准备就绪的事件。
     *
     * @param runtime 表示 FIT 运行时环境的 {@link FitRuntime}。
     * @param duration 表示 FIT 运行时环境准备就绪的时长的 {@link Duration}。
     * @return 表示 FIT 运行时环境已准备就绪的事件的 {@link FitRuntimePreparedEvent}。
     */
    static FitRuntimePreparedEvent prepared(FitRuntime runtime, Duration duration) {
        return new Prepared(runtime, duration);
    }

    /**
     * 获取 FIT 运行时环境已启动的事件。
     *
     * @param runtime 表示 FIT 运行时环境的 {@link FitRuntime}。
     * @param duration 表示 FIT 运行时环境启动的时长的 {@link Duration}。
     * @return 表示 FIT 运行时环境已启动的事件的 {@link FitRuntimeStartedEvent}。
     */
    static FitRuntimeStartedEvent started(FitRuntime runtime, Duration duration) {
        return new Started(runtime, duration);
    }

    /**
     * 获取 FIT 运行时环境启动失败的事件。
     *
     * @param runtime 表示 FIT 运行时环境的 {@link FitRuntime}。
     * @param cause 表示 FIT 运行时环境启动失败的原因的 {@link Throwable}。
     * @return 表示 FIT 运行时环境启动失败的事件的 {@link FitRuntimeFailedEvent}。
     */
    static FitRuntimeFailedEvent failed(FitRuntime runtime, Throwable cause) {
        return new Failed(runtime, cause);
    }
}
