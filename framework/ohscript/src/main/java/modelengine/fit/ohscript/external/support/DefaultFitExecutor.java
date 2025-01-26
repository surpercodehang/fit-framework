/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external.support;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.ohscript.external.FitExecutionException;
import modelengine.fit.ohscript.external.FitExecutor;
import modelengine.fit.ohscript.util.EmptyValue;
import modelengine.fit.ohscript.util.ValueUtils;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.filter.route.AliasFilter;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 表示 {@link FitExecutor} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-12-18
 */
public class DefaultFitExecutor implements FitExecutor {
    private static final Logger log = Logger.get(DefaultFitExecutor.class);

    private static final String ALIAS = "alias";

    private static final String ID = "id";

    private static final String INVOKER_FILTER_BEAN_NAME = "invokerFilterBeanName";

    private static final String ROUTER_FILTER_BEAN_NAME = "routerFilterBeanName";

    private static final String TIMEOUT = "timeout";

    private static final String ASYNC = "async";

    private static final String FORMAT = "format";

    private static final String FILTER_EXTENSIONS = "filterExtensions";

    private final BeanContainer beanContainer;

    private final BrokerClient brokerClient;

    public DefaultFitExecutor(BeanContainer container, BrokerClient brokerClient) {
        this.beanContainer = notNull(container, "The bean container cannot be null.");
        this.brokerClient = notNull(brokerClient, "The fit broker client cannot be null.");
    }

    @Override
    public Object execute(String genericableId, Object[] args) {
        if (args == null || args.length < 1) {
            throw new FitExecutionException(genericableId, "Failed to execute by FIT. [cause=No arguments.]");
        }
        try {
            Map<String, Object> context = this.getContext(args[0]);
            Object[] actualArgs = new Object[args.length - 1];
            System.arraycopy(args, 1, actualArgs, 0, actualArgs.length);
            Object[] actual = Stream.of(actualArgs)
                    .map(ValueUtils::fromOhScript)
                    .filter(arg -> arg != EmptyValue.IGNORE)
                    .toArray();
            log.debug("Prepare to invoke genericable by OhScript. [genericableId={}]", genericableId);
            log.debug("Context: {}.", context);
            for (int i = 0; i < actual.length; i++) {
                log.debug("Arg[{}]: {}.", i, actual[i]);
            }
            Object result = this.brokerClient.getRouter(genericableId)
                    .route(this.getRouterFilter(context))
                    .timeout(this.getTimeoutInMillis(context), TimeUnit.MILLISECONDS)
                    .communicationType(this.getCommunicationType(context))
                    .format(this.getSerializationFormat(context))
                    .filterExtensions(this.getFilterExtensions(context))
                    .filter(this.getInvokerFilter(context))
                    .invoke(actual);
            log.debug("Invoke genericable by OhScript successfully. [result={}]", result);
            return result;
        } catch (FitException e) {
            log.error("Failed to execute by FIT. [genericableId={}]", genericableId, e);
            throw new FitExecutionException(e.associatedGenericableId(), e.associatedFitableId(),
                    StringUtils.format("Failed to execute by FIT. [genericableId={0}]", genericableId), e);
        } catch (Throwable e) {
            log.error("Failed to execute by FIT. [genericableId={}]", genericableId, e);
            throw new FitExecutionException(genericableId,
                    StringUtils.format("Failed to execute by FIT. [genericableId={0}]", genericableId), e);
        }
    }

    private Map<String, Object> getContext(Object obj) {
        Object newObj = ValueUtils.fromOhScript(obj);
        if (newObj instanceof Map) {
            return cast(newObj);
        }
        return new HashMap<>();
    }

    private Router.Filter getRouterFilter(Map<String, Object> context) {
        Router.Filter filter = null;
        if (context.containsKey(ROUTER_FILTER_BEAN_NAME)) {
            String beanName = String.valueOf(context.get(ROUTER_FILTER_BEAN_NAME));
            filter = this.beanContainer.all(Router.Filter.class)
                    .stream()
                    .filter(beanFactory -> Objects.equals(beanFactory.metadata().name(), beanName))
                    .map(BeanFactory::<Router.Filter>get)
                    .findFirst()
                    .orElse(null);
        }
        if (context.containsKey(ALIAS)) {
            Object alias = context.get(ALIAS);
            if (alias instanceof String) {
                filter = Router.Filter.combine(filter, new AliasFilter((String) alias));
            }
        }
        if (context.containsKey(ID)) {
            Object id = context.get(ID);
            if (id instanceof String) {
                filter = Router.Filter.combine(filter, new FitableIdFilter((String) id));
            }
        }
        return filter;
    }

    private Invoker.Filter getInvokerFilter(Map<String, Object> context) {
        if (context.containsKey(INVOKER_FILTER_BEAN_NAME)) {
            String beanName = String.valueOf(context.get(INVOKER_FILTER_BEAN_NAME));
            return this.beanContainer.all(Invoker.Filter.class)
                    .stream()
                    .filter(beanFactory -> Objects.equals(beanFactory.metadata().name(), beanName))
                    .map(BeanFactory::<Invoker.Filter>get)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private long getTimeoutInMillis(Map<String, Object> context) {
        long timeout = 3000L;
        if (context.containsKey(TIMEOUT)) {
            Object timeoutValue = context.get(TIMEOUT);
            if (timeoutValue instanceof Number) {
                timeout = ((Number) timeoutValue).longValue();
            }
        }
        return timeout;
    }

    private CommunicationType getCommunicationType(Map<String, Object> context) {
        boolean isAsync = this.isAsync(context);
        return isAsync ? CommunicationType.ASYNC : CommunicationType.DEFAULT;
    }

    private boolean isAsync(Map<String, Object> context) {
        if (context.containsKey(ASYNC)) {
            Object asyncValue = context.get(ASYNC);
            return Boolean.parseBoolean(String.valueOf(asyncValue));
        }
        return false;
    }

    private SerializationFormat getSerializationFormat(Map<String, Object> context) {
        if (context.containsKey(FORMAT)) {
            Object formatValue = context.get(FORMAT);
            return SerializationFormat.from(String.valueOf(formatValue));
        }
        return SerializationFormat.UNKNOWN;
    }

    private Map<String, Object> getFilterExtensions(Map<String, Object> context) {
        if (context.containsKey(FILTER_EXTENSIONS)) {
            Object extensions = context.get(FILTER_EXTENSIONS);
            if (extensions instanceof Map) {
                return cast(extensions);
            }
        }
        return new HashMap<>();
    }
}
