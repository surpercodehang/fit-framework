/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.aop;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.aop.interceptor.support.AbstractMethodInterceptor;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.filter.route.AliasFilter;
import modelengine.fitframework.broker.client.filter.route.DefaultFilter;
import modelengine.fitframework.broker.util.AnnotationUtils;
import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.pattern.builder.BuilderFactory;
import modelengine.fitframework.util.GenericableUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 微服务动态路由的拦截器。
 *
 * @author 季聿阶
 * @since 2022-05-27
 */
public class DynamicRoutingInterceptor extends AbstractMethodInterceptor {
    private final LazyLoader<BrokerClient> brokerClientLazyLoader;
    private final String alias;
    private final int retry;
    private final int timeout;
    private final TimeUnit timeoutUnit;
    private final CommunicationProtocol protocol;
    private final SerializationFormat format;
    private final Map<Method, GenericableInfo> genericableInfos = new ConcurrentHashMap<>();

    public DynamicRoutingInterceptor(LazyLoader<BrokerClient> brokerClientLazyLoader, String alias, int retry,
            int timeout, TimeUnit timeoutUnit, CommunicationProtocol protocol, SerializationFormat format) {
        this.brokerClientLazyLoader = notNull(brokerClientLazyLoader, "The broker client loader cannot be null.");
        this.alias = alias;
        this.retry = retry;
        this.timeout = timeout;
        this.timeoutUnit = ObjectUtils.nullIf(timeoutUnit, TimeUnit.MILLISECONDS);
        this.protocol = protocol;
        this.format = format;
    }

    @Override
    public Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) {
        Method method = methodJoinPoint.getProxiedInvocation().getMethod();
        GenericableInfo genericableInfo = this.getGenericableId(method);
        Router.Filter filter;
        if (StringUtils.isNotBlank(this.alias)) {
            filter = new AliasFilter(this.alias);
        } else {
            filter = DefaultFilter.INSTANCE;
        }
        return this.brokerClientLazyLoader.get()
                .getRouter(genericableInfo.id(), genericableInfo.isMicro(), method)
                .route(filter)
                .retry(this.retry)
                .timeout(this.timeout, this.timeoutUnit)
                .protocol(this.protocol)
                .format(this.format)
                .invoke(methodJoinPoint.getProxiedInvocation().getArguments());
    }

    private GenericableInfo getGenericableId(Method method) {
        if (this.genericableInfos.containsKey(method)) {
            return this.genericableInfos.get(method);
        }
        GenericableInfo genericableInfo = this.calculateGenericable(method);
        this.genericableInfos.put(method, genericableInfo);
        return genericableInfo;
    }

    private GenericableInfo calculateGenericable(Method method) {
        Optional<String> genericableId = AnnotationUtils.getGenericableId(method);
        if (genericableId.isPresent()) {
            return GenericableInfo.builder().id(genericableId.get()).isMicro(false).build();
        }
        String methodName = method.getName();
        Class<?> methodInterface = ReflectionUtils.getInterface(method)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "Failed to get interface of specified method. [method={0}]",
                        methodName)));
        genericableId = AnnotationUtils.getGenericableId(methodInterface);
        if (genericableId.isPresent()) {
            return GenericableInfo.builder().id(genericableId.get()).isMicro(false).build();
        }
        return GenericableInfo.builder()
                .id(GenericableUtils.getGenericableId(methodInterface, methodName, method.getParameterTypes()))
                .isMicro(true)
                .build();
    }

    /**
     * 用于在动态路由中表示服务的信息。
     */
    public interface GenericableInfo {
        /**
         * 获取服务的唯一标识。
         *
         * @return 表示服务唯一标识的 {@link String}。
         */
        String id();

        /**
         * 判断服务是否为微观服务。
         *
         * @return 如果服务是微观服务，返回 {@code true}，否则，返回 {@code false}。
         */
        boolean isMicro();

        /**
         * {@link GenericableInfo} 的构建器。
         */
        interface Builder {
            /**
             * 向当前构建器中设置服务的唯一标识。
             *
             * @param id 表示待设置的服务的唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder id(String id);

            /**
             * 向当前构建器中设置是否为微观服务的标记。
             *
             * @param isMicro 表示待设置的是否为微观服务的标记的 {@code boolean}。
             * @return 表示当前构建器的 {@code boolean}。
             */
            Builder isMicro(boolean isMicro);

            /**
             * 构建对象。
             *
             * @return 表示构建出来的对象的 {@link GenericableInfo}。
             */
            GenericableInfo build();
        }

        /**
         * 获取 {@link GenericableInfo} 的构建器。
         *
         * @return 表示 {@link GenericableInfo} 的构建器的 {@link Builder}。
         */
        static Builder builder() {
            return builder(null);
        }

        /**
         * 获取 {@link GenericableInfo} 的构建器，同时将指定对象的值进行填充。
         *
         * @param value 表示指定对象的 {@link GenericableInfo}。
         * @return 表示 {@link GenericableInfo} 的构建器的 {@link Builder}。
         */
        static Builder builder(GenericableInfo value) {
            return BuilderFactory.get(GenericableInfo.class, Builder.class).create(value);
        }
    }
}
