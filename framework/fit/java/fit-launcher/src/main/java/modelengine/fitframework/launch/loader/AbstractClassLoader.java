/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.launch.loader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为类加载程序提供基类。
 *
 * @author 梁济时
 * @since 2022-09-01
 */
public abstract class AbstractClassLoader extends URLClassLoader {
    /**
     * 表示 JAR 文件的扩展名。
     */
    private static final String JAR_FILE_EXTENSION = ".jar";

    /**
     * 使用类加载程序所包含 JAR 的 URL 及上级类加载程序初始化 {@link AbstractClassLoader} 类的新实例。
     *
     * @param urls 表示包含的 JAR 的 URL 的 {@link URL}{@code []}。
     * @param parent 表示上级父加载程序的 {@link ClassLoader}。
     */
    public AbstractClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * 在指定目录中收集所有 JAR 的 URL。
     *
     * @param directories 表示待查找的目录的 {@link File}{@code []}。
     * @return 表示从目录中找到的所有 JAR 的 {@link URL}{@code []}。
     */
    protected static URL[] jars(File... directories) {
        if (directories == null) {
            return new URL[0];
        }
        Queue<File> queue =
                Stream.of(directories).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedList::new));
        List<File> jars = new LinkedList<>();
        while (!queue.isEmpty()) {
            File current = queue.poll();
            if (current.isDirectory()) {
                Optional.ofNullable(current.listFiles()).map(Arrays::asList).ifPresent(queue::addAll);
                continue;
            }
            if (jar(current)) {
                jars.add(current);
            }
        }
        return jars.stream().map(AbstractClassLoader::url).toArray(URL[]::new);
    }

    /**
     * 检查指定文件是否是一个JAR文件。
     * <p>当前仅检查文件扩展名。</p>
     *
     * @param file 表示待检查的文件的 {@link File}。
     * @return 若是JAR文件，则为 {@code true}；否则为 {@code false}。
     */
    private static boolean jar(File file) {
        String name = file.getName();
        if (name.length() < JAR_FILE_EXTENSION.length()) {
            return false;
        }
        String suffix = name.substring(name.length() - JAR_FILE_EXTENSION.length());
        return suffix.equalsIgnoreCase(JAR_FILE_EXTENSION);
    }

    /**
     * 将指定的文件转为 URL。
     *
     * @param file 表示待转为URL的文件的 {@link File}。
     * @return 表示文件的URL表现形式的 {@link URL}。
     */
    private static URL url(File file) {
        try {
            return file.getCanonicalFile().toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Invalid path of JAR. [path=%s]",
                    path(file)));
        } catch (IOException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "The file is not canonical. [path=%s]",
                    path(file)));
        }
    }

    /**
     * 获取指定文件的标准化路径。
     *
     * @param file 表示待获取路径的文件的 {@link File}。
     * @return 表示文件的路径的 {@link String}。
     * @throws IllegalStateException 当标准化失败时。
     */
    private static String path(File file) {
        if (file == null) {
            return null;
        }

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to canonicalize file. [file=%s]", file.getPath()),
                    e);
        }
    }
}
