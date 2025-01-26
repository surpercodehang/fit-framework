/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io.virtualization;

import modelengine.fitframework.io.virtualization.local.LocalVirtualFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 为虚拟文件系统提供虚拟机文件。
 *
 * @author 梁济时
 * @since 2022-05-30
 */
public interface VirtualFile extends VirtualFileSystemElement {
    /**
     * 获取文件所属的虚拟目录。
     *
     * @return 表示虚拟目录的 {@link VirtualDirectory}。
     */
    VirtualDirectory directory();

    /**
     * 打开文件以读取内容。
     *
     * @return 表示用以读取文件内容的输入流的 {@link InputStream}。
     * @throws IOException 打开文件过程发生输入输出异常。s
     */
    InputStream openRead() throws IOException;

    /**
     * 将当前虚拟文件转为URL。
     *
     * @return 表示文件位置的 {@link URL}。
     */
    URL url();

    /**
     * 使用文件系统中的文件实例创建虚拟文件的新实例。
     *
     * @param file 表示文件系统中的文件实例的 {@link File}。
     * @return 表示新创建的虚拟文件实例的 {@link VirtualFile}。
     * @throws IllegalArgumentException {@code file} 为 {@code null} 或不存在或不是一个文件。
     */
    static VirtualFile of(File file) {
        return new LocalVirtualFile(file);
    }
}
