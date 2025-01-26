/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.classpath;

import modelengine.fitframework.resource.classpath.support.UriClassPathKeyResolverComposite;
import modelengine.fitframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 为从 {@link URI} 中解析 {@link ClassPathKey} 提供解析程序。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
@FunctionalInterface
public interface UriClassPathKeyResolver {
    /**
     * 从 {@link URI} 中解析类路径的键。
     *
     * @param uri 表示类路径的 {@link URI}。
     * @return 若可以解析类路径，则为解析到的类路径的 {@link Optional}{@code <}{@link ClassPathKey}{@code >}；否则为
     * {@link Optional#empty()}。
     * @throws IOException 当解析过程发生输入输出异常时。
     */
    Optional<ClassPathKey> resolve(URI uri) throws IOException;

    /**
     * 组合两个类路径键的解析器。
     *
     * @param resolver1 表示待组合的第一个类路径键解析程序的 {@link UriClassPathKeyResolver}。
     * @param resolver2 表示待组合的第二个类路径键解析程序的 {@link UriClassPathKeyResolver}。
     * @return 表示组合后的类路径键的解析器的 {@link UriClassPathKeyResolver}。
     * @throws IllegalArgumentException 当 {@code resolver1} 或 {@code resolver2} 为 {@code null} 时。
     */
    static UriClassPathKeyResolver combine(UriClassPathKeyResolver resolver1, UriClassPathKeyResolver resolver2) {
        return ObjectUtils.combine(resolver1, resolver2, UriClassPathKeyResolverComposite::new);
    }

    /**
     * 将多个类路径键的解析程序组合成为一个类路径键的解析程序。
     *
     * @param resolvers 表示待组合的类路径键解析程序的 {@link Collection}{@code <}{@link UriClassPathKeyResolver}{@code >}。
     * @return 表示组合后的类路径键的解析程序的 {@link UriClassPathKeyResolver}。
     */
    static UriClassPathKeyResolver combine(Collection<UriClassPathKeyResolver> resolvers) {
        return Optional.ofNullable(resolvers)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .reduce(null, UriClassPathKeyResolver::combine);
    }

    /**
     * 将多个类路径键的解析程序组合成为一个类路径键的解析程序。
     *
     * @param resolvers 表示待组合的类路径键解析程序的 {@link UriClassPathKeyResolver}{@code []}。
     * @return 表示组合后的类路径键的解析程序的 {@link UriClassPathKeyResolver}。
     */
    static UriClassPathKeyResolver combine(UriClassPathKeyResolver... resolvers) {
        return combine(ObjectUtils.<UriClassPathKeyResolver[], Collection<UriClassPathKeyResolver>>mapIfNotNull(
                resolvers,
                Arrays::asList));
    }

    /**
     * 获取当前默认的类路径键的解析程序。
     *
     * @return 表示当前类路径解析程序的 {@link UriClassPathKeyResolver}。
     */
    static UriClassPathKeyResolver current() {
        return UriClassPathKeyResolvers.CURRENT;
    }
}
