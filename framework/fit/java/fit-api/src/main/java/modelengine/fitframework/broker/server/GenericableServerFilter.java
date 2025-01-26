/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.server;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * 表示 {@link modelengine.fitframework.annotation.Genericable} 的服务端过滤器。
 *
 * @author 季聿阶
 * @since 2024-08-21
 */
public interface GenericableServerFilter {
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
     * 获取过滤器所属的插件。
     *
     * @return 表示插件的 {@link Plugin}。
     */
    Plugin plugin();

    /**
     * 获取过滤器的过滤服务唯一标识样式列表。
     * <p>只有服务唯一标识满足了过滤路径样式，才能进行过滤。例如：
     * <ul>
     * <li>当服务唯一标识样式为 {@code 'a.bb.ccc'} 时，只有完全匹配的请求 {@code 'a.bb.ccc'} 才能过滤；</li>
     * <li>当服务唯一标识样式为 {@code 'a.bb.ccc?'} 时，可以模糊匹配 1 个字符，如 {@code 'a.bb.cccx'} 或 {@code 'a.bb.cccy'}
     * 这样的请求；</li>
     * <li>当服务唯一标识样式为 {@code 'a.bb.ccc*'} 时，可以在一段路径内模糊匹配任意个字符，如 {@code 'a.bb.ccc'}
     * 或 {@code 'a.bb.cccx'}
     * 这样的请求；</li>
     *  <li>当服务唯一标识样式为 {@code 'a.bb.ccc**'} 时，可以在任意段路径内模糊匹配任意个字符，如 {@code 'a.bb.ccc'}
     *  或 {@code 'a.bb.ccc.xx.yy}
     *  这样的请求。</li>
     * </ul>
     * </p>
     *
     * @return 表示过滤器的过滤服务唯一标识样式列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> matchPatterns();

    /**
     * 获取过滤器的不匹配的过滤服务唯一标识样式列表。
     *
     * @return 表示过滤器的不匹配的过滤服务唯一标识样式列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> mismatchPatterns();

    /**
     * 执行过滤器。
     *
     * @param genericableId 表示服务的唯一标识。
     * @param args 表示当前调用的参数列表的 {@link Object}{@code []}。
     * @param chain 表示当前过滤器的调用链的 {@link GenericableServerFilter}。
     * @throws DoGenericableServerFilterException 当执行过程中发生异常时。
     */
    void doFilter(String genericableId, Object[] args, GenericableServerFilterChain chain)
            throws DoGenericableServerFilterException;

    /**
     * 获取生效范围，默认为 {@code GenericableServerFilterScope.PLUGIN}。
     *
     * @return 表示生效范围的 {@link Scope}。
     */
    default Scope scope() {
        return Scope.PLUGIN;
    }

    /**
     * 表示 {@link GenericableServerFilter} 的比较器。
     * <p>该比较器优先比较 {@link GenericableServerFilter#priority()}，当优先级一致时，
     * 比较 {@link GenericableServerFilter#name()}。</p>
     */
    class PriorityComparator implements Comparator<GenericableServerFilter> {
        /** 表示比较器的单例。 */
        public static final Comparator<GenericableServerFilter> INSTANCE = new PriorityComparator();

        private PriorityComparator() {}

        @Override
        public int compare(GenericableServerFilter o1, GenericableServerFilter o2) {
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