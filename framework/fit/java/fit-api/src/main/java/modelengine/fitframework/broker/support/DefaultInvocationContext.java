/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

/**
 * 表示 {@link InvocationContext} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-03-31
 */
public class DefaultInvocationContext implements InvocationContext {
    private final String genericableId;
    private final boolean isMicro;
    private final Method genericableMethod;
    private final Router.Filter routingFilter;
    private final Invoker.Filter loadBalanceFilter;
    private final List<UniqueFitableId> loadBalanceWith;
    private final String localWorkerId;
    private final String appName;
    private final List<String> environmentPrioritySequence;
    private final String specifiedEnvironment;
    private final int retry;
    private final long timeout;
    private final TimeUnit timeoutUnit;
    private final CommunicationProtocol protocol;
    private final SerializationFormat format;
    private final boolean isGeneric;
    private final boolean isMulticast;
    private final CommunicationType communicationType;
    private final boolean withDegradation;
    private final BinaryOperator<Object> accumulator;
    private final Map<String, Object> extensions;

    private DefaultInvocationContext(String genericableId, boolean isMicro, Method genericableMethod,
            Router.Filter routingFilter, Invoker.Filter loadBalanceFilter, List<UniqueFitableId> loadBalanceWith,
            String localWorkerId, String appName, List<String> environmentPrioritySequence, String specifiedEnvironment,
            int retry, long timeout, TimeUnit timeoutUnit, CommunicationProtocol protocol, SerializationFormat format,
            boolean isGeneric, boolean isMulticast, CommunicationType communicationType, boolean withDegradation,
            BinaryOperator<Object> accumulator, Map<String, Object> extensions) {
        this.genericableId = genericableId;
        this.isMicro = isMicro;
        this.genericableMethod = genericableMethod;
        this.routingFilter = routingFilter;
        this.loadBalanceFilter = loadBalanceFilter;
        this.loadBalanceWith = Collections.unmodifiableList(loadBalanceWith);
        this.localWorkerId = localWorkerId;
        this.appName = appName;
        this.environmentPrioritySequence = Collections.unmodifiableList(environmentPrioritySequence);
        this.specifiedEnvironment = specifiedEnvironment;
        this.retry = retry;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.protocol = protocol;
        this.format = format;
        this.isGeneric = isGeneric;
        this.isMulticast = isMulticast;
        this.communicationType = communicationType;
        this.withDegradation = withDegradation;
        this.accumulator = accumulator;
        this.extensions = extensions;
    }

    @Override
    public String genericableId() {
        return this.genericableId;
    }

    @Override
    public boolean isMicro() {
        return this.isMicro;
    }

    @Override
    public Method genericableMethod() {
        return this.genericableMethod;
    }

    @Override
    public Router.Filter routingFilter() {
        return this.routingFilter;
    }

    @Override
    public Invoker.Filter loadBalanceFilter() {
        return this.loadBalanceFilter;
    }

    @Override
    public List<UniqueFitableId> loadBalanceWith() {
        return this.loadBalanceWith;
    }

    @Override
    public String localWorkerId() {
        return this.localWorkerId;
    }

    @Override
    public String appName() {
        return this.appName;
    }

    @Override
    public List<String> environmentPrioritySequence() {
        return this.environmentPrioritySequence;
    }

    @Override
    public String specifiedEnvironment() {
        return this.specifiedEnvironment;
    }

    @Override
    public int retry() {
        return this.retry;
    }

    @Override
    public long timeout() {
        return this.timeout;
    }

    @Override
    public TimeUnit timeoutUnit() {
        return this.timeoutUnit;
    }

    @Override
    public CommunicationProtocol protocol() {
        return this.protocol;
    }

    @Override
    public SerializationFormat format() {
        return this.format;
    }

    @Override
    public boolean isGeneric() {
        return this.isGeneric;
    }

    @Override
    public boolean isMulticast() {
        return this.isMulticast;
    }

    @Override
    public CommunicationType communicationType() {
        return this.communicationType;
    }

    @Override
    public boolean withDegradation() {
        return this.withDegradation;
    }

    @Override
    public BinaryOperator<Object> accumulator() {
        return this.accumulator;
    }

    @Override
    public Map<String, Object> filterExtensions() {
        return this.extensions;
    }

    /**
     * 表示 {@link InvocationContext.Builder} 的默认实现。
     */
    public static class Builder implements InvocationContext.Builder {
        private String genericableId;
        private boolean isMicro;
        private Method genericableMethod;
        private Router.Filter routingFilter;
        private Invoker.Filter loadBalanceFilter;
        private final List<UniqueFitableId> loadBalanceWith = new ArrayList<>();
        private String localWorkerId;
        private String appName;
        private final List<String> environmentPrioritySequence = new ArrayList<>();
        private String specifiedEnvironment;
        private int retry;
        private long timeout;
        private TimeUnit timeoutUnit;
        private CommunicationProtocol protocol = CommunicationProtocol.UNKNOWN;
        private SerializationFormat format = SerializationFormat.UNKNOWN;
        private boolean isGeneric;
        private boolean isMulticast;
        private boolean withDegradation;
        private BinaryOperator<Object> accumulator;
        private CommunicationType communicationType = CommunicationType.DEFAULT;
        private Map<String, Object> filterExtensions = new HashMap<>();

        /**
         * 使用已知的调用上下文初始化 {@link DefaultInvocationContext.Builder} 类的新实例。
         *
         * @param context 表示已知的调用上下文的 {@link InvocationContext}。
         */
        public Builder(InvocationContext context) {
            if (context != null) {
                this.genericableId = context.genericableId();
                this.isMicro = context.isMicro();
                this.genericableMethod = context.genericableMethod();
                this.routingFilter = context.routingFilter();
                this.loadBalanceFilter = context.loadBalanceFilter();
                Optional.ofNullable(context.loadBalanceWith()).ifPresent(this.loadBalanceWith::addAll);
                this.localWorkerId = context.localWorkerId();
                this.appName = context.appName();
                Optional.ofNullable(context.environmentPrioritySequence())
                        .ifPresent(this.environmentPrioritySequence::addAll);
                this.specifiedEnvironment = context.specifiedEnvironment();
                this.retry = context.retry();
                this.timeout = context.timeout();
                this.timeoutUnit = context.timeoutUnit();
                this.protocol = context.protocol();
                this.format = context.format();
                this.isGeneric = context.isGeneric();
                this.isMulticast = context.isMulticast();
                this.communicationType = context.communicationType();
                this.withDegradation = context.withDegradation();
                this.accumulator = context.accumulator();
            }
        }

        @Override
        public InvocationContext.Builder genericableId(String genericableId) {
            this.genericableId = genericableId;
            return this;
        }

        @Override
        public InvocationContext.Builder isMicro(boolean isMicro) {
            this.isMicro = isMicro;
            return this;
        }

        @Override
        public InvocationContext.Builder genericableMethod(Method genericableMethod) {
            this.genericableMethod = genericableMethod;
            return this;
        }

        @Override
        public InvocationContext.Builder routingFilter(Router.Filter filter) {
            this.routingFilter = filter;
            return this;
        }

        @Override
        public InvocationContext.Builder loadBalanceFilter(Invoker.Filter filter) {
            this.loadBalanceFilter = filter;
            return this;
        }

        @Override
        public InvocationContext.Builder loadBalanceWith(List<UniqueFitableId> ids) {
            this.loadBalanceWith.clear();
            if (ids != null) {
                this.loadBalanceWith.addAll(ids);
            }
            return this;
        }

        @Override
        public InvocationContext.Builder localWorkerId(String workerId) {
            this.localWorkerId = workerId;
            return this;
        }

        @Override
        public InvocationContext.Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        @Override
        public InvocationContext.Builder environmentPrioritySequence(List<String> environments) {
            this.environmentPrioritySequence.clear();
            if (environments != null) {
                this.environmentPrioritySequence.addAll(environments);
            }
            return this;
        }

        @Override
        public InvocationContext.Builder specifiedEnvironment(String environment) {
            this.specifiedEnvironment = environment;
            return this;
        }

        @Override
        public InvocationContext.Builder retry(int maxCount) {
            this.retry = maxCount;
            return this;
        }

        @Override
        public InvocationContext.Builder timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public InvocationContext.Builder timeoutUnit(TimeUnit timeoutUnit) {
            this.timeoutUnit = timeoutUnit;
            return this;
        }

        @Override
        public InvocationContext.Builder protocol(CommunicationProtocol protocol) {
            this.protocol = ObjectUtils.nullIf(protocol, CommunicationProtocol.UNKNOWN);
            return this;
        }

        @Override
        public InvocationContext.Builder format(SerializationFormat format) {
            this.format = ObjectUtils.nullIf(format, SerializationFormat.UNKNOWN);
            return this;
        }

        @Override
        public InvocationContext.Builder isGeneric(boolean isGeneric) {
            this.isGeneric = isGeneric;
            return this;
        }

        @Override
        public InvocationContext.Builder isMulticast(boolean isMulticast) {
            this.isMulticast = isMulticast;
            return this;
        }

        @Override
        public InvocationContext.Builder communicationType(CommunicationType communicationType) {
            this.communicationType = ObjectUtils.nullIf(communicationType, CommunicationType.DEFAULT);
            return this;
        }

        @Override
        public InvocationContext.Builder withDegradation(boolean withDegradation) {
            this.withDegradation = withDegradation;
            return this;
        }

        @Override
        public InvocationContext.Builder accumulator(BinaryOperator<Object> accumulator) {
            this.accumulator = accumulator;
            return this;
        }

        @Override
        public InvocationContext.Builder filterExtensions(Map<String, Object> filterExtensions) {
            if (MapUtils.isEmpty(filterExtensions)) {
                this.filterExtensions = new HashMap<>();
            } else {
                this.filterExtensions = filterExtensions;
            }
            return this;
        }

        @Override
        public InvocationContext build() {
            return new DefaultInvocationContext(this.genericableId,
                    this.isMicro,
                    this.genericableMethod,
                    this.routingFilter,
                    this.loadBalanceFilter,
                    this.loadBalanceWith,
                    this.localWorkerId,
                    this.appName,
                    this.environmentPrioritySequence,
                    this.specifiedEnvironment,
                    this.retry,
                    this.timeout,
                    this.timeoutUnit,
                    this.protocol,
                    this.format,
                    this.isGeneric,
                    this.isMulticast,
                    this.communicationType,
                    this.withDegradation,
                    this.accumulator,
                    this.filterExtensions);
        }
    }
}
