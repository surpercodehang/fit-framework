/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.dispatch;

import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.server.HttpHandler;
import modelengine.fit.http.server.dispatch.support.DefaultMappingTree;
import modelengine.fitframework.util.StringUtils;

import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示 Http 请求路径的匹配树。
 * <p>当处理器为 {@link HttpHandler} 时，每一棵匹配树都代表一种特定的 Http 请求方法，比如
 * {@link HttpRequestMethod#GET}</p>
 *
 * @param <T> 表示匹配树上的处理器类型的 {@link T}。
 * @author 季聿阶
 * @since 2022-07-26
 */
public interface MappingTree<T> {
    /**
     * 向当前的匹配树中注册指定匹配路径样式的处理器。
     *
     * @param pathPattern 表示指定的匹配路径样式的 {@link String}。
     * @param handler 表示指定匹配路径样式的处理器的 {@link T}。
     * @return 当指定匹配路径样式已经存在处理器时，返回注册过的处理器的
     * {@link Optional}{@code <}{@link T}{@code >}，否则，返回 {@link Optional#empty()}。
     * @throws IllegalArgumentException 当 {@code pathPattern} 为 {@code null} 或空白字符串或不以 {@code '/'}
     * 开头或仅包含 {@code '/'} 时。
     * @throws IllegalArgumentException 当 {@code handler} 为 {@code null} 时。
     */
    Optional<T> register(String pathPattern, T handler);

    /**
     * 向当前的匹配树中取消注册指定匹配路径样式的处理器。
     *
     * @param pathPattern 表示指定的匹配路径样式的 {@link String}。
     */
    void unregister(String pathPattern);

    /**
     * 从当前的匹配树中搜索指定路径的处理器。
     *
     * @param path 表示指定路径的 {@link String}。
     * @return 表示搜索到的指定路径的处理器的 {@link Optional}{@code <}{@link T}{@code >}。
     * @throws IllegalArgumentException 当 {@code path} 为 {@code null} 或空白字符串或不以 {@code '/'}
     * 开头或仅包含 {@code '/'} 时。
     */
    Optional<T> search(String path);

    /**
     * 获取当前匹配树上所有的处理器。
     *
     * @return 表示处理器的 {@link List}{@code <}{@link T}{@code >}。
     */
    List<T> getAllHandlers();

    /**
     * 将标准的处理器路径转换成通配符格式的路径。
     *
     * @param pathPattern 表示待转换的处理器的标准路径样式的 {@link String}。
     * @return 表示转换后的带通配符的路径样式的 {@link String}。
     */
    static String convertToMatchedPathPattern(String pathPattern) {
        Deque<String> pathPatternFragments = MappingTree.splitPath(pathPattern);
        return pathPatternFragments.stream()
                .map(subPathPattern -> subPathPattern.replaceAll("\\{.*}", "*"))
                .collect(Collectors.joining(DefaultMappingTree.PATH_SEPARATOR,
                        DefaultMappingTree.PATH_SEPARATOR,
                        StringUtils.EMPTY));
    }

    /**
     * 将指定路径按照路径分隔符进行切分。
     *
     * @param path 表示待切分的完整路径的 {@link String}。
     * @return 表示切分后路径的 {@link Deque}{@code <}{@link String}{@code >}。
     * @throws IllegalArgumentException 当 {@code path} 为 {@code null} 时。
     */
    static Deque<String> splitPath(String path) {
        return DefaultMappingTree.splitPath(path);
    }
}
