/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.GenericableMetadata;
import modelengine.fitframework.broker.client.filter.route.AliasFilter;
import modelengine.fitframework.broker.client.filter.route.DefaultFilter;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;

import java.util.List;
import java.util.Map;

/**
 * 寻找调用的具体服务实现的路由器。
 *
 * @author 季聿阶
 * @since 2021-06-09
 */
public interface Router {
    /**
     * 不设置路由规则的过滤器，直接获取调用器。
     *
     * @return 表示寻找调用的服务地址的负载均衡器的 {@link Invoker}。
     */
    default Invoker route() {
        return this.route(null);
    }

    /**
     * 设置路由规则的过滤器，并获取调用器。
     *
     * @param filter 表示路由规则的过滤器的 {@link Filter}。
     * @return 表示寻找调用的服务地址的负载均衡器的 {@link Invoker}。
     */
    Invoker route(Filter filter);

    /**
     * 路由规则的过滤器。
     * TODO 增加一个FIT的默认路由实现，该过滤器的逻辑为：
     * 1. 查看是否有规则，有规则使用规则
     * 2. 查看是否有默认，有默认使用默认
     * 3. 从注册中心找存活，存活的随机调用
     *
     * @see AliasFilter
     * @see DefaultFilter
     * @see FitableIdFilter
     */
    @FunctionalInterface
    interface Filter {
        /**
         * 进行路由过滤。
         *
         * @param genericable 表示待过滤的泛服务元数据的 {@link GenericableMetadata}。
         * @param toFilterFitables 表示待过滤的泛服务实现元数据列表的 {@link List}{@code <}{@link
         * FitableMetadata}{@code >}。
         * @param args 表示实际调用参数的 {@link Object}{@code []}。
         * @param extensions 表示动态路由所需扩展信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         * @return 表示过滤后的泛服务实现列表的 {@link List}{@code <}{@link FitableMetadata}{@code >}。
         * @throws IllegalArgumentException 当 {@code genericable} 为 {@code null} 时。
         * @throws IllegalArgumentException 当 {@code toFilterFitables} 为 {@code null} 时。
         * @throws IllegalArgumentException 当 {@code toFilterFitables} 中包含 {@code null} 时。
         * @throws IllegalArgumentException 当 {@code toFilterFitables} 中的泛服务元数据和
         * {@code genericable} 不一致时。
         */
        List<? extends FitableMetadata> filter(GenericableMetadata genericable,
                List<? extends FitableMetadata> toFilterFitables, Object[] args, Map<String, Object> extensions);

        /**
         * 将当前路由过滤器与另一个路由过滤器合并。
         *
         * @param another 表示待合并的另一个路由过滤器的 {@link Filter}。
         * @return 表示合并后的路由过滤器的 {@link Filter}。
         */
        default Filter combine(Filter another) {
            return combine(this, another);
        }

        /**
         * 将两个路由过滤器合并。
         *
         * @param first 表示待合并的第一个路由过滤器的 {@link Filter}。
         * @param second 表示待合并的第二个路由过滤器的 {@link Filter}。
         * @return 表示合并后的路由过滤器的 {@link Filter}。
         */
        static Filter combine(Filter first, Filter second) {
            if (first == null) {
                return second;
            } else if (second == null) {
                return first;
            } else {
                return (genericable, toFilterFitables, args, extensions) -> {
                    List<? extends FitableMetadata> filteredFitables =
                            first.filter(genericable, toFilterFitables, args, extensions);
                    return second.filter(genericable, filteredFitables, args, extensions);
                };
            }
        }

        /**
         * 将多于两个的路由过滤器合并。
         *
         * @param filters 表示待合并的路由过滤器列表的 {@link Filter}{@code []}。
         * @return 表示合并后的路由过滤器的 {@link Filter}。
         * @see #combine(Filter, Filter) 合并两个路由过滤器的方法
         */
        static Filter combine(Filter... filters) {
            if (filters == null || filters.length == 0) {
                return null;
            }
            Filter actualFilter = filters[0];
            for (int i = 1; i < filters.length; i++) {
                actualFilter = Router.Filter.combine(actualFilter, filters[i]);
            }
            return actualFilter;
        }

        /**
         * 获取使用泛服务默认实现的路由的过滤器。
         *
         * @return 表示使用泛服务默认实现的路由的过滤器的 {@link Filter}。
         */
        static Filter defaultFilter() {
            return DefaultFilter.INSTANCE;
        }
    }
}
