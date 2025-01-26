/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.wildcard.SymbolClassifier;
import modelengine.fitframework.util.wildcard.SymbolType;

import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link SymbolClassifier} 提供默认实现。
 *
 * @param <T> 表示符号的类型。
 * @author 梁济时
 * @since 2022-08-04
 */
public class DefaultSymbolClassifier<T> implements SymbolClassifier<T> {
    private final T singleWildcard;
    private final T multipleWildcard;

    /**
     * 使用表示单符号和多符号的通配符初始化 {@link DefaultSymbolClassifier} 类的新实例。
     *
     * @param singleWildcard 表示单符号的通配符的 {@link Object}。
     * @param multipleWildcard 表示多符号的通配符的 {@link Object}。
     */
    public DefaultSymbolClassifier(T singleWildcard, T multipleWildcard) {
        this.singleWildcard = singleWildcard;
        this.multipleWildcard = multipleWildcard;
    }

    @Override
    public SymbolType classify(T item) {
        if (Objects.equals(this.singleWildcard, item)) {
            return SymbolType.SINGLE_WILDCARD;
        } else if (Objects.equals(this.multipleWildcard, item)) {
            return SymbolType.MULTIPLE_WILDCARD;
        } else {
            return SymbolType.NORMAL;
        }
    }

    @Override
    public int hashCode() {
        Object[] values = new Object[] {DefaultSymbolClassifier.class, this.singleWildcard, this.multipleWildcard};
        return Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DefaultSymbolClassifier) {
            DefaultSymbolClassifier<T> another = cast(obj);
            return Objects.equals(another.singleWildcard, this.singleWildcard)
                    && Objects.equals(another.multipleWildcard, this.multipleWildcard);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return StringUtils.format("[wildcards=[single={0}, multiple={1}]]",
                this.singleWildcard, this.multipleWildcard);
    }
}
