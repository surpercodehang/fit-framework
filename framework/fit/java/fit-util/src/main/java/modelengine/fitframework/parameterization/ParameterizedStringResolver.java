/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.parameterization;

import modelengine.fitframework.parameterization.support.DefaultParameterizedStringResolver;

/**
 * 为应用程序提供字符串变量解析能力。
 * <p>从字符串中解析具备指定前缀和后缀的变量，并通过指定的参数映射对变量进行替换，以得到最终结果。</p>
 * <p>例如，对于字符串 {@code "My name is ${name}."}，变量以 {@code "$\u007b"} 为 {@link
 * ParameterizedStringResolver#getParameterPrefix() 前缀}，以 {@code "\u007d"} 为 {@link
 * ParameterizedStringResolver#getParameterSuffix() 后缀}，并提供变量 {@code "name"} 的值为 {@code "梁济时"}，则：</p>
 * <pre>
 *     VariableResolver resolver = VariableResolver.create("${", "}", '/'); // 得到一个解析器
 *     String source = "My name is ${name}."; // 表示原始字符串
 *     Map&lt;String, String&gt; params = MapBuilder.get().put("name", "梁济时").build(); // 参数映射
 *     VariableResolvingResult result = resolver.resolve(source, params); // 解析字符串
 *     String resolved = result.getResolvedString(); // 得到解析后的字符串
 * </pre>
 * <p>为了避免前后缀中字符不可被使用的问题，可以通过{@link ParameterizedStringResolver#getEscapeCharacter() 转义字符}
 * 来强制使关键字符失效。</p>
 * <p>一般来讲，对于某个业务逻辑而言，其所使用的前缀、后缀及转义字符通常保持不变，因此可将相应的 {@link ParameterizedStringResolver}
 * 实例作为静态变量，以降低内存开销。</p>
 *
 * @author 梁济时
 * @see ParameterizedString
 * @see ResolvedParameter
 * @since 1.0
 */
public interface ParameterizedStringResolver {
    /**
     * 获取参数前缀。
     *
     * @return 表示参数前缀的 {@link String}。
     */
    String getParameterPrefix();

    /**
     * 获取参数后缀。
     *
     * @return 表示参数的后缀的 {@link String}。
     */
    String getParameterSuffix();

    /**
     * 获取转义字符。
     *
     * @return 表示转义字符。
     */
    char getEscapeCharacter();

    /**
     * 从字符串中解析变量。
     *
     * @param originalString 表示待解析变量的字符串的 {@link String}。
     * @return 表示变量的解析结果的 {@link ResolvedParameter}。
     * @throws IllegalArgumentException {@code originalString} 为 {@code null}。
     * @throws IllegalStateException 解析字符串失败。
     */
    ParameterizedString resolve(String originalString);

    /**
     * 使用变量的前缀、后缀及转义字符实例化一个 {@link ParameterizedStringResolver} 的默认实现，采用严格校验模式。
     *
     * @param prefix 表示变量的前缀的 {@link String}。
     * @param suffix 表示变量的后缀的 {@link String}。
     * @param escapeCharacter 表示转义字符的 {@code char}。
     * @return 表示新实例化的解析工具的 {@link ParameterizedStringResolver}。
     * @throws IllegalArgumentException {@code prefix} 为 {@code null} 或空字符串。
     * @throws IllegalArgumentException {@code suffix} 为 {@code null} 或空字符串。
     * @throws IllegalArgumentException {@code prefix} 中包含 {@code escapeCharacter}。
     * @throws IllegalArgumentException {@code suffix} 中包含 {@code escapeCharacter}。
     */
    static ParameterizedStringResolver create(String prefix, String suffix, char escapeCharacter) {
        return new DefaultParameterizedStringResolver(prefix, suffix, escapeCharacter, true);
    }

    /**
     * 使用变量的前缀、后缀及转义字符实例化一个 {@link ParameterizedStringResolver} 的默认实现。
     *
     * @param prefix 表示变量的前缀的 {@link String}。
     * @param suffix 表示变量的后缀的 {@link String}。
     * @param escapeCharacter 表示转义字符的 {@code char}。
     * @param isStrict 表示是否采用严格校验模式的 {@code boolean}。
     * @return 表示新实例化的解析工具的 {@link ParameterizedStringResolver}。
     * @throws IllegalArgumentException {@code prefix} 为 {@code null} 或空字符串。
     * @throws IllegalArgumentException {@code suffix} 为 {@code null} 或空字符串。
     * @throws IllegalArgumentException {@code prefix} 中包含 {@code escapeCharacter}。
     * @throws IllegalArgumentException {@code suffix} 中包含 {@code escapeCharacter}。
     */
    static ParameterizedStringResolver create(String prefix, String suffix, char escapeCharacter, boolean isStrict) {
        return new DefaultParameterizedStringResolver(prefix, suffix, escapeCharacter, isStrict);
    }
}
