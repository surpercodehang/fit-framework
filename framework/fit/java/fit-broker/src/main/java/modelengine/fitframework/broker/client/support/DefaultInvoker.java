/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.support;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.ConfigurableGenericable;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.GenericableMetadata;
import modelengine.fitframework.broker.GenericableRepository;
import modelengine.fitframework.broker.InvocationContext;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.client.FitableNotFoundException;
import modelengine.fitframework.broker.client.GenericableNotFoundException;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

/**
 * {@link Invoker} 的默认实现。
 *
 * @author 季聿阶
 * @since 2021-06-17
 */
public class DefaultInvoker implements Invoker {
    /** 以下属性为通过构造函数传入的属性。 */

    private final GenericableRepository microGenericableRepository;
    private final GenericableRepository macroGenericableRepository;
    private final InvocationContext.Builder contextBuilder;
    private InvocationContext context;

    /** 以下属性为在当前调用器中设置的属性。 */

    private Filter filter;
    private final List<UniqueFitableId> filterWith = new ArrayList<>();
    private BinaryOperator<Object> accumulator = (first, second) -> first;

    DefaultInvoker(GenericableRepository microGenericableRepository, GenericableRepository macroGenericableRepository,
            String genericableId, InvocationContext.Builder contextBuilder) {
        this.microGenericableRepository =
                notNull(microGenericableRepository, "The micro genericable repository cannot be null.");
        this.macroGenericableRepository =
                notNull(macroGenericableRepository, "The macro genericable repository cannot be null.");
        this.contextBuilder = notNull(contextBuilder,
                "The invocation context builder cannot be null. [genericableId={0}]",
                genericableId).isMulticast(false).accumulator(this.accumulator).withDegradation(true);
    }

    @Override
    public Invoker filter(Filter filter) {
        this.filter = Filter.combine(this.filter, filter);
        this.contextBuilder.loadBalanceFilter(this.filter);
        return this;
    }

    @Override
    public Invoker filterWith(List<UniqueFitableId> ids) {
        this.filterWith.addAll(ids);
        this.contextBuilder.loadBalanceWith(this.filterWith);
        return this;
    }

    @Override
    public Invoker filterWithSpecifiedEnvironment(String environment) {
        this.contextBuilder.specifiedEnvironment(environment);
        return this;
    }

    @Override
    public Invoker unicast() {
        this.contextBuilder.isMulticast(false);
        return this;
    }

    @Override
    public Invoker communicationType(CommunicationType communicationType) {
        this.contextBuilder.communicationType(communicationType);
        return this;
    }

    @Override
    public Invoker multicast(BinaryOperator<Object> accumulator) {
        this.accumulator = accumulator;
        this.contextBuilder.isMulticast(true).accumulator(accumulator);
        return this;
    }

    @Override
    public Invoker retry(int maxCount) {
        if (maxCount >= 0) {
            this.contextBuilder.retry(maxCount);
        }
        return this;
    }

    @Override
    public Invoker timeout(long timeout, TimeUnit timeoutUnit) {
        if (timeout > 0) {
            this.contextBuilder.timeout(timeout).timeoutUnit(timeoutUnit);
        }
        return this;
    }

    @Override
    public Invoker protocol(CommunicationProtocol protocol) {
        this.contextBuilder.protocol(protocol);
        return this;
    }

    @Override
    public Invoker format(SerializationFormat format) {
        this.contextBuilder.format(format);
        return this;
    }

    @Override
    public Invoker ignoreDegradation() {
        this.contextBuilder.withDegradation(false);
        return this;
    }

    @Override
    public Invoker filterExtensions(Map<String, Object> filterExtensions) {
        this.contextBuilder.filterExtensions(filterExtensions);
        return this;
    }

    @Override
    public <R> R invoke(Object... args) {
        try {
            Genericable genericable = this.getGenericable();
            return ObjectUtils.cast(genericable.execute(this.context, args));
        } catch (Throwable e) {
            throw FitException.wrap(e, this.context.genericableId());
        }
    }

    @Override
    public Genericable getGenericable() {
        this.context = this.contextBuilder.build();
        String genericableId = notBlank(this.context.genericableId(),
                () -> new GenericableNotFoundException("The genericable id cannot be blank."));
        Genericable genericable = this.getGenericable(genericableId);
        if (genericable instanceof ConfigurableGenericable) {
            ((ConfigurableGenericable) genericable).method(this.context.genericableMethod());
        }
        return genericable;
    }

    private Genericable getGenericable(String genericableId) {
        GenericableRepository repository =
                this.context.isMicro() ? this.microGenericableRepository : this.macroGenericableRepository;
        return repository.get(genericableId, GenericableMetadata.DEFAULT_VERSION)
                .orElseThrow(() -> FitException.wrap(new FitableNotFoundException(StringUtils.format(
                        "No fitables. [genericableId={0}, isMicro={1}]",
                        genericableId,
                        this.context.isMicro())), genericableId));
    }
}
