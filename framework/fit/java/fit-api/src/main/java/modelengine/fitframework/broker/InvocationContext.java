/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker;

import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.support.DefaultInvocationContext;
import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

/**
 * 表示调用上下文。
 *
 * @author 季聿阶
 * @since 2022-06-29
 */
public interface InvocationContext {
    /**
     * 获取调用的泛服务的唯一标识。
     *
     * @return 表示调用的泛服务的唯一标识的 {@link String}。
     */
    String genericableId();

    /**
     * 判断调用的泛服务是否为微观服务。
     *
     * @return 如果是微观服务，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isMicro();

    /**
     * 获取调用的泛服务的方法。
     *
     * @return 表示调用的泛服务的方法的 {@link Method}。
     */
    Method genericableMethod();

    /**
     * 获取路由过滤器。
     *
     * @return 表示路由过滤器的 {@link Router.Filter}。
     */
    Router.Filter routingFilter();

    /**
     * 获取负载均衡过滤器。
     *
     * @return 表示负载均衡过滤器的 {@link Invoker.Filter}。
     */
    Invoker.Filter loadBalanceFilter();

    /**
     * 获取负载均衡的条件：负载均衡需要和指定服务的地址取交集。
     *
     * @return 表示指定服务列表的 {@link List}{@code <}{@link UniqueFitableId}{@code >}。
     */
    List<UniqueFitableId> loadBalanceWith();

    /**
     * 获取发起调用端的进程唯一标识，即本地进程唯一标识。
     *
     * @return 表示发起调用端的进程唯一标识的 {@link String}。
     */
    String localWorkerId();

    /**
     * 获取当前进程的应用名。
     *
     * @return 表示当前进程的应用名的 {@link String}。
     */
    String appName();

    /**
     * 获取环境调用链列表。
     *
     * @return 表示环境调用链列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> environmentPrioritySequence();

    /**
     * 获取调用目标端的环境标。
     *
     * @return 表示调用目标端的环境标的 {@link String}。
     */
    String specifiedEnvironment();

    /**
     * 获取可以重试的最大次数。
     *
     * @return 表示可以重试的最大次数的 {@code int}。
     */
    int retry();

    /**
     * 获取服务调用超时时间。
     *
     * @return 表示服务调用超时时间的 {@code long}。
     */
    long timeout();

    /**
     * 获取服务调用超时时间的单位。
     *
     * @return 表示服务调用超时时间单位的 {@link TimeUnit}。
     */
    TimeUnit timeoutUnit();

    /**
     * 获取通讯协议。
     * <p>如果是 {@link CommunicationProtocol#UNKNOWN}，表示不指定通讯协议。</p>
     *
     * @return 表示通讯协议的 {@link CommunicationProtocol}。
     */
    CommunicationProtocol protocol();

    /**
     * 获取序列化方式。
     * <p>如果是 {@link SerializationFormat#UNKNOWN}，表示不指定序列化方式。</p>
     *
     * @return 表示序列化方式的 {@link SerializationFormat}。
     */
    SerializationFormat format();

    /**
     * 判断当前调用是否为泛化调用。
     *
     * @return 如果当前调用是泛化调用，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isGeneric();

    /**
     * 判断当前调用是否为多播调用。
     *
     * @return 如果当前调用是多播调用，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean isMulticast();

    /**
     * 返回当前调用的通信方式。
     *
     * @return 表示当前调用的通信方式的 {@link CommunicationType}。
     */
    CommunicationType communicationType();

    /**
     * 判断当前调用是否携带降级。
     *
     * @return 如果当前调用携带降级，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean withDegradation();

    /**
     * 获取多播调用的结果累加器。
     *
     * @return 表示多播调用的结果累加器的 {@link BinaryOperator}{@code <}{@link Object}{@code >}。
     */
    BinaryOperator<Object> accumulator();

    /**
     * 获取动态路由或负载均衡所需的扩展信息。
     *
     * @return 表示扩展信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    Map<String, Object> filterExtensions();

    /**
     * {@link InvocationContext} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置调用的泛服务的唯一标识。
         *
         * @param genericableId 表示待设置的调用的泛服务的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder genericableId(String genericableId);

        /**
         * 向当前构建器中设置调用的泛服务是否为微观服务的标志。
         *
         * @param isMicro 表示待设置的调用的泛服务是否为微观服务的标志的 {@code boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isMicro(boolean isMicro);

        /**
         * 向当前构建器中设置调用的泛服务的方法。
         *
         * @param genericableMethod 表示待设置的调用的泛服务的方法的 {@link Method}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder genericableMethod(Method genericableMethod);

        /**
         * 向当前构建器中设置路由过滤器。
         *
         * @param filter 表示待设置的路由过滤器的 {@link Router.Filter}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder routingFilter(Router.Filter filter);

        /**
         * 向当前构建器中设置负载均衡过滤器。
         *
         * @param filter 表示负载均衡过滤器的 {@link Invoker.Filter}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder loadBalanceFilter(Invoker.Filter filter);

        /**
         * 向当前构建器中设置指定服务列表，来执行特殊负载均衡策略。
         * <p>该负载均衡策略为和指定服务的地址取交集。</p>
         *
         * @param fitableKeys 表示指定服务列表的 {@link List}{@code <}{@link UniqueFitableId}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder loadBalanceWith(List<UniqueFitableId> fitableKeys);

        /**
         * 向当前构建器中设置本地进程唯一标识。
         *
         * @param workerId 表示发起调用端的进程唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder localWorkerId(String workerId);

        /**
         * 向当前构建器中设置应用名。
         *
         * @param appName 表示当前进程的应用名的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder appName(String appName);

        /**
         * 向当前构建器中设置环境调用链列表。
         *
         * @param environments 表示环境调用链列表的 {@link List}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder environmentPrioritySequence(List<String> environments);

        /**
         * 向当前构建器中设置调用目标端的环境标。
         *
         * @param environment 表示调用目标端的环境标的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder specifiedEnvironment(String environment);

        /**
         * 向当前构建器中设置可以重试的最大次数。
         *
         * @param maxCount 表示可以重试的最大次数的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder retry(int maxCount);

        /**
         * 向当前构建器中设置服务调用的超时时间。
         *
         * @param timeout 表示服务调用超时时间的 {@code long}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder timeout(long timeout);

        /**
         * 向当前构建器中设置服务调用超时时间的单位。
         *
         * @param timeoutUnit 表示服务调用超时时间单位的 {@link TimeUnit}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder timeoutUnit(TimeUnit timeoutUnit);

        /**
         * 向当前构建器中设置通讯协议。
         *
         * @param protocol 表示通讯协议的 {@link CommunicationProtocol}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder protocol(CommunicationProtocol protocol);

        /**
         * 向当前构建器中设置序列化方式。
         *
         * @param format 表示序列化方式的 {@link SerializationFormat}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder format(SerializationFormat format);

        /**
         * 向当前构建器中设置当前调用是否为泛化调用。
         *
         * @param isGeneric 如果当前调用是泛化调用，则为 {@code true}，否则为 {@code false}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isGeneric(boolean isGeneric);

        /**
         * 向当前构建器中设置当前调用是否为多播调用。
         *
         * @param isMulticast 如果当前调用是多播调用，则为 {@code true}，否则为 {@code false}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isMulticast(boolean isMulticast);

        /**
         * 向当前构建器中设置调用的通信方式。
         *
         * @param communicationType 表示当前调用的通信方式的 {@link CommunicationType}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder communicationType(CommunicationType communicationType);

        /**
         * 向当前构建器中设置当前调用是否携带降级。
         *
         * @param withDegradation 如果当前调用携带降级，则为 {@code true}，否则为 {@code false}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder withDegradation(boolean withDegradation);

        /**
         * 向当前构建器中设置多播调用的结果累加器。
         *
         * @param accumulator 表示多播调用的结果累加器的 {@link BinaryOperator}{@code <}{@link Object}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder accumulator(BinaryOperator<Object> accumulator);

        /**
         * 向当前构建器中设置动态路由或负载均衡所需的扩展信息。
         *
         * @param filterExtensions 表示扩展信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder filterExtensions(Map<String, Object> filterExtensions);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link InvocationContext}。
         */
        InvocationContext build();
    }

    /**
     * 获取 {@link InvocationContext} 的构建器。
     *
     * @return 表示 {@link InvocationContext} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return custom(null);
    }

    /**
     * 获取 {@link InvocationContext} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link InvocationContext}。
     * @return 表示 {@link InvocationContext} 的构建器的 {@link Builder}。
     */
    static Builder custom(InvocationContext value) {
        return new DefaultInvocationContext.Builder(value);
    }
}
