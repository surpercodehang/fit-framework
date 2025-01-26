/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * 表示 Http 请求过滤器。
 *
 * @author 季聿阶
 * @since 2022-07-05
 */
public interface HttpServerFilter {
    /**
     * 获取过滤器的名字。
     *
     * @return 表示过滤器名字的 {@link String}。
     */
    String name();

    /**
     * 获取过滤器的优先级。
     * <p><b>优先级越小，越先执行。</b></p>
     *
     * @return 表示过滤器的顺序的 {@code int}。
     */
    int priority();

    /**
     * 获取过滤器的过滤路径样式列表。
     * <p>只有 Http 请求的路径满足了过滤路径样式，才能进行过滤。例如：
     * <ul>
     *     <li>当路径样式为 {@code '/a'} 时，只有完全匹配的请求 {@code '/a'} 才能过滤；</li>
     *     <li>当路径样式为 {@code '/a?'} 时，可以模糊匹配 1 个字符，如 {@code '/aa'} 或 {@code '/ab'} 这样的请求；</li>
     *     <li>当路径样式为 {@code '/a*'} 时，可以在一段路径内模糊匹配任意个字符，如 {@code '/a'} 或 {@code '/abc'}
     *     这样的请求；</li>
     *     <li>当路径样式为 {@code '/a**'} 时，可以在任意段路径内模糊匹配任意个字符，如 {@code '/a'} 或 {@code '/a/b/c'}
     *     这样的请求。</li>
     * </ul>
     * </p>
     *
     * @return 表示过滤器的过滤路径样式列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> matchPatterns();

    /**
     * 获取过滤器的不匹配的过滤路径样式列表。
     *
     * @return 表示过滤器的不匹配的过滤路径样式列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> mismatchPatterns();

    /**
     * 执行过滤器。
     *
     * @param request 表示当前 Http 请求的 {@link HttpClassicServerRequest}。
     * @param response 表示当前 Http 响应的 {@link HttpClassicServerResponse}。
     * @param chain 表示当前过滤器的调用链的 {@link HttpServerFilterChain}。
     * @throws DoHttpServerFilterException 当执行过程中发生异常时。
     */
    void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response, HttpServerFilterChain chain)
            throws DoHttpServerFilterException;

    /**
     * 获取生效范围，默认为 {@code HttpServerFilterScope.PLUGIN}。
     *
     * @return 表示生效范围的 {@link Scope}。
     */
    default Scope scope() {
        return Scope.PLUGIN;
    }

    /**
     * 表示 {@link HttpServerFilter} 的比较器。
     * <p>该比较器优先比较 {@link HttpServerFilter#priority()}，当优先级一致时，比较 {@link HttpServerFilter#name()}。</p>
     */
    class PriorityComparator implements Comparator<HttpServerFilter> {
        /** 表示比较器的单例。 */
        public static final Comparator<HttpServerFilter> INSTANCE = new PriorityComparator();

        private PriorityComparator() {}

        @Override
        public int compare(HttpServerFilter o1, HttpServerFilter o2) {
            if (o1 == null || o2 == null) {
                throw new IllegalStateException("The filter to compare cannot be null.");
            }
            if (o1.priority() != o2.priority()) {
                return Comparator.<Integer>naturalOrder().compare(o1.priority(), o2.priority());
            }
            return Comparator.<String>naturalOrder()
                    .compare(nullIf(o1.name(), StringUtils.EMPTY), nullIf(o2.name(), StringUtils.EMPTY));
        }
    }
}
