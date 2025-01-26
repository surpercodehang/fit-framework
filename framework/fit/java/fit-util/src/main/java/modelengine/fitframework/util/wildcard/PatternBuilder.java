/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

/**
 * 为 {@link Pattern} 提供构建程序。
 *
 * @param <T> 表示模式匹配的元素的类型。
 * @author 梁济时
 * @since 2022-07-29
 */
public interface PatternBuilder<T> {
    /**
     * 设置包含模式信息的符号序。
     *
     * @param pattern 表示模式源的符号序的 {@link SymbolSequence}。
     * @return 表示当前构建程序的 {@link PatternBuilder}。
     */
    PatternBuilder<T> pattern(SymbolSequence<T> pattern);

    /**
     * 获取符号相关的配置程序。
     *
     * @return 表示符号相关的配置程序的 {@link SymbolConfigurator}。
     */
    SymbolConfigurator<T> symbol();

    /**
     * 构建模式的新实例。
     *
     * @return 表示新构建的模式实例的 {@link Pattern}。
     */
    Pattern<T> build();

    /**
     * 为 {@link PatternBuilder} 提供符号相关的配置。
     *
     * @param <T> 表示符号的类型。
     * @author 梁济时
     * @since 2022-07-29
     */
    interface SymbolConfigurator<T> {
        /**
         * 设置符号的分类程序。
         *
         * @param classifier 表示符号分类程序的 {@link SymbolClassifier}。
         * @return 表示当前构建程序的 {@link PatternBuilder}。
         */
        PatternBuilder<T> classifier(SymbolClassifier<T> classifier);

        /**
         * 设置符号的匹配程序。
         *
         * @param matcher 表示符号匹配程序的 {@link SymbolMatcher}。
         * @return 表示当前构建程序的 {@link PatternBuilder}。
         */
        PatternBuilder<T> matcher(SymbolMatcher<T> matcher);
    }
}
