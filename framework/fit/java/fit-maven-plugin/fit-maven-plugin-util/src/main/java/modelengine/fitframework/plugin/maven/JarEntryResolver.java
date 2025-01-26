/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import modelengine.fitframework.plugin.maven.exception.FitMavenPluginException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 解析 jar 包的解析器。
 *
 * @param <T> 表示解析结果类型的 {@link T}。
 * @author 梁济时
 * @author 季聿阶
 * @since 2021-06-29
 */
public interface JarEntryResolver<T> {
    /**
     * 判断 jar 包内的指定文件是否满足指定要求。
     *
     * @param entry 表示 jar 包内的指定文件的 {@link JarEntry}。
     * @return 表示判断结果的 {@code boolean}。
     */
    boolean is(JarEntry entry);

    /**
     * 按照指定规则进行文件流的解析。
     *
     * @param in 表示待解析的文件的输入流的 {@link InputStream}。
     * @return 表示解析结果的 {@link T}。
     */
    T resolve(InputStream in);

    /**
     * 给定指定的解析器列表，对指定 jar 文件进行解析，找到第一个解析成功的结果。
     *
     * @param jarFile 表示待解析的 jar 文件的 {@link File}。
     * @param resolvers 表示指定的解析器列表的 {@link Supplier}{@code <}{@link List}
     * {@code <}{@link JarEntryResolver}{@code <}{@link T}{@code >>>}。
     * @param <T> 表示解析器的解析结果类型的 {@link T}。
     * @return 表示解析结果的 {@link Optional}{@code <}{@link T}{@code >}。
     */
    static <T> Optional<T> resolve(File jarFile, Supplier<List<JarEntryResolver<T>>> resolvers) {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            Optional<T> value = Optional.empty();
            while (!value.isPresent() && entries.hasMoreElements()) {
                value = JarEntryResolver.resolve(jar, entries.nextElement(), resolvers.get());
            }
            return value;
        } catch (IOException e) {
            throw new FitMavenPluginException(String.format(Locale.ROOT,
                    "Fail to resolve file as a JAR. [file=%s]",
                    jarFile.getName()), e);
        }
    }

    /**
     * 给定指定的解析器列表，对指定 jar 文件进行解析，找到第一个解析成功的结果。
     *
     * @param file 表示待解析的 jar 文件的 {@link File}。
     * @param entry 表示待解析的 jar 包内的指定文件的 {@link JarFile}。
     * @param resolvers 表示指定的解析器列表的 {@link List}{@code <}
     * {@link JarEntryResolver}{@code <}{@link T}{@code >>}。
     * @param <T> 表示解析器的解析结果类型的 {@link T}。
     * @return 表示解析结果的 {@link Optional}{@code <}{@link T}{@code >}。
     */
    static <T> Optional<T> resolve(JarFile file, JarEntry entry, List<JarEntryResolver<T>> resolvers) {
        return resolvers.stream()
                .map(resolver -> resolve(file, entry, resolver))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    /**
     * 给定指定的解析器列表，对指定 jar 文件进行解析，找到第一个解析成功的结果。
     *
     * @param file 表示待解析的 jar 文件的 {@link File}。
     * @param entry 表示待解析的 jar 包内的指定文件的 {@link JarFile}。
     * @param resolver 表示指定的解析器的 {@link JarEntryResolver}{@code <}{@link T}{@code >}。
     * @param <T> 表示解析器的解析结果类型的 {@link T}。
     * @return 表示解析结果的 {@link Optional}{@code <}{@link T}{@code >}。
     */
    static <T> Optional<T> resolve(JarFile file, JarEntry entry, JarEntryResolver<T> resolver) {
        if (resolver.is(entry)) {
            try (InputStream in = file.getInputStream(entry)) {
                return Optional.ofNullable(resolver.resolve(in));
            } catch (IOException e) {
                throw new FitMavenPluginException(String.format(Locale.ROOT,
                        "Fail to resolve JAR entry. [jar=%s, entry=%s]",
                        file.getName(),
                        entry.getName()), e);
            }
        }
        return Optional.empty();
    }
}
