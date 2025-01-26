/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io.virtualization;

import modelengine.fitframework.io.virtualization.local.LocalVirtualDirectory;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;

/**
 * 为虚拟文件系统提供虚拟目录。
 *
 * @author 梁济时
 * @since 2022-05-30
 */
public interface VirtualDirectory extends VirtualFileSystemElement {
    /**
     * 获取父目录。
     *
     * @return 表示父目录的 {@link VirtualDirectory}。
     */
    VirtualDirectory parent();

    /**
     * 获取所包含的子目录的集合。
     *
     * @return 表示子目录集合的 {@link Collection}{@code <}{@link VirtualDirectory}{@code >}。
     */
    Collection<VirtualDirectory> children();

    /**
     * 获取指定名称的子目录。
     *
     * @param name 表示子目录名称的 {@link String}。
     * @return 若子目录存在，则为表示子目录的 {@link Optional}{@code <}{@link VirtualDirectory}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<VirtualDirectory> child(String name);

    /**
     * 获取指定路径的子目录。
     *
     * @param path 表示子目录的路径的 {@link String}{@code []}。
     * @return 若子目录存在，则为表示子目录的 {@link Optional}{@code <}{@link VirtualDirectory}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<VirtualDirectory> child(String... path);

    /**
     * 获取所包含的文件的集合。
     *
     * @return 表示文件集合的 {@link Collection}{@code <}{@link VirtualFile}{@code >}。
     */
    Collection<VirtualFile> files();

    /**
     * 获取所包含的指定名称的文件。
     *
     * @param name 表示文件名称的 {@link String}。
     * @return 若文件存在，则为表示文件的 {@link Optional}{@code <}{@link VirtualFile}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<VirtualFile> file(String name);

    /**
     * 获取指定路径的文件。
     *
     * @param path 表示文件路径的 {@link String}{@code []}。
     * @return 若文件存在，则为表示文件的 {@link Optional}{@code <}{@link VirtualFile}{@code >}；否则为
     * {@link Optional#empty()}。
     */
    Optional<VirtualFile> file(String... path);

    /**
     * 使用文件系统中的目录实例创建虚拟目录实例。
     *
     * @param directory 表示文件系统目录实例的 {@link File}。
     * @return 表示新创建的虚拟目录实例的 {@link VirtualDirectory}。
     * @throws IllegalArgumentException {@code directory} 为 {@code null}，或不存在或不是一个目录。
     */
    static VirtualDirectory of(File directory) {
        return new LocalVirtualDirectory(directory);
    }

    /**
     * 获取虚拟文件系统的 URL 地址。
     *
     * @return 表示虚拟文件系统的 URL 地址的 {@link URL}。
     */
    URL url();
}
