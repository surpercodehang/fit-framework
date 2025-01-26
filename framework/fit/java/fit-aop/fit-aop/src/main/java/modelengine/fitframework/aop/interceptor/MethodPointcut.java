/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 表示定义的方法切点集合。
 *
 * @author 季聿阶
 * @since 2022-05-05
 */
public interface MethodPointcut {
    /**
     * 获取定义的方法切点集合的 {@link Set}{@code <}{@link Method}{@code >}。
     *
     * @return 表示定义的方法切点集合的 {@link Set}{@code <}{@link Method}{@code >}。
     */
    Set<Method> methods();

    /**
     * 获取方法匹配器集合的 {@link MethodMatcherCollection}。
     *
     * @return 表示方法匹配器集合的 {@link MethodMatcherCollection}。
     */
    MethodMatcherCollection matchers();

    /**
     * 向方法切点集合中添加一个类的所有方法。
     * <p>只有添加的方法满足所有方法匹配器的匹配之后，才能真正被添加到当前的方法切点集合中。</p>
     *
     * @param clazz 表示待添加的类的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 如果添加成功，返回 {@code true}，否则，返回 {@code false}。
     */
    boolean add(Class<?> clazz);
}
