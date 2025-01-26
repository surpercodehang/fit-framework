/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

/**
 * 为 {@link Pattern} 提供基于字节序的模式。
 *
 * @author 梁济时
 * @since 2022-07-29
 */
public interface CharSequencePattern extends Pattern<Character> {
    /**
     * 测试是否可以匹配指定的字符序。
     *
     * @param value 表示待匹配的字符序的 {@link CharSequence}。
     * @return 若可以成功匹配，则为 {@code true}；否则为 {@code false}。
     */
    default boolean matches(CharSequence value) {
        return this.matches(SymbolSequence.fromCharSequence(value));
    }
}
