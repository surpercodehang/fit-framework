/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.model;

import modelengine.fitframework.model.support.DefaultTuple;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * 表示不可变的元组。
 *
 * @author 季聿阶
 * @since 2022-08-14
 */
public interface Tuple {
    /**
     * 获取元组的容量。
     *
     * @return 表示元组容量的 {@code int}。
     */
    int capacity();

    /**
     * 获取元组中指定序号的元素。
     *
     * @param index 表示指定序号的 {@code int}。
     * @param <T> 表示获取的元素类型的 {@link T}。
     * @return 表示指定序号的元素的 {@link Optional}{@code <}{@link T}{@code >}。
     * @throws IndexOutOfBoundsException 当 {@code index} 小于 0 或大于等于 {@link #capacity()} 时。
     */
    <T> Optional<T> get(int index);

    /**
     * 创建一个包含元素 {@code t1} 的一元组。
     *
     * @param t1 表示一元组中的唯一元素的 {@link T1}。
     * @param <T1> 表示一元组中的唯一元素类型的 {@link T1}。
     * @return 表示创建的一元组的 {@link Tuple}。
     */
    static <T1> Tuple solo(T1 t1) {
        return new DefaultTuple(Collections.singletonList(t1));
    }

    /**
     * 创建一个包含元素 {@code t1}、{@code t2} 的二元组。
     *
     * @param t1 表示二元组中的第一个元素的 {@link T1}。
     * @param t2 表示二元组中的第二个元素的 {@link T2}。
     * @param <T1> 表示二元组中的第一个元素类型的 {@link T1}。
     * @param <T2> 表示二元组中的第二个元素类型的 {@link T2}。
     * @return 表示创建的二元组的 {@link Tuple}。
     */
    static <T1, T2> Tuple duet(T1 t1, T2 t2) {
        return new DefaultTuple(Arrays.asList(t1, t2));
    }

    /**
     * 创建一个包含元素 {@code t1}、{@code t2}、{@code t3} 的三元组。
     *
     * @param t1 表示三元组中的第一个元素的 {@link T1}。
     * @param t2 表示三元组中的第二个元素的 {@link T2}。
     * @param t3 表示三元组中的第三个元素的 {@link T3}。
     * @param <T1> 表示三元组中的第一个元素类型的 {@link T1}。
     * @param <T2> 表示三元组中的第二个元素类型的 {@link T2}。
     * @param <T3> 表示三元组中的第三个元素类型的 {@link T3}。
     * @return 表示创建的三元组的 {@link Tuple}。
     */
    static <T1, T2, T3> Tuple trio(T1 t1, T2 t2, T3 t3) {
        return new DefaultTuple(Arrays.asList(t1, t2, t3));
    }

    /**
     * 创建一个包含元素 {@code t1}、{@code t2}、{@code t3}、{@code t4} 的四元组。
     *
     * @param t1 表示四元组中的第一个元素的 {@link T1}。
     * @param t2 表示四元组中的第二个元素的 {@link T2}。
     * @param t3 表示四元组中的第三个元素的 {@link T3}。
     * @param t4 表示四元组中的第四个元素的 {@link T4}。
     * @param <T1> 表示四元组中的第一个元素类型的 {@link T1}。
     * @param <T2> 表示四元组中的第二个元素类型的 {@link T2}。
     * @param <T3> 表示四元组中的第三个元素类型的 {@link T3}。
     * @param <T4> 表示四元组中的第四个元素类型的 {@link T4}。
     * @return 表示创建的四元组的 {@link Tuple}。
     */
    static <T1, T2, T3, T4> Tuple quartet(T1 t1, T2 t2, T3 t3, T4 t4) {
        return new DefaultTuple(Arrays.asList(t1, t2, t3, t4));
    }

    /**
     * 创建一个包含元素 {@code t1}、{@code t2}、{@code t3}、{@code t4}、{@code t5} 的五元组。
     *
     * @param t1 表示五元组中的第一个元素的 {@link T1}。
     * @param t2 表示五元组中的第二个元素的 {@link T2}。
     * @param t3 表示五元组中的第三个元素的 {@link T3}。
     * @param t4 表示五元组中的第四个元素的 {@link T4}。
     * @param t5 表示五元组中的第五个元素的 {@link T5}。
     * @param <T1> 表示五元组中的第一个元素类型的 {@link T1}。
     * @param <T2> 表示五元组中的第二个元素类型的 {@link T2}。
     * @param <T3> 表示五元组中的第三个元素类型的 {@link T3}。
     * @param <T4> 表示五元组中的第四个元素类型的 {@link T4}。
     * @param <T5> 表示五元组中的第五个元素类型的 {@link T5}。
     * @return 表示创建的五元组的 {@link Tuple}。
     */
    static <T1, T2, T3, T4, T5> Tuple quintet(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return new DefaultTuple(Arrays.asList(t1, t2, t3, t4, t5));
    }
}
