/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

import modelengine.fitframework.util.wildcard.support.DefaultCharSequencePattern;
import modelengine.fitframework.util.wildcard.support.DefaultPathPattern;
import modelengine.fitframework.util.wildcard.support.DefaultPatternBuilder;
import modelengine.fitframework.util.wildcard.support.DefaultVirtualFilePattern;

import java.io.File;
import java.util.List;
import java.util.function.Function;

/**
 * 为匹配提供模式定义。
 *
 * @param <S> 表示匹配元素的类型。
 * @author 梁济时
 * @since 2022-07-28
 */
public interface Pattern<S> extends SymbolSequence<S>, Result<S> {
    /**
     * 获取符号相关的配置。
     *
     * @return 表示符号相关配置的 {@link SymbolConfiguration}。
     */
    SymbolConfiguration<S> symbols();

    /**
     * 匹配指定元素序列。
     *
     * @param sequence 表示待匹配的元素序列的 {@link Iterable}。
     * @return 若可以成功匹配元素序列，则为 {@code true}；否则为 {@code false}。
     */
    default boolean matches(SymbolSequence<S> sequence) {
        Result<S> result = this;
        for (S value : sequence) {
            result = result.match(value);
        }
        return result.matched();
    }

    /**
     * 从符号树中匹配符合的值。
     *
     * @param tree 表示符号树的 {@link SymbolTree}。
     * @param <V> 表示值的类型。
     * @return 表示匹配到的符号的列表的 {@link List}。
     */
    default <V> List<V> match(SymbolTree<V, S> tree) {
        return Wildcards.match(this, tree);
    }

    /**
     * 从符号树中匹配符合的值。
     *
     * @param roots 表示根符号的列表的 {@link List}。
     * @param childrenMapper 表示用以通过符号获取子符号的方法的 {@link Function}。
     * @param symbolMapper 表示符号的获取方法的 {@link Function}。
     * @param <V> 表示值的类型。
     * @return 表示匹配到的符号的列表的 {@link List}。
     */
    default <V> List<V> match(List<V> roots, Function<V, List<V>> childrenMapper, Function<V, S> symbolMapper) {
        return this.match(SymbolTree.create(roots, childrenMapper, symbolMapper));
    }

    /**
     * 为模式提供符号相关的配置。
     *
     * @param <E> 表示符号的实际类型。
     * @author 梁济时
     * @since 2022-07-28
     */
    interface SymbolConfiguration<E> {
        /**
         * 获取符号的匹配程序。
         *
         * @return 表示符号匹配程序的 {@link SymbolMatcher}。
         */
        SymbolMatcher<E> matcher();

        /**
         * 获取符号的分类程序。
         *
         * @return 表示符号分类程序的 {@link SymbolClassifier}。
         */
        SymbolClassifier<E> classifier();
    }

    /**
     * 返回一个构建程序，用以构建模式对象的新实例。
     *
     * @param <T> 表示模式所匹配的元素的类型。
     * @return 表示模式对象的构建程序的 {@link PatternBuilder}。
     */
    static <T> PatternBuilder<T> custom() {
        return new DefaultPatternBuilder<>();
    }

    /**
     * 使用模式字符序、单字符通配符和多字符通配符创建字符序模式的新实例。
     * <p>将使用 {@code ?} 作为单字符通配符，并使用 {@code *} 作为多字符通配符。</p>
     *
     * @param pattern 表示模式字符序的 {@link CharSequencePattern}。
     * @return 表示用以匹配字符序的模式的 {@link CharSequencePattern}。
     */
    static CharSequencePattern forCharSequence(CharSequence pattern) {
        return forCharSequence(pattern, '?', '*');
    }

    /**
     * 使用模式字符序、单字符通配符和多字符通配符创建字符序模式的新实例。
     *
     * @param pattern 表示模式字符序的 {@link CharSequencePattern}。
     * @param singleWildcard 表示单字符通配符的字符的 {@code char}。
     * @param multipleWildcard 表示多字符通配符的字符的 {@code char}。
     * @return 表示用以匹配字符序的模式的 {@link CharSequencePattern}。
     */
    static CharSequencePattern forCharSequence(CharSequence pattern, char singleWildcard, char multipleWildcard) {
        return new DefaultCharSequencePattern(pattern, singleWildcard, multipleWildcard);
    }

    /**
     * 通过指定的路径样式和 {@link File#separatorChar} 来创建一个路径匹配模式。
     *
     * @param pattern 表示指定的路径样式的 {@link String}。
     * @return 表示创建的路径匹配模式的 {@link PathPattern}。
     * @see #forPath(String, char)
     */
    static PathPattern forPath(String pattern) {
        return forPath(pattern, File.separatorChar);
    }

    /**
     * 通过指定的路径样式和路径分隔符来创建一个路径匹配模式。
     * <p>路径样式匹配举例：（假设路径分隔符为 '/'）
     * <ul>
     *     <li>当路径样式为 {@code '/a'} 时，只有完全匹配的请求 {@code '/a'} 才能过滤；</li>
     *     <li>当路径样式为 {@code '/a?'} 时，可以模糊匹配 1 个字符，如 {@code '/aa'} 或 {@code '/ab'} 这样的请求；</li>
     *     <li>当路径样式为 {@code '/a*'} 时，可以在一段路径内模糊匹配任意个字符，如 {@code '/a'} 或 {@code '/abc'}
     *     这样的请求；</li>
     *     <li>当路径样式为 {@code '/a**'} 时，可以在任意段路径内模糊匹配任意个字符，如 {@code '/a'} 或 {@code '/a/b/c'}
     *     这样的请求。</li>
     * </ul></p>
     *
     * @param pattern 表示指定的路径样式的 {@link String}。
     * @param pathSeparator 表示路径分隔符的 {@code char}。
     * @return 表示创建的路径匹配模式的 {@link PathPattern}。
     */
    static PathPattern forPath(String pattern, char pathSeparator) {
        return new DefaultPathPattern(pattern, pathSeparator);
    }

    /**
     * 为虚拟文件提供匹配程序。
     *
     * @param pattern 表示路径的匹配模式的 {@link List}{@code <}{@link String}{@code >}。
     * @param multipleWildcard 表示用以匹配路径中连续多个名称的通配符的 {@link String}。
     * @param nameMatcher 表示名称的匹配模式的 {@link SymbolMatcher}{@code <}{@link String}{@code >}。
     * @return 表示虚拟文件的匹配模式的 {@link VirtualFilePattern}。
     */
    static VirtualFilePattern forVirtualFile(List<String> pattern, String multipleWildcard,
            SymbolMatcher<String> nameMatcher) {
        return new DefaultVirtualFilePattern(SymbolSequence.fromList(pattern), multipleWildcard, nameMatcher);
    }
}
