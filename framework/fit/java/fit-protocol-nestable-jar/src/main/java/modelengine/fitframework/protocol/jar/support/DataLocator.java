/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import java.io.File;
import java.security.Permission;

/**
 * 为数据提供定位程序。
 *
 * @author 梁济时
 * @since 2023-02-21
 */
public interface DataLocator {
    /**
     * 获取数据所在的文件。
     *
     * @return 表示数据所在文件的 {@link File}。
     */
    File file();

    /**
     * 获取数据在文件中的偏移量。
     *
     * @return 表示数据在文件中偏移量的 64 位整数。
     */
    long offset();

    /**
     * 获取数据的长度。
     *
     * @return 表示数据长度的 64 位整数。
     */
    long length();

    /**
     * 获取对目标数据的访问权限。
     *
     * @return 表示访问权限的 {@link Permission}。
     */
    Permission permission();

    /**
     * 获取数据子块的定位程序。
     *
     * @param offset 表示数据子块在当前数据块中的偏移量的 64 位整数。
     * @param length 表示数据子块的长度的 64 位整数。
     * @return 表示数据子块的定位程序的 {@link DataLocator}。
     * @throws IllegalArgumentException {@code offset} 或 {@code length} 超出限制。
     */
    DataLocator sub(long offset, long length);

    /**
     * 获取表示指定文件完整数据的定位程序。
     *
     * @param file 表示数据所在文件的 {@link File}。
     * @return 表示该文件所有数据的定位程序的 {@link DataLocator}。
     * @throws IllegalArgumentException {@code file} 为 {@code null} 或不存在或不是一个常规文件。
     */
    static DataLocator of(File file) {
        return new DataLocators.Default(file);
    }
}
