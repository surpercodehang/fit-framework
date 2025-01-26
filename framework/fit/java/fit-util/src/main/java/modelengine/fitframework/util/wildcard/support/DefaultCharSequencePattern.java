/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard.support;

import modelengine.fitframework.util.wildcard.CharSequencePattern;
import modelengine.fitframework.util.wildcard.SymbolSequence;

import java.util.Objects;

/**
 * 为 {@link CharSequencePattern} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-07-29
 */
public class DefaultCharSequencePattern extends DefaultPattern<Character> implements CharSequencePattern {
    /**
     * 使用模式的长度初始化 {@link DefaultPattern} 类的新实例。
     *
     * @param pattern 表示匹配模式的符号序的 {@link CharSequence}。
     * @param singleWildcard 表示单字符通配符的字符的 {@code char}。
     * @param multipleWildcard 表示多字符通配符的字符的 {@code char}。
     */
    public DefaultCharSequencePattern(CharSequence pattern, char singleWildcard, char multipleWildcard) {
        super(SymbolSequence.fromCharSequence(pattern),
                new DefaultSymbolClassifier<>(singleWildcard, multipleWildcard),
                Objects::equals);
    }
}
