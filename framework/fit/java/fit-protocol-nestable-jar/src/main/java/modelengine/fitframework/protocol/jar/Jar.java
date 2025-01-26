/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Permission;
import java.util.Date;
import java.util.stream.Stream;

/**
 * 表示一个 JAR 包。
 *
 * @author 梁济时
 * @since 2022-09-13
 */
public interface Jar {
    /**
     * 表示 JAR 文件的扩展名。
     */
    String FILE_EXTENSION = ".jar";

    /**
     * 获取 JAR 所在的位置。
     *
     * @return 表示 JAR 的位置的 {@link JarLocation}。
     */
    JarLocation location();

    /**
     * 获取 JAR 的权限。
     *
     * @return 表示权限的 {@link Permission}。
     */
    Permission permission();

    /**
     * 获取归档件中记录的集合。
     *
     * @return 表示记录集合的 {@link EntryCollection}。
     */
    EntryCollection entries();

    /**
     * 获取 JAR 的备注信息。
     *
     * @return 表示备注信息的 {@link String}。
     */
    String comment();

    /**
     * 从文件中加载 JAR 信息。
     *
     * @param file 表示包含 JAR 信息的文件的 {@link File}。
     * @return 表示从文件中加载到的 JAR 的 {@link Jar}。
     * @throws IllegalArgumentException 当 {@code file} 为 {@code null} 时。
     * @throws IOException 当加载 JAR 过程中发生输入输出异常时。
     */
    static Jar from(File file) throws IOException {
        return from(JarLocation.custom().file(file).build());
    }

    /**
     * 从指定的位置加载 JAR 信息。
     *
     * @param location 表示 JAR 的位置的 {@link JarLocation}。
     * @return 表示从该位置加载到的 JAR 信息的 {@link Jar}。
     * @throws IllegalArgumentException 当 {@code location} 为 {@code null} 时。
     * @throws IllegalStateException 当加载 JAR 失败时。
     * @throws IOException 当加载 JAR 过程中发生输入输出异常时。
     */
    static Jar from(JarLocation location) throws IOException {
        return JarCache.instance().get(location);
    }

    /**
     * 从指定的位置加载 JAR 信息。
     *
     * @param url 表示 JAR 的位置的 {@link URL}。
     * @return 表示从该位置加载到的 JAR 信息的 {@link Jar}。
     * @throws IOException 当加载 JAR 过程中发生输入输出异常时。
     */
    static Jar from(URL url) throws IOException {
        return from(JarLocation.parse(url));
    }

    /**
     * 为 {@link Jar} 提供其所包含的条目。
     *
     * @author 梁济时
     * @since 2022-09-13
     */
    interface Entry {
        /**
         * 获取条目所在的 JAR。
         *
         * @return 表示所属 JAR 的 {@link Jar}。
         */
        Jar jar();

        /**
         * 获取条目的名称。
         *
         * @return 表示条目名称的 {@link String}。
         */
        String name();

        /**
         * 获取条目的压缩方法。
         *
         * @return 表示压缩方法的 {@link CompressionMethod}。
         */
        CompressionMethod methodOfCompression();

        /**
         * 获取条目内容的 CRC-32 校验码。
         *
         * @return 表示校验码的 32 位整数。
         */
        int crc32();

        /**
         * 获取条目对应的本地文件头在数据块中的偏移量。
         *
         * @return 表示偏移量的 64 位整数。
         */
        long offsetOfLocalHeader();

        /**
         * 表示条目压缩后的字节数。
         *
         * @return 表示压缩后的字节数的 64 位整数。
         */
        long sizeOfCompressed();

        /**
         * 获取条目压缩前的字节数。
         *
         * @return 表示压缩前的字节数的 64 位整数。
         */
        long sizeOfUncompressed();

        /**
         * 获取条目的上次修改时间。
         *
         * @return 表示上次修改时间的 {@link Date}。
         */
        Date timeOfLastModification();

        /**
         * 获取条目包含的额外数据。
         * <p>为了节省内存开销，这里直接返回额外数据的信息，因此外部不应该修改其内容，否则会导致原始的额外数据被破坏。</p>
         *
         * @return 表示额外数据的字节序。
         */
        byte[] extra();

        /**
         * 获取 JAR 中条目的备注信息。
         *
         * @return 表示备注信息的 {@link String}。
         */
        String comment();

        /**
         * 获取一个值，该值指示条目是否是一个目录。
         *
         * @return 若是一个目录，则为 {@code true}；否则为 {@code false}。
         */
        boolean directory();

        /**
         * 获取当前记录的位置。
         *
         * @return 表示记录位置的 {@link JarEntryLocation}。
         */
        JarEntryLocation location();

        /**
         * 获取一个输入流，用以读取条目中的内容。
         *
         * @return 表示用以读取条目内容的输入流的 {@link InputStream}。
         * @throws IOException 当读取记录过程中发生输入输出异常时。
         */
        InputStream read() throws IOException;

        /**
         * 获取当前条目所表示的嵌套的 JAR。
         *
         * @return 表示嵌套的 JAR 的 {@link Jar}。
         * @throws IOException 当前条目不能作为嵌套的 JAR 使用时。
         */
        Jar asJar() throws IOException;
    }

    /**
     * 为 {@link Entry} 提供集合。
     *
     * @author 梁济时
     * @since 2023-01-10
     */
    interface EntryCollection extends Iterable<Entry> {
        /**
         * 获取集合中包含记录的数量。
         *
         * @return 表示记录数量的 32 位整数。
         */
        int size();

        /**
         * 获取指定索引处的记录。
         *
         * @param index 表示待获取的记录所在位置的索引的 32 位整数。
         * @return 表示该索引处的记录的 {@link Entry}。
         * @throws IndexOutOfBoundsException 索引超出限制。
         */
        Entry get(int index);

        /**
         * 获取指定名称的记录。
         *
         * @param name 表示待获取的记录的名称的 {@link String}。
         * @return 若存在该名称的记录，则为表示该记录的 {@link Entry}，否则为 {@code null}。
         */
        Entry get(String name);

        /**
         * 返回一个操作流，用以操作所包含的所有条目。
         *
         * @return 表示 JAR 中条目的操作流的 {@link Stream}{@code <}{@link Entry}{@code >}。
         */
        Stream<Entry> stream();
    }
}
