/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

import static modelengine.fitframework.util.ObjectUtils.cast;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * 为通配符匹配提供符号序定义。
 *
 * @param <E> 表示符号的实际类型。
 * @author 梁济时
 * @since 2022-07-29
 */
public interface SymbolSequence<E> extends Iterable<E> {
    /**
     * 获取符号序的长度。
     *
     * @return 表示符号序长度的32位整数。
     */
    int length();

    /**
     * 获取指定索引处的符号。
     *
     * @param index 表示从 {@code 0} 开始的索引的32位整数。
     * @return 表示索引处的符号的 {@link Object}。
     * @throws IndexOutOfBoundsException 索引超出限制。
     */
    E at(int index);

    @Override
    default Iterator<E> iterator() {
        return Wildcards.iterator(this);
    }

    /**
     * 返回一个空的符号序。
     *
     * @param <T> 表示符号序中元素的类型。
     * @return 表示空的符号序的 {@link SymbolSequence}。
     */
    static <T> SymbolSequence<T> empty() {
        return cast(Wildcards.EMPTY);
    }

    /**
     * 使用字符序创建符号序。
     *
     * @param chars 表示作为数据源的字符序的 {@link CharSequence}。
     * @return 表示符号序的 {@link SymbolSequence}。
     */
    static SymbolSequence<Character> fromCharSequence(CharSequence chars) {
        return Optional.ofNullable(chars)
                .map(Wildcards::sequence)
                .orElse(SymbolSequence.empty());
    }

    /**
     * 从列表中包含的元素创建符号序。
     *
     * @param list 表示包含符号序信息的列表的 {@link List}。
     * @param <E> 表示列表中元素的类型。
     * @return 表示以列表作为数据源的符号序的 {@link SymbolSequence}。
     */
    static <E> SymbolSequence<E> fromList(List<E> list) {
        return Optional.ofNullable(list)
                .map(Wildcards::sequence)
                .orElse(empty());
    }

    /**
     * 从数组中包含的元素创建符号序。
     *
     * @param array 表示包含符号序信息的数组的 {@link Object}{@code []}。
     * @param <E> 表示数组中元素的类型。
     * @return 表示以数组作为数据源的符号序的 {@link SymbolSequence}。
     */
    static <E> SymbolSequence<E> fromArray(E[] array) {
        return Optional.ofNullable(array)
                .map(Wildcards::sequence)
                .orElse(empty());
    }
}
