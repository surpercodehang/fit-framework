/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.location;

import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarLocation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;

/**
 * 为归档件和归档记录的位置信息提供工具方法。
 *
 * @author 梁济时
 * @since 2023-02-20
 */
public final class Locations {
    private static final int STRING_INITIAL_CAPACITY = 128;
    private static final String JAR_PREFIX = JarLocation.JAR_PROTOCOL + JarLocation.PROTOCOL_SEPARATOR;

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Locations() {}

    static String toString(File file, List<String> nests, String entry) {
        String url;
        try {
            url = file.toURI().toURL().toExternalForm();
        } catch (MalformedURLException ex) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to obtain URL of file. [file=%s]",
                    path(file)), ex);
        }
        if (nests.isEmpty() && entry == null) {
            return url;
        }
        StringBuilder builder = new StringBuilder(STRING_INITIAL_CAPACITY);
        builder.append(JAR_PREFIX).append(url);
        for (String nest : nests) {
            appendUrlSeparator(builder).append(nest);
        }
        appendUrlSeparator(builder);
        if (entry != null) {
            builder.append(entry);
        }
        return builder.toString();
    }

    private static StringBuilder appendUrlSeparator(StringBuilder builder) {
        if (builder.charAt(builder.length() - 1) != JarEntryLocation.ENTRY_PATH_SEPARATOR) {
            builder.append(JarLocation.URL_PATH_SEPARATOR);
        }
        return builder;
    }

    /**
     * 表示归档件和归档记录的工厂。
     *
     * @param <T> 表示归档件或归档记录的类型的 {@link T}。
     */
    @FunctionalInterface
    interface Factory<T> {
        /**
         * 创建归档件或归档记录。
         *
         * @param file 表示所属文件的 {@link File}。
         * @param nests 表示嵌套路径的 {@link List}{@code <}{@link String}{@code >}。
         * @param entry 表示所属的入口地址的 {@link String}。
         * @return 表示创建的归档件或归档记录的 {@link T}。
         */
        T create(File file, List<String> nests, String entry);
    }

    /**
     * 按照指定的归档件位置创建一个构建器。
     *
     * @param location 表示指定的归档件位置的 {@link JarLocation}。
     * @return 表示创建的归档件位置的构建器的 {@link JarLocation.Builder}。
     */
    public static JarLocation.Builder createBuilderForJarLocation(JarLocation location) {
        return new DefaultJarLocation.Builder(location);
    }

    /**
     * 按照指定的归档记录位置创建一个构建器。
     *
     * @param location 表示指定的归档记录位置的 {@link JarEntryLocation}。
     * @return 表示创建的归档记录位置的构建器的 {@link JarEntryLocation.Builder}。
     */
    public static JarEntryLocation.Builder createBuilderForJarEntryLocation(JarEntryLocation location) {
        return new DefaultJarEntryLocation.Builder(location);
    }

    /**
     * 获取指定文件的标准化路径。
     *
     * @param file 表示待获取路径的文件的 {@link File}。
     * @return 表示文件的路径的 {@link String}。
     * @throws IllegalStateException 当标准化失败时。
     */
    public static String path(File file) {
        if (file == null) {
            return null;
        }
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to canonicalize file. [file=%s]", file.getPath()), e);
        }
    }
}
