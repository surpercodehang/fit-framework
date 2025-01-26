/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 为文件系统提供工具方法。
 *
 * @author 梁济时
 * @since 2020-11-26
 */
public class FileSystemUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private FileSystemUtils() {}

    /**
     * 标准化指定文件。
     *
     * @param file 表示待标准化的文件的 {@link File}。
     * @return 表示标准化后的文件的 {@link File}。
     * @throws IllegalStateException 标准化过程发生异常。
     */
    public static File canonicalize(File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 获取文件的标准化路径。
     *
     * @param file 表示待获取路径的文件的 {@link File}。
     * @return 表示文件标准化路径的 {@link String}。
     * @throws IllegalStateException 获取标准化路径过程发生异常。
     */
    public static String path(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 获取指定路径的子路径。
     *
     * @param parent 表示原始路径的 {@link File}。
     * @param path 表示子路径的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示子路径的 {@link File}。
     */
    public static File child(File parent, List<String> path) {
        File ptr = parent;
        for (String item : path) {
            ptr = new File(ptr, item);
        }
        return ptr;
    }
}
