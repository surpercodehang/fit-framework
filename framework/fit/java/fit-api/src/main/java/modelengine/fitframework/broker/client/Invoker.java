/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client;

import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.broker.UniqueFitableId;
import modelengine.fitframework.broker.client.filter.loadbalance.EmptyFilter;
import modelengine.fitframework.broker.client.filter.loadbalance.RoundRobinFilter;
import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

/**
 * 服务调用器。
 *
 * @author 季聿阶
 * @since 2021-06-11
 */
public interface Invoker {
    /**
     * 设置服务调用地址的自定义过滤器。
     *
     * @param filter 表示服务调用地址的自定义过滤器的 {@link Filter}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker filter(Filter filter);

    /**
     * 设置服务调用地址过滤的条件：需要在指定服务实现列表的调用地址范围内。
     * <p>指定服务实现列表未必和当前调用的服务是同一个服务。</p>
     *
     * @param ids 表示指定服务实现列表的 {@link List}{@code <}{@link UniqueFitableId}{@code >}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker filterWith(List<UniqueFitableId> ids);

    /**
     * 设置服务调用的通信类型。
     * <p>如果设置了 {@link CommunicationType#ASYNC}，但服务器不支持异步调用，则仍以同步方式执行调用。</p>
     * TODO: 向应用提供异步API
     *
     * @param communicationType 表示需要设置的通信类型的 {@link CommunicationType}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker communicationType(CommunicationType communicationType);

    /**
     * 设置服务调用地址过滤的条件：调用目标地址的环境标必须是指定的环境标。
     *
     * @param environment 表示指定环境标的 {@link String}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker filterWithSpecifiedEnvironment(String environment);

    /**
     * 使用单播调用，即只调用一次。
     * <p>这个是默认的调用形式。
     *
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker unicast();

    /**
     * 使用多播调用，即会调用多次。
     *
     * @param accumulator 表示多个结果的聚合器的 {@link BinaryOperator}{@code <}{@link Object}{@code >}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker multicast(BinaryOperator<Object> accumulator);

    /**
     * 设置最多可重试的次数。
     *
     * @param maxCount 表示最多可以重试的次数的 {@code int}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker retry(int maxCount);

    /**
     * 设置超时时间。
     *
     * @param timeout 表示服务调用超时时间的 {@code long}。
     * @param timeoutUnit 表示服务调用超时时间的单位的 {@link TimeUnit}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker timeout(long timeout, TimeUnit timeoutUnit);

    /**
     * 设置通讯协议。
     * <p>当 {@code protocol} 为 {@link CommunicationProtocol#UNKNOWN} 时，表示不指定通讯协议。</p>
     *
     * @param protocol 表示通讯协议的 {@link CommunicationProtocol}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker protocol(CommunicationProtocol protocol);

    /**
     * 设置序列化方式。
     * <p>当 {@code format} 为 {@link SerializationFormat#UNKNOWN} 时，表示不指定序列化方式。</p>
     *
     * @param format 表示序列化方式的 {@link SerializationFormat}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker format(SerializationFormat format);

    /**
     * 设置不进行降级调用。
     * <p>默认行为是进行降级调用。
     *
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker ignoreDegradation();

    /**
     * 设置动态路由或负载均衡所需的扩展信息。
     *
     * @param filterExtensions 表示扩展信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 表示当前的服务调用器的 {@link Invoker}。
     */
    Invoker filterExtensions(Map<String, Object> filterExtensions);

    /**
     * 进行服务调用。
     *
     * @param args 表示服务调用的参数列表的 {@link Object}{@code []}。
     * @param <R> 表示返回值类型的 {@link R}。
     * @return 表示调用结果的 {@link R}。
     * @throws GenericableNotFoundException 当待调用的服务唯一标识为 {@code null} 或空白字符串时。
     * @throws FitableNotFoundException 当从服务仓库中找不到合适的服务实现时。
     */
    <R> R invoke(Object... args);

    /**
     * 获取当前调用器对应的服务。
     * <p><b>注意：该方法获取的服务是未经过动态路由和负载均衡的，如果需要调用获取结果，请使用 {@link #invoke(Object...)}。</b></p>
     *
     * @return 表示当前调用器对应的服务的 {@link Genericable}。
     * @throws GenericableNotFoundException 当待调用的服务唯一标识为 {@code null} 或空白字符串时，或当从服务仓库中找不到合适的服务时。
     */
    Genericable getGenericable();

    /**
     * 调用地址的过滤器。
     */
    @FunctionalInterface
    interface Filter {
        /**
         * 进行调用地址过滤。
         *
         * @param fitable 表示待过滤服务地址所属服务实现的元数据的 {@link FitableMetadata}。
         * @param localWorkerId 表示本地进程的唯一标识的 {@link String}。
         * @param toFilterTargets 表示待过滤服务地址列表的 {@link List}{@code <}{@link Target}{@code >}。
         * @param extensions 表示负载均衡所需扩展信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         * @return 表示过滤后的服务地址列表的 {@link List}{@code <}{@link Target}{@code >}。
         * @throws IllegalArgumentException 当 {@code fitable} 为 {@code null} 时。
         * @throws IllegalArgumentException 当 {@code workerId} 为 {@code null} 或空白字符串时。
         * @throws IllegalArgumentException 当 {@code toFilterTargets} 为 {@code null} 时。
         * @throws IllegalArgumentException 当 {@code toFilterTargets} 中包含 {@code null} 时。
         */
        List<Target> filter(FitableMetadata fitable, String localWorkerId, List<Target> toFilterTargets,
                Map<String, Object> extensions);

        /**
         * 将当前负载均衡过滤器与另一个负载均衡过滤器合并。
         *
         * @param another 表示待合并的另一个负载均衡过滤器的 {@link Filter}。
         * @return 表示合并后的负载均衡过滤器的 {@link Filter}。
         */
        default Filter combine(Filter another) {
            return combine(this, another);
        }

        /**
         * 将两个负载均衡过滤器合并。
         *
         * @param first 表示待合并的第一个负载均衡过滤器的 {@link Filter}。
         * @param second 表示待合并的第二个负载均衡过滤器的 {@link Filter}。
         * @return 表示合并后的负载均衡过滤器的 {@link Filter}。
         * @see #combine(Filter...) 合并多于两个负载均衡过滤器的方法
         */
        static Filter combine(Filter first, Filter second) {
            if (first == null) {
                return second;
            } else if (second == null) {
                return first;
            } else {
                return (metadata, localWorkerId, toFilterTargets, extensions) -> {
                    List<Target> filteredTargets = first.filter(metadata, localWorkerId, toFilterTargets, extensions);
                    return second.filter(metadata, localWorkerId, filteredTargets, extensions);
                };
            }
        }

        /**
         * 将多于两个的负载均衡过滤器合并。
         *
         * @param filters 表示待合并的负载均衡过滤器列表的 {@link Filter}{@code []}。
         * @return 表示合并后的负载均衡过滤器的 {@link Filter}。
         * @see #combine(Filter, Filter) 合并两个负载均衡过滤器的方法
         */
        static Filter combine(Filter... filters) {
            if (filters == null || filters.length == 0) {
                return null;
            }
            Filter actualFilter = filters[0];
            for (int i = 1; i < filters.length; i++) {
                actualFilter = Invoker.Filter.combine(actualFilter, filters[i]);
            }
            return actualFilter;
        }

        /**
         * 获取一个空的负载均衡过滤器。
         *
         * @return 表示空的负载均衡过滤器的实例的 {@link Filter}。
         */
        static Filter empty() {
            return EmptyFilter.INSTANCE;
        }

        /**
         * 获取一个轮询的负载均衡过滤器。
         *
         * @return 表示轮询的负载均衡过滤器的实例的 {@link Filter}。
         */
        static Filter roundRobin() {
            return RoundRobinFilter.INSTANCE;
        }
    }
}
