/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.resource.support.ResourceTrees;
import modelengine.fitframework.util.wildcard.Pattern;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 为资源提供树形结构定义。
 *
 * @author 梁济时
 * @since 2023-01-29
 */
public interface ResourceTree {
    /**
     * 获取资源树所在的位置。
     *
     * @return 表示资源树所在位置的 {@link URL}。
     * @throws MalformedURLException 当 URL 的格式不正确时。
     */
    URL location() throws MalformedURLException;

    /**
     * 获取资源树中包含的根节点的集合。
     *
     * @return 表示根节点集合的 {@link NodeCollection}。
     */
    NodeCollection roots();

    /**
     * 遍历所有文件。
     *
     * @param consumer 表示文件的消费程序的 {@link Consumer}{@code <}{@link FileNode}{@code >}。
     * @throws IllegalArgumentException {@code consumer} 为 {@code null}。
     */
    void traverse(Consumer<FileNode> consumer);

    /**
     * 遍历所有文件。
     *
     * @param filter 表示文件的筛选程序的 {@link Predicate}{@code <}{@link FileNode}{@code >}。
     * @param consumer 表示文件的消费程序的 {@link Consumer}{@code <}{@link FileNode}{@code >}。
     * @throws IllegalArgumentException {@code consumer} 为 {@code null}。
     */
    void traverse(Predicate<FileNode> filter, Consumer<FileNode> consumer);

    /**
     * 获取指定路径的节点。
     * <p>以 {@code /} 作为分割符。</p>
     *
     * @param path 表示节点的路径的 {@link String}。
     * @return 若存在该路径的节点，则为表示该节点的 {@link Node}，否则为 {@code null}。
     * @throws IllegalArgumentException path 为 {@code null}。
     */
    @Nullable
    Node nodeAt(String path);

    /**
     * 在资源树中匹配所有符合条件的文件节点。
     *
     * @param pattern 表示匹配的样式的 {@link Pattern}。
     * @return 表示符合样式的文件节点的列表的 {@link List}{@code <}{@link FileNode}{@code >}。
     * @throws IllegalArgumentException {@code pattern} 为 {@code null}。
     */
    List<FileNode> match(Pattern<String> pattern);

    /**
     * 为资源树提供节点定义。
     *
     * @author 梁济时
     * @since 2023-01-29
     */
    interface Node {
        /**
         * 获取节点所在的资源树。
         *
         * @return 表示所在资源树的 {@link ResourceTree}。
         */
        @Nonnull
        ResourceTree tree();

        /**
         * 获取节点的名称。
         *
         * @return 表示节点名称的 {@link String}。
         */
        String name();

        /**
         * 获取节点的路径。
         *
         * @return 表示节点路径的 {@link String}。
         */
        String path();
    }

    /**
     * 为资源树中的节点提供集合。
     *
     * @author 梁济时
     * @since 2023-01-29
     */
    interface NodeCollection extends Iterable<Node> {
        /**
         * 统计集合中节点的数量。
         *
         * @return 表示节点数量的 32 位整数。
         */
        int count();

        /**
         * 获取指定名称的节点。
         *
         * @param name 表示节点名称的 {@link String}。
         * @return 若存在该名称的节点，则为表示该节点的 {@link Node}，否则为 {@code null}。
         */
        Node get(String name);

        /**
         * 获取指定索引处的节点。
         *
         * @param index 表示节点所在索引的 32 位整数。
         * @return 表示该索引处的节点的 {@link Node}。
         * @throws IndexOutOfBoundsException 索引超出限制。
         */
        Node get(int index);

        /**
         * 返回一个操作流，用以操作集合中的所有节点。
         *
         * @return 表示用以操作集合中节点的操作流的 {@link Stream}{@code <}{@link Node}{@code >}。
         */
        Stream<Node> stream();

        /**
         * 返回一个列表，包含集合中的所有节点。
         *
         * @return 表示包含集合中所有节点的列表的 {@link List}{@code <}{@link Node}{@code >}。
         */
        List<Node> toList();
    }

    /**
     * 为资源树提供目录节点。
     *
     * @author 梁济时
     * @since 2023-01-29
     */
    interface DirectoryNode extends Node {
        /**
         * 获取当前目录的父目录。
         *
         * @return 表示父目录的 {@link DirectoryNode}。
         */
        @Nullable
        DirectoryNode parent();

        /**
         * 表示包含的子节点的集合。
         *
         * @return 表示包含子节点的集合的 {@link NodeCollection}。
         */
        NodeCollection children();

        /**
         * 遍历目录下所有的文件。
         *
         * @param consumer 表示文件的消费程序的 {@link Consumer}{@code <}{@link FileNode}{@code >}。
         * @throws IllegalArgumentException {@code consumer} 为 {@code null}。
         */
        void traverse(Consumer<FileNode> consumer);

        /**
         * 遍历目录下所有符合条件的文件。
         *
         * @param filter 表示文件的筛选条件的 {@link Predicate}{@code <}{@link FileNode}{@code >}。
         * @param consumer 表示文件的消费程序的 {@link Consumer}{@code <}{@link FileNode}{@code >}。
         * @throws IllegalArgumentException {@code consumer} 为 {@code null}。
         */
        void traverse(Predicate<FileNode> filter, Consumer<FileNode> consumer);
    }

    /**
     * 为资源树提供文件节点。
     *
     * @author 梁济时
     * @since 2023-01-29
     */
    interface FileNode extends Node, Resource {
        /**
         * 获取节点所在的目录。
         *
         * @return 若存在于目录中，则为其所在目录的 {@link DirectoryNode}，否则为 {@code null}。
         */
        @Nullable
        DirectoryNode directory();
    }

    /**
     * 从资源所在的位置生成资源树。
     * <p>支持 {@code file} 和 {@code jar} 协议。</p>
     *
     * @param location 表示资源所在位置的 {@link URL}。
     * @return 表示该资源的资源情况的资源树的 {@link ResourceTree}。
     * @throws IllegalArgumentException {@code location} 为 {@code null}。
     * @throws IllegalStateException 未能从指定位置加载资源树。
     */
    static ResourceTree of(URL location) {
        return ResourceTrees.of(location);
    }

    /**
     * 从指定的 JAR 生成资源树。
     *
     * @param jar 表示待生成资源树的 JAR 的 {@link Jar}。
     * @return 表示 JAR 中资源的资源树的 {@link ResourceTree}。
     */
    static ResourceTree of(Jar jar) {
        return ResourceTrees.of(jar);
    }

    /**
     * 从指定的目录生成资源树。
     *
     * @param directory 表示资源树的根目录的 {@link File}。
     * @return 表示以该为目录作为根目录的资源树的 {@link ResourceTree}。
     */
    static ResourceTree of(File directory) {
        return ResourceTrees.of(directory);
    }
}
