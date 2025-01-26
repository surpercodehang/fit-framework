/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard.support;

import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.wildcard.PathPattern;
import modelengine.fitframework.util.wildcard.Pattern;
import modelengine.fitframework.util.wildcard.SymbolClassifier;
import modelengine.fitframework.util.wildcard.SymbolMatcher;
import modelengine.fitframework.util.wildcard.SymbolSequence;
import modelengine.fitframework.util.wildcard.SymbolType;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 表示 {@link PathPattern} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-12-21
 */
public class DefaultPathPattern extends DefaultPattern<String> implements PathPattern {
    private static final String MULTIPLE_WILDCARD = "**";
    private static final String SINGLE_WILDCARD = "*";

    private static final SymbolClassifier<String> DEFAULT_PATH_CLASSIFIER = symbol -> {
        if (Objects.equals(symbol, MULTIPLE_WILDCARD)) {
            return SymbolType.MULTIPLE_WILDCARD;
        } else if (Objects.equals(symbol, SINGLE_WILDCARD)) {
            return SymbolType.SINGLE_WILDCARD;
        } else {
            return SymbolType.NORMAL;
        }
    };
    private static final SymbolMatcher<String> DEFAULT_PATH_MATCHER =
            (pattern, value) -> Pattern.forCharSequence(pattern).matches(value);

    private final char pathSeparator;

    /**
     * 构造一个新的 {@link DefaultPathPattern} 实例。
     *
     * @param pattern 表示路径模式的 {@link String}。
     * @param pathSeparator 表示路径分隔符的 {@code char}。
     */
    public DefaultPathPattern(String pattern, char pathSeparator) {
        super(SymbolSequence.fromList(StringUtils.split(pattern,
                pathSeparator,
                ArrayList::new,
                StringUtils::isNotBlank)), DEFAULT_PATH_CLASSIFIER, DEFAULT_PATH_MATCHER);
        this.pathSeparator = pathSeparator;
    }

    @Override
    public boolean matches(String path) {
        return this.matches(SymbolSequence.fromList(StringUtils.split(path,
                this.pathSeparator,
                ArrayList::new,
                StringUtils::isNotBlank)));
    }
}
