/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard.support;

import modelengine.fitframework.util.wildcard.Pattern;
import modelengine.fitframework.util.wildcard.Result;
import modelengine.fitframework.util.wildcard.SymbolClassifier;
import modelengine.fitframework.util.wildcard.SymbolMatcher;
import modelengine.fitframework.util.wildcard.SymbolSequence;
import modelengine.fitframework.util.wildcard.SymbolType;

import java.util.Iterator;

/**
 * 为 {@link Pattern} 提供基类。
 *
 * @param <T> 表示模式中元素的类型。
 * @author 梁济时
 * @since 2022-07-28
 */
public class DefaultPattern<T> extends AbstractMatcher<T> implements Pattern<T> {
    private final SymbolSequence<T> pattern;
    private final boolean[] results;
    private final SymbolClassifier<T> symbolClassifier;
    private final SymbolMatcher<T> symbolMatcher;
    private final SymbolConfiguration symbols;

    /**
     * 使用模式的长度初始化 {@link DefaultPattern} 类的新实例。
     *
     * @param pattern 表示匹配模式的符号序的 {@link SymbolSequence}。
     * @param symbolClassifier 表示符号的分类程序的 {@link SymbolClassifier}。
     * @param symbolMatcher 表示符号的匹配程序的 {@link SymbolMatcher}。
     */
    public DefaultPattern(SymbolSequence<T> pattern, SymbolClassifier<T> symbolClassifier,
            SymbolMatcher<T> symbolMatcher) {
        this.pattern = pattern;
        this.results = new boolean[this.pattern.length() + 1];
        this.symbolClassifier = symbolClassifier;
        this.symbolMatcher = symbolMatcher;
        this.symbols = this.new SymbolConfiguration();
    }

    private class SymbolConfiguration implements Pattern.SymbolConfiguration<T> {
        @Override
        public SymbolMatcher<T> matcher() {
            return DefaultPattern.this.symbolMatcher;
        }

        @Override
        public SymbolClassifier<T> classifier() {
            return DefaultPattern.this.symbolClassifier;
        }
    }

    @Override
    public Pattern.SymbolConfiguration<T> symbols() {
        return this.symbols;
    }

    @Override
    public Pattern<T> pattern() {
        return this;
    }

    @Override
    protected Result<T> previous() {
        return this;
    }

    @Override
    public int length() {
        return this.pattern.length();
    }

    @Override
    public T at(int index) {
        return this.pattern.at(index);
    }

    @Override
    public Iterator<T> iterator() {
        return this.pattern.iterator();
    }

    @Override
    public boolean get(int index) {
        return this.results[index + 1];
    }

    @Override
    public Result<T> match(T value) {
        this.results[0] = true;
        for (int i = 0; i < this.length(); i++) {
            if (this.symbols().classifier().classify(this.at(i)) == SymbolType.MULTIPLE_WILDCARD) {
                this.results[i + 1] = true;
            } else {
                break;
            }
        }
        return super.match(value);
    }

    @Override
    public String toString() {
        return this.pattern.toString();
    }
}
